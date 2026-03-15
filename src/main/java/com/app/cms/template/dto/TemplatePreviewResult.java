package com.app.cms.template.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TemplatePreviewResult {
    private String subject;
    private String content;
}
