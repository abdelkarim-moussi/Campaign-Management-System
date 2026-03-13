package com.app.cms.automation.repository;

import com.app.cms.automation.entity.WorkflowAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowActionRepository extends JpaRepository<WorkflowAction, Long> {
    List<WorkflowAction> findByWorkflowIdOrderByOrderIndexAsc(Long workflowId);
    void deleteByWorkflowId(Long workflowId);
}
