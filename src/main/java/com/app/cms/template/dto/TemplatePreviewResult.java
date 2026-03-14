package com.app.cms.template.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TemplatePreviewResult {
    private String subject;
    private String content;
}
