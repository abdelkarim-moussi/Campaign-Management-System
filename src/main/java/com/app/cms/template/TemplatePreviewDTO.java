package com.app.cms.template;

import lombok.Data;

import java.util.Map;

@Data
public class TemplatePreviewDTO {
    private String templateId;
    private Map<String,String> variables;
}
