package com.app.cms.automation.service;

import com.app.cms.automation.entity.ExecutionStatus;
import com.app.cms.automation.entity.Workflow;
import com.app.cms.automation.entity.WorkflowAction;
import com.app.cms.automation.entity.WorkflowExecution;
import com.app.cms.automation.repository.WorkflowExecutionRepository;
import com.app.cms.automation.repository.WorkflowLogRepository;
import com.app.cms.channel.ChannelService;
import com.app.cms.contact.Contact;
import com.app.cms.contact.ContactService;
import com.app.cms.template.TemplateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowExecutionService {

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

}
