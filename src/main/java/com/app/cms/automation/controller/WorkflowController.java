package com.app.cms.automation.controller;

import com.app.cms.automation.dto.WorkflowDto;
import com.app.cms.automation.dto.WorkflowStatsDto;
import com.app.cms.automation.entity.Workflow;
import com.app.cms.automation.entity.WorkflowExecution;
import com.app.cms.automation.entity.WorkflowLog;
import com.app.cms.automation.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("api/v1/automation/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Workflow> createWorkflow(@Valid @RequestBody WorkflowDto dto) {
        Workflow workflow = workflowService.createWorkflow(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(workflow);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Workflow> updateWorkflow(@PathVariable Long id, @Valid @RequestBody WorkflowDto dto) {
        return ResponseEntity.ok(workflowService.updateWorkflow(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Page<Workflow>> getAllWorkflows(Pageable pageable) {
        return ResponseEntity.ok(workflowService.getAllWorkflows(pageable));
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Page<Workflow>> getActiveWorkflows(Pageable pageable) {
        return ResponseEntity.ok(workflowService.getActiveWorkflows(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Workflow> getWorkflow(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getWorkflow(id));
    }

    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Workflow> activateWorkflow(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.activateWorkflow(id));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Workflow> deactivateWorkflow(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.deactivateWorkflow(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/executions")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Page<WorkflowExecution>> getWorkflowExecutions(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(workflowService.getWorkflowExecutions(id, pageable));
    }

    @GetMapping("/executions/{executionId}/logs")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Page<WorkflowLog>> getExecutionLogs(@PathVariable Long executionId, Pageable pageable) {
        return ResponseEntity.ok(workflowService.getExecutionLogs(executionId, pageable));
    }

    @GetMapping("/{id}/stats")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<WorkflowStatsDto> getWorkflowStats(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getWorkflowStats(id));
    }
}
