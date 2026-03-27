package com.app.cms.automation.service;

import com.app.cms.automation.dto.WorkflowActionDto;
import com.app.cms.automation.dto.WorkflowDto;
import com.app.cms.automation.dto.WorkflowStatsDto;
import com.app.cms.automation.entity.*;
import com.app.cms.automation.repository.WorkflowActionRepository;
import com.app.cms.automation.repository.WorkflowExecutionRepository;
import com.app.cms.automation.repository.WorkflowLogRepository;
import com.app.cms.automation.repository.WorkflowRepository;
import com.app.cms.campaign.entity.CampaignContact;
import com.app.cms.campaign.entity.MessageStatus;
import com.app.cms.campaign.events.CampaignSentEvent;
import com.app.cms.campaign.service.CampaignService;
import com.app.cms.common.security.OrganizationContext;
import com.app.cms.contact.entity.Contact;
import com.app.cms.contact.service.ContactService;
import com.app.cms.contact.event.ContactCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowActionRepository actionRepository;
    private final WorkflowExecutionRepository executionRepository;
    private final WorkflowLogRepository logRepository;
    private final WorkflowExecutionService executionService;
    private final ContactService contactService;
    private final ObjectMapper objectMapper;
    private final CampaignService campaignService;

    @Transactional
    public Workflow createWorkflow(WorkflowDto dto) {
        log.info("Creating workflow: {}", dto.getName());

        Long organizationId = OrganizationContext.getOrganizationId();

        if (workflowRepository.existsByNameAndOrganizationId(dto.getName(),organizationId)) {
            throw new IllegalArgumentException("Workflow name already exists: " + dto.getName());
        }

        Workflow workflow = new Workflow();
        workflow.setOrganizationId(organizationId);
        workflow.setName(dto.getName());
        workflow.setDescription(dto.getDescription());
        workflow.setTriggerType(dto.getTriggerType());
        workflow.setTriggerParams(dto.getTriggerParams());
        workflow.setStatus(WorkflowStatus.DRAFT);

        Workflow saved = workflowRepository.save(workflow);

        for (WorkflowActionDto actionDto : dto.getActions()) {
            WorkflowAction action = new WorkflowAction();
            action.setWorkflow(saved);
            action.setType(actionDto.getType());
            action.setOrderIndex(actionDto.getOrderIndex());
            action.setDelayHours(actionDto.getDelayHours());
            action.setActionParams(actionDto.getActionParams());

            actionRepository.save(action);
        }

        log.info("Workflow created with {} actions", dto.getActions().size());
        return saved;
    }

    @Transactional
    public Workflow updateWorkflow(Long id, WorkflowDto dto) {
        log.info("Updating workflow: {}", id);
        Workflow workflow = getWorkflow(id);

        workflow.setName(dto.getName());
        workflow.setDescription(dto.getDescription());
        workflow.setTriggerType(dto.getTriggerType());
        workflow.setTriggerParams(dto.getTriggerParams());

        actionRepository.deleteByWorkflowId(id);
        
        for (WorkflowActionDto actionDto : dto.getActions()) {
            WorkflowAction action = new WorkflowAction();
            action.setWorkflow(workflow);
            action.setType(actionDto.getType());
            action.setOrderIndex(actionDto.getOrderIndex());
            action.setDelayHours(actionDto.getDelayHours());
            action.setActionParams(actionDto.getActionParams());
            actionRepository.save(action);
        }
        
        return workflowRepository.save(workflow);
    }

    public Workflow getWorkflow(Long id) {

        Long organizationId = OrganizationContext.getOrganizationId();

        return workflowRepository.findByIdAndOrganizationId(id,organizationId)
                .orElseThrow(() -> new RuntimeException("Workflow not found: " + id));
    }

    public Page<Workflow> getAllWorkflows(Pageable pageable) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return workflowRepository.findByOrganizationId(organizationId, pageable);
    }

    public Page<Workflow> getActiveWorkflows(Pageable pageable) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return workflowRepository.findByOrganizationIdAndStatus(organizationId, WorkflowStatus.ACTIVE, pageable);
    }


    public Workflow activateWorkflow(Long id) {
        Workflow workflow = getWorkflow(id);
        if(workflow.isActive()){{
            log.error("Workflow '{}' is Already Active", workflow.getName());
            throw new RuntimeException("Workflow is Already Active "+workflow.getName());
        }}
        workflow.setStatus(WorkflowStatus.ACTIVE);
        log.info("Workflow '{}' activated", workflow.getName());
        return workflowRepository.save(workflow);
    }

    public Workflow deactivateWorkflow(Long id) {
        Workflow workflow = getWorkflow(id);
        if(!workflow.isActive()){{
            log.error("Workflow '{}' is Already Deactivated", workflow.getName());
            throw new RuntimeException("Workflow is Already deactivated "+workflow.getName());
        }}
        workflow.setStatus(WorkflowStatus.INACTIVE);
        log.info("Workflow '{}' deactivated", workflow.getName());
        return workflowRepository.save(workflow);
    }

    public void deleteWorkflow(Long id) {
        Workflow workflow = getWorkflow(id);
        workflowRepository.delete(workflow);

        log.info("Workflow '{}' deleted", workflow.getName());
    }

    public Page<WorkflowExecution> getWorkflowExecutions(Long workflowId, Pageable pageable) {
        getWorkflow(workflowId);
        return executionRepository.findByWorkflowId(workflowId, pageable);
    }

    public Page<WorkflowLog> getExecutionLogs(Long executionId, Pageable pageable) {
        return logRepository.findByExecutionIdOrderByExecutedAtAsc(executionId, pageable);
    }


    public WorkflowStatsDto getWorkflowStats(Long workflowId) {
        Long organizationId = OrganizationContext.getOrganizationId();
        Workflow workflow = getWorkflow(workflowId);

        int activeExecutions = (int) executionRepository.findByWorkflowId(workflowId)
                .stream()
                .filter(e -> e.getStatus() == ExecutionStatus.RUNNING ||
                        e.getStatus() == ExecutionStatus.WAITING)
                .count();

        return new WorkflowStatsDto(
                workflow.getId(),
                workflow.getName(),
                workflow.getStatus(),
                workflow.getTotalExecutions(),
                workflow.getSuccessfulExecutions(),
                workflow.getFailedExecutions(),
                workflow.getSuccessRate(),
                activeExecutions,
                workflow.getCreatedAt()
        );
    }

    @ApplicationModuleListener
    public void onContactCreated(ContactCreatedEvent event) {
        log.debug("Checking workflows for CONTACT_CREATED trigger");

        Contact contact = contactService.getContact(event.contactId());
        Long organizationId = contact.getOrganizationId();

        List<Workflow> workflows = workflowRepository
                .findActiveTriggers(organizationId, WorkflowTriggerType.CONTACT_CREATED);

        if (workflows.isEmpty()) {
            log.debug("No active CONTACT_CREATED workflows for organization {}", organizationId);
            return;
        }

        for (Workflow workflow : workflows) {
            log.info("Triggering workflow '{}' for new contact {} in organization {}",
                    workflow.getName(), contact.getId(), organizationId);
            executionService.startExecution(workflow, contact);
        }

    }

    @ApplicationModuleListener
    public void onCampaignSent(CampaignSentEvent event) {
        log.info("Checking workflows for CAMPAIGN_SENT trigger in organization {}",
                event.organizationId());

        List<Workflow> workflows = workflowRepository
                .findActiveTriggers(event.organizationId(), WorkflowTriggerType.CAMPAIGN_SENT);

        if (workflows.isEmpty()) {
            log.debug("No active CAMPAIGN_SENT workflows for organization {}", event.organizationId());
            return;
        }

        log.info("Found {} active workflows for CAMPAIGN_SENT", workflows.size());

        List<CampaignContact> campaignContacts = campaignService.getCampaignContacts(event.campaignId());

        log.info("Campaign {} has {} contacts", event.campaignId(), campaignContacts.size());

        for (Workflow workflow : workflows) {

            if (shouldTriggerWorkflowForCampaign(workflow, event)) {

                for (CampaignContact cc : campaignContacts) {
                    Contact contact = cc.getContact();

                    if (cc.getStatus() == MessageStatus.SENT ||
                            cc.getStatus() == MessageStatus.DELIVERED) {

                        log.info("Triggering workflow '{}' for contact {} (campaign: {})",
                                workflow.getName(), contact.getId(), event.campaignId());

                        executionService.startExecution(workflow, contact);
                    } else {
                        log.debug("Skipping contact {} - message not sent (status: {})",
                                contact.getId(), cc.getStatus());
                    }
                }
            }
        }

    }

    private boolean shouldTriggerWorkflowForCampaign(Workflow workflow, CampaignSentEvent event) {

        if (workflow.getTriggerParams() == null || workflow.getTriggerParams().isEmpty() ||
                workflow.getTriggerParams().equals("{}")) {
            return true;
        }

        try {

            Map<String, Object> params = objectMapper.readValue(workflow.getTriggerParams(), Map.class);

            if (params.containsKey("campaignId")) {
                Long specificCampaignId = Long.valueOf(params.get("campaignId").toString());
                if (!event.campaignId().equals(specificCampaignId)) {
                    log.debug("Workflow '{}' is configured for campaign {} only, skipping campaign {}",
                            workflow.getName(), specificCampaignId, event.campaignId());
                    return false;
                }
            }

            if (params.containsKey("channel")) {
                String specificChannel = params.get("channel").toString();
                if (!event.channel().toString().equals(specificChannel)) {
                    log.debug("Workflow '{}' is configured for channel {} only, skipping channel {}",
                            workflow.getName(), specificChannel, event.channel());
                    return false;
                }
            }

            return true;

        } catch (Exception e) {
            log.error("Error parsing workflow trigger params: {}", e.getMessage());
            return true;
        }
    }


}
