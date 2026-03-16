package com.app.cms.template.dto;

import com.app.cms.template.entity.TemplateStatus;
import com.app.cms.template.entity.TemplateType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateDTO {
    @NotBlank(message = "name is required")
    private String name;

    private String description;

    @NotNull(message = "template type is required")
    private TemplateType type;

    private TemplateStatus status = TemplateStatus.DRAFT;

    private String subject;

    @NotBlank(message = "content is required")
    private String content;

    private List<String> availableVariables;

}
