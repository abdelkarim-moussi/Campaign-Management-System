package com.app.cms.template;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    Template createTemplate(TemplateDTO dto);

    Template getTemplate(String id);

    List<Template> getAllTemplates();

    List<Template> getTemplatesByType(TemplateType type);
    List<Template> getTemplatesByStatus(TemplateStatus status);

    List<Template> getActiveTemplates();

    List<Template> getActiveTemplatesByType(TemplateType type);

    List<Template> searchTemplates(String keyword);

    Template updateTemplate(String id,TemplateDTO dto);

    void deleteTemplate(String id);

    Template activateTemplate(String id);

    Template archiveTemplate(String id);

    TemplatePreviewResult previewTemplate(String id, Map<String,String> variables);

    List<String> getTemplateVariables(String id);

    TemplatePreviewDTO processTemplateForCampaign(String templateId, Map<String,String> templateVariables);
}
