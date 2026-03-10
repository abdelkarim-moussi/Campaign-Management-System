package com.app.cms.template;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class TemplatePreviewDTO {
    private Long templateId;
    private Map<String,String> variables;
}
