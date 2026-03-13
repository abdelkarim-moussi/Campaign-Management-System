package com.app.cms.automation.service;

import com.app.cms.automation.entity.Workflow;
import com.app.cms.automation.entity.WorkflowExecution;
import com.app.cms.contact.Contact;


public interface WorkflowExecutionService {
    WorkflowExecution startExecution(Workflow workflow, Contact contact);
    void executeNextAction(WorkflowExecution execution);
}
