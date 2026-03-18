package com.app.cms.template.service;

import com.app.cms.template.dto.TemplateDTO;
import com.app.cms.template.dto.TemplatePreviewResult;
import com.app.cms.template.entity.Template;
import com.app.cms.template.entity.TemplateStatus;
import com.app.cms.template.entity.TemplateType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    Template createTemplate(TemplateDTO dto);

    Template getTemplate(Long id);

    List<Template> getAllTemplates();
    Page<Template> getAllTemplates(Pageable pageable);

    List<Template> getTemplatesByType(TemplateType type);
    Page<Template> getTemplatesByType(TemplateType type, Pageable pageable);
    List<Template> getTemplatesByStatus(TemplateStatus status);
    Page<Template> getTemplatesByStatus(TemplateStatus status, Pageable pageable);

    List<Template> getActiveTemplates();
    Page<Template> getActiveTemplates(Pageable pageable);

    List<Template> getActiveTemplatesByType(TemplateType type);
    Page<Template> getActiveTemplatesByType(TemplateType type, Pageable pageable);

    List<Template> searchTemplates(String keyword);
    Page<Template> searchTemplates(String keyword, Pageable pageable);

    Template updateTemplate(Long id,TemplateDTO dto);

    void deleteTemplate(Long id);

    Template activateTemplate(Long id);

    Template archiveTemplate(Long id);

    TemplatePreviewResult previewTemplate(Long id, Map<String,String> variables);

    List<String> getTemplateVariables(Long id);

    TemplatePreviewResult processTemplateForCampaign(Long templateId, Map<String,String> templateVariables);
}
