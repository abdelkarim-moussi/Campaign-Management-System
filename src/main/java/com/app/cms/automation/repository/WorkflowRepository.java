package com.app.cms.automation.repository;

import com.app.cms.automation.entity.Workflow;
import com.app.cms.automation.entity.WorkflowStatus;
import com.app.cms.automation.entity.WorkflowTriggerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
    List<Workflow> findByOrganizationId(Long organizationId);

    Page<Workflow> findByOrganizationId(Long organizationId, Pageable pageable);

    Optional<Workflow> findByIdAndOrganizationId(Long id, Long organizationId);

    List<Workflow> findByOrganizationIdAndStatus(Long organizationId, WorkflowStatus status);

    Page<Workflow> findByOrganizationIdAndStatus(Long organizationId, WorkflowStatus status, Pageable pageable);

    List<Workflow> findByOrganizationIdAndTriggerType(Long organizationId, WorkflowTriggerType triggerType);

    @Query("SELECT w FROM Workflow w WHERE w.organizationId = :organizationId " +
            "AND w.triggerType = :triggerType AND w.status = 'ACTIVE'")
    List<Workflow> findActiveTriggers(Long organizationId, WorkflowTriggerType triggerType);

    boolean existsByNameAndOrganizationId(String name, Long organizationId);

    @Query("SELECT COUNT(w) FROM Workflow w WHERE w.organizationId = :organizationId")
    int countByOrganizationId(Long organizationId);

    boolean existsByName(String name);
}
