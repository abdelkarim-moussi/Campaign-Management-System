package com.app.cms.automation.repository;

import com.app.cms.automation.entity.WorkflowLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowLogRepository extends JpaRepository<WorkflowLog, Long> {

    List<WorkflowLog> findByExecutionIdOrderByExecutedAtAsc(Long executionId);
    Page<WorkflowLog> findByExecutionIdOrderByExecutedAtAsc(Long executionId, Pageable pageable);
    List<WorkflowLog> findByExecutionId(Long executionId);
}
