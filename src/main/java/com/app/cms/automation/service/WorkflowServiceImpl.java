package com.app.cms.automation.service;

import com.app.cms.automation.dto.WorkflowActionDto;
import com.app.cms.automation.dto.WorkflowDto;
import com.app.cms.automation.entity.Workflow;
import com.app.cms.automation.entity.WorkflowAction;
import com.app.cms.automation.entity.WorkflowStatus;
import com.app.cms.automation.repository.WorkflowActionRepository;
import com.app.cms.automation.repository.WorkflowExecutionRepository;
import com.app.cms.automation.repository.WorkflowLogRepository;
import com.app.cms.automation.repository.WorkflowRepository;
import com.app.cms.contact.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
