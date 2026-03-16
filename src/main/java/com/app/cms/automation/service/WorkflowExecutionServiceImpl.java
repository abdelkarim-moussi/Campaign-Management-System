package com.app.cms.automation.service;

import com.app.cms.automation.entity.*;
import com.app.cms.automation.repository.WorkflowExecutionRepository;
import com.app.cms.automation.repository.WorkflowLogRepository;
import com.app.cms.channel.service.ChannelService;
import com.app.cms.channel.dto.EmailDto;
import com.app.cms.channel.dto.SmsDto;
import com.app.cms.contact.entity.Contact;
import com.app.cms.contact.service.ContactService;
import com.app.cms.contact.entity.ContactStatus;
import com.app.cms.template.dto.TemplatePreviewResult;
import com.app.cms.template.service.TemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowExecutionServiceImpl implements WorkflowExecutionService {

    private final WorkflowExecutionRepository executionRepository;
    private final WorkflowLogRepository logRepository;
    private final ContactService contactService;
    private final TemplateService templateService;
    private final ChannelService channelService;
    private final ObjectMapper objectMapper;

    @Transactional
    public WorkflowExecution startExecution(Workflow workflow, Contact contact) {
        log.info("Starting workflow '{}' for contact {}", workflow.getName(), contact.getId());

        WorkflowExecution execution = new WorkflowExecution();
        execution.setWorkflow(workflow);
        execution.setContact(contact);
        execution.setStatus(ExecutionStatus.RUNNING);
        execution.setCurrentActionIndex(0);
        execution.setStartedAt(LocalDateTime.now());
        execution.setContext("{}");

        WorkflowExecution saved = executionRepository.save(execution);

        workflow.incrementExecutions();

        executeNextAction(saved);

        return saved;
    }

    @Async
    @Transactional
    public void executeNextAction(WorkflowExecution execution) {
        try {
            List<WorkflowAction> actions = execution.getWorkflow().getActions();

            if (execution.getCurrentActionIndex() >= actions.size()) {
                completeExecution(execution, true);
                return;
            }

            WorkflowAction action = actions.get(execution.getCurrentActionIndex());

            log.info("Executing action {} ({}) for execution {}",
                    execution.getCurrentActionIndex() + 1,
                    action.getType(),
                    execution.getId());

            if (action.isWaitAction()) {
                handleWaitAction(execution, action);
                return;
            }

            boolean success = performAction(execution, action);

            logAction(execution, action, success, null);

            if (!success) {
                completeExecution(execution, false);
                return;
            }

            execution.setCurrentActionIndex(execution.getCurrentActionIndex() + 1);
            executionRepository.save(execution);

            executeNextAction(execution);

        } catch (Exception e) {
            log.error("Error executing workflow: {}", e.getMessage(), e);
            execution.setErrorMessage(e.getMessage());
            completeExecution(execution, false);
        }
    }

    @Transactional
    protected void handleWaitAction(WorkflowExecution execution, WorkflowAction action) {
        log.info("Waiting {} hours before next action", action.getDelayHours());

        execution.setStatus(ExecutionStatus.WAITING);
        execution.setResumeAt(LocalDateTime.now().plusHours(action.getDelayHours()));
        executionRepository.save(execution);

        logAction(execution, action, true, "Waiting " + action.getDelayHours() + " hours");

        execution.setCurrentActionIndex(execution.getCurrentActionIndex() + 1);
        executionRepository.save(execution);
    }

    private boolean performAction(WorkflowExecution execution, WorkflowAction action) {
        Contact contact = execution.getContact();

        try {
            Map<String, Object> params = parseActionParams(action.getActionParams());

            switch (action.getType()) {
                case SEND_EMAIL:
                    return sendEmail(contact, params);

                case SEND_SMS:
                    return sendSms(contact, params);

                case CHANGE_STATUS:
                    return changeStatus(contact, params);

                default:
                    log.warn("Unknown action type: {}", action.getType());
                    return false;
            }

        } catch (Exception e) {
            log.error("Error performing action {}: {}", action.getType(), e.getMessage());
            logAction(execution, action, false, e.getMessage());
            return false;
        }
    }

    private boolean sendEmail(Contact contact, Map<String, Object> params) {
        Long templateId = Long.valueOf(params.get("templateId").toString());

        Map<String, String> variables = new HashMap<>();
        variables.put("firstName", contact.getFirstName());
        variables.put("lastName", contact.getLastName());
        variables.put("email", contact.getEmail());
        variables.put("company", contact.getCompany() != null ? contact.getCompany() : "");

        TemplatePreviewResult processed =
                templateService.processTemplateForCampaign(templateId, variables);

        EmailDto emailDto = new EmailDto(
                null,
                contact.getId(),
                contact.getEmail(),
                processed.getSubject(),
                processed.getContent(),
                null,
                null
        );

        var result = channelService.sendEmail(emailDto);
        return result.isSuccess();
    }

    private boolean sendSms(Contact contact, Map<String, Object> params) {
        Long templateId = Long.valueOf(params.get("templateId").toString());

        Map<String, String> variables = new HashMap<>();
        variables.put("firstName", contact.getFirstName());
        variables.put("lastName", contact.getLastName());

        TemplatePreviewResult processed =
                templateService.processTemplateForCampaign(templateId, variables);

        SmsDto smsDto = new SmsDto(
                null,
                contact.getId(),
                contact.getPhone(),
                processed.getContent(),
                null
        );

        var result = channelService.sendSms(smsDto);
        return result.isSuccess();
    }

    private boolean changeStatus(Contact contact, Map<String, Object> params) {
        String newStatus = params.get("newStatus").toString();
        contact.setStatus(ContactStatus.valueOf(newStatus));
        contactService.updateContact(contact.getId(), null);  // Simplifier pour MVP
        log.info("Changed status of contact {} to {}", contact.getId(), newStatus);
        return true;
    }

    @Transactional
    protected void completeExecution(WorkflowExecution execution, boolean success) {
        execution.setStatus(success ? ExecutionStatus.COMPLETED : ExecutionStatus.FAILED);
        execution.setCompletedAt(LocalDateTime.now());
        executionRepository.save(execution);

        Workflow workflow = execution.getWorkflow();
        if (success) {
            workflow.incrementSuccessful();
        } else {
            workflow.incrementFailed();
        }

        log.info("Workflow execution {} completed with status: {}",
                execution.getId(), execution.getStatus());
    }


    private void logAction(WorkflowExecution execution, WorkflowAction action,
                           boolean success, String details) {
        WorkflowLog log = new WorkflowLog();
        log.setExecution(execution);
        log.setActionIndex(execution.getCurrentActionIndex());
        log.setActionType(action.getType().toString());
        log.setDescription(buildDescription(action, execution.getContact()));
        log.setSuccess(success);
        log.setErrorDetails(details);

        logRepository.save(log);
    }

    private String buildDescription(WorkflowAction action, Contact contact) {
        return switch (action.getType()) {
            case SEND_EMAIL -> "Email envoyé à " + contact.getEmail();
            case SEND_SMS -> "SMS envoyé à " + contact.getPhone();
            case CHANGE_STATUS -> "Statut changé pour le contact";
            case WAIT -> "En attente de " + action.getDelayHours() + " heures";
        };
    }

    private Map<String, Object> parseActionParams(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            log.error("Error parsing action params: {}", e.getMessage());
            return new HashMap<>();
        }
    }
}
