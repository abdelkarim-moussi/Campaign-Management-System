package com.app.cms.template;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class TemplateDTO {
    @NotBlank(message = "name is required")
    private String name;

    private String description;

    @NotBlank(message = "template type is required")
    private TemplateType type;

    private TemplateStatus status = TemplateStatus.DRAFT;

    @NotBlank(message = "content is required")
    private String content;

    private List<String> availableVariables;


}
