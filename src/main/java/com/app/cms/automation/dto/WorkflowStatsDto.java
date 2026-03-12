package com.app.cms.automation.dto;

import com.app.cms.automation.entity.WorkflowStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class WorkflowStatsDto {
    private Long workflowId;
    private String workflowName;
    private WorkflowStatus status;
    private Integer totalExecutions;
    private Integer successfulExecutions;
    private Integer failedExecutions;
    private Double successRate;
    private Integer activeExecutions;
    private LocalDateTime createdAt;
}
