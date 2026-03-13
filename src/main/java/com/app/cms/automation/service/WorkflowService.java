package com.app.cms.automation.service;

import com.app.cms.automation.dto.WorkflowDto;
import com.app.cms.automation.entity.Workflow;

public interface WorkflowService {
    Workflow createWorkflow(WorkflowDto dto);
}
