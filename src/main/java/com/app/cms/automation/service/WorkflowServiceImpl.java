package com.app.cms.automation.service;

import com.app.cms.automation.dto.WorkflowActionDto;
import com.app.cms.automation.dto.WorkflowDto;
import com.app.cms.automation.entity.Workflow;
import com.app.cms.automation.entity.WorkflowAction;
import com.app.cms.automation.entity.WorkflowExecution;
import com.app.cms.automation.entity.WorkflowStatus;
import com.app.cms.automation.repository.WorkflowActionRepository;
import com.app.cms.automation.repository.WorkflowExecutionRepository;
import com.app.cms.automation.repository.WorkflowLogRepository;
import com.app.cms.automation.repository.WorkflowRepository;
import com.app.cms.common.security.OrganizationContext;
import com.app.cms.contact.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowServiceImpl {

    private final WorkflowRepository workflowRepository;
    private final WorkflowActionRepository actionRepository;
    private final WorkflowExecutionRepository executionRepository;
    private final WorkflowLogRepository logRepository;
    private final WorkflowExecutionService executionService;
    private final ContactService contactService;
    private final ObjectMapper objectMapper;

    @Transactional
    public Workflow createWorkflow(WorkflowDto dto) {
        log.info("Creating workflow: {}", dto.getName());

        if (workflowRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Workflow name already exists: " + dto.getName());
        }

        Workflow workflow = new Workflow();
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

    public Workflow getWorkflow(Long id) {

        Long organizationId = OrganizationContext.getOrganizationId();

        return workflowRepository.findByIdAndOrganizationId(id,organizationId)
                .orElseThrow(() -> new RuntimeException("Workflow not found: " + id));
    }

    public List<Workflow> getAllWorkflows() {
        Long organizationId = OrganizationContext.getOrganizationId();
        return workflowRepository.findByOrganizationId(organizationId);
    }

    public List<Workflow> getActiveWorkflows() {
        Long organizationId = OrganizationContext.getOrganizationId();
        return workflowRepository.findByOrganizationIdAndStatus(organizationId,WorkflowStatus.ACTIVE);
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

    public List<WorkflowExecution> getWorkflowExecutions(Long workflowId) {
        getWorkflow(workflowId);
        return executionRepository.findByWorkflowId(workflowId);
    }


}
