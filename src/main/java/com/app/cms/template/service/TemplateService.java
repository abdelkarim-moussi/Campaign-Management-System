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

    Page<Template> getAllTemplates(Pageable pageable);

    Page<Template> getTemplatesByType(TemplateType type, Pageable pageable);
    Page<Template> getTemplatesByStatus(TemplateStatus status, Pageable pageable);

    Page<Template> getActiveTemplates(Pageable pageable);

    Page<Template> getActiveTemplatesByType(TemplateType type, Pageable pageable);

    Page<Template> searchTemplates(String keyword, Pageable pageable);

    Template updateTemplate(Long id,TemplateDTO dto);

    void deleteTemplate(Long id);

    Template activateTemplate(Long id);

    Template archiveTemplate(Long id);

    TemplatePreviewResult previewTemplate(Long id, Map<String,String> variables);

    List<String> getTemplateVariables(Long id);

    TemplatePreviewResult processTemplateForCampaign(Long templateId, Map<String,String> templateVariables);
}
