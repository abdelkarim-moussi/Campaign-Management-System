package com.app.cms.automation.service;

import com.app.cms.automation.dto.WorkflowDto;
import com.app.cms.automation.dto.WorkflowStatsDto;
import com.app.cms.automation.entity.Workflow;
import com.app.cms.automation.entity.WorkflowExecution;
import com.app.cms.automation.entity.WorkflowLog;
import com.app.cms.campaign.events.CampaignSentEvent;
import com.app.cms.contact.event.ContactCreatedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



public interface WorkflowService {
    Workflow createWorkflow(WorkflowDto dto);
    Workflow updateWorkflow(Long id, WorkflowDto dto);
    Workflow getWorkflow(Long id);
    Page<Workflow> getAllWorkflows(Pageable pageable);
    Page<Workflow> getActiveWorkflows(Pageable pageable);
    Workflow activateWorkflow(Long id);
    Workflow deactivateWorkflow(Long id);
    void deleteWorkflow(Long id);
    Page<WorkflowExecution> getWorkflowExecutions(Long workflowId, Pageable pageable);
    Page<WorkflowLog> getExecutionLogs(Long executionId, Pageable pageable);
    WorkflowStatsDto getWorkflowStats(Long workflowId);
    void onContactCreated(ContactCreatedEvent event);
    void onCampaignSent(CampaignSentEvent event);
}
