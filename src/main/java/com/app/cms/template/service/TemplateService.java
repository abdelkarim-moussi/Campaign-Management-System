package com.app.cms.template.service;

import com.app.cms.template.dto.TemplateDTO;
import com.app.cms.template.dto.TemplatePreviewResult;
import com.app.cms.template.entity.Template;
import com.app.cms.template.entity.TemplateStatus;
import com.app.cms.template.entity.TemplateType;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    Template createTemplate(TemplateDTO dto);

    Template getTemplate(Long id);

    List<Template> getAllTemplates();

    List<Template> getTemplatesByType(TemplateType type);
    List<Template> getTemplatesByStatus(TemplateStatus status);

    List<Template> getActiveTemplates();

    List<Template> getActiveTemplatesByType(TemplateType type);

    List<Template> searchTemplates(String keyword);

    Template updateTemplate(Long id,TemplateDTO dto);

    void deleteTemplate(Long id);

    Template activateTemplate(Long id);

    Template archiveTemplate(Long id);

    TemplatePreviewResult previewTemplate(Long id, Map<String,String> variables);

    List<String> getTemplateVariables(Long id);

    TemplatePreviewResult processTemplateForCampaign(Long templateId, Map<String,String> templateVariables);
}
