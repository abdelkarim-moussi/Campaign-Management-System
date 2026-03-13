package com.app.cms.automation.controller;

import com.app.cms.automation.dto.WorkflowDto;
import com.app.cms.automation.dto.WorkflowStatsDto;
import com.app.cms.automation.entity.Workflow;
import com.app.cms.automation.entity.WorkflowExecution;
import com.app.cms.automation.entity.WorkflowLog;
import com.app.cms.automation.service.WorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/v1/automation/workflows")
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    @PostMapping
    public ResponseEntity<Workflow> createWorkflow(@Valid @RequestBody WorkflowDto dto) {
        Workflow workflow = workflowService.createWorkflow(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(workflow);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workflow> updateWorkflow(@PathVariable Long id, @Valid @RequestBody WorkflowDto dto) {
        return ResponseEntity.ok(workflowService.updateWorkflow(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<Workflow>> getAllWorkflows() {
        return ResponseEntity.ok(workflowService.getAllWorkflows());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Workflow>> getActiveWorkflows() {
        return ResponseEntity.ok(workflowService.getActiveWorkflows());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workflow> getWorkflow(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getWorkflow(id));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Workflow> activateWorkflow(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.activateWorkflow(id));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Workflow> deactivateWorkflow(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.deactivateWorkflow(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWorkflow(@PathVariable Long id) {
        workflowService.deleteWorkflow(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/executions")
    public ResponseEntity<List<WorkflowExecution>> getWorkflowExecutions(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getWorkflowExecutions(id));
    }

    @GetMapping("/executions/{executionId}/logs")
    public ResponseEntity<List<WorkflowLog>> getExecutionLogs(@PathVariable Long executionId) {
        return ResponseEntity.ok(workflowService.getExecutionLogs(executionId));
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<WorkflowStatsDto> getWorkflowStats(@PathVariable Long id) {
        return ResponseEntity.ok(workflowService.getWorkflowStats(id));
    }
}
