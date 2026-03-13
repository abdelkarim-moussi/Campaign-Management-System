package com.app.cms.automation.dto;

import com.app.cms.automation.entity.WorkflowTriggerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class WorkflowDto {
    @NotBlank(message = "Workflow name is required")
    private String name;

    private String description;

    @NotNull(message = "Trigger type is required")
    private WorkflowTriggerType triggerType;

    private String triggerParams;  // JSON

    @NotNull(message = "Actions are required")
    private List<WorkflowActionDto> actions;
}
