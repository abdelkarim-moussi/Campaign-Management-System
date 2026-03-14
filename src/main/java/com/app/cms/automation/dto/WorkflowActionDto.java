package com.app.cms.automation.dto;

import com.app.cms.automation.entity.ActionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WorkflowActionDto {
    @NotNull(message = "Action type is required")
    private ActionType type;

    @NotNull(message = "Order index is required")
    private Integer orderIndex;

    private Integer delayHours;

    private String actionParams;
}
