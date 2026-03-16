package com.app.cms.automation.repository;

import com.app.cms.automation.entity.ExecutionStatus;
import com.app.cms.automation.entity.WorkflowExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface WorkflowExecutionRepository extends JpaRepository<WorkflowExecution, Long> {
    List<WorkflowExecution> findByOrganizationId(Long organizationId);

    List<WorkflowExecution> findByWorkflowId(Long workflowId);

    List<WorkflowExecution> findByContactId(Long contactId);

    List<WorkflowExecution> findByOrganizationIdAndStatus(Long organizationId, ExecutionStatus status);

    @Query("SELECT e FROM WorkflowExecution e WHERE " +
            "e.status = 'WAITING' AND e.resumeAt <= :now")
    List<WorkflowExecution> findReadyToResume(LocalDateTime now);

    @Query("SELECT COUNT(e) FROM WorkflowExecution e WHERE " +
            "e.workflow.id = :workflowId AND e.organizationId = :organizationId")
    int countByWorkflowIdAndOrganizationId(Long workflowId, Long organizationId);

}
