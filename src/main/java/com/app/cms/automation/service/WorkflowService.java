package com.app.cms.automation.service;

import com.app.cms.automation.dto.WorkflowDto;
import com.app.cms.automation.dto.WorkflowStatsDto;
import com.app.cms.automation.entity.Workflow;
import com.app.cms.automation.entity.WorkflowExecution;
import com.app.cms.automation.entity.WorkflowLog;
import com.app.cms.campaign.events.CampaignSentEvent;
import com.app.cms.contact.event.ContactCreatedEvent;

import java.util.List;

public interface WorkflowService {
    Workflow createWorkflow(WorkflowDto dto);
    Workflow updateWorkflow(Long id, WorkflowDto dto);
    Workflow getWorkflow(Long id);
    List<Workflow> getAllWorkflows();
    List<Workflow> getActiveWorkflows();
    Workflow activateWorkflow(Long id);
    Workflow deactivateWorkflow(Long id);
    void deleteWorkflow(Long id);
    List<WorkflowExecution> getWorkflowExecutions(Long workflowId);
    List<WorkflowLog> getExecutionLogs(Long executionId);
    WorkflowStatsDto getWorkflowStats(Long workflowId);
    void onContactCreated(ContactCreatedEvent event);
    void onCampaignSent(CampaignSentEvent event);
}
