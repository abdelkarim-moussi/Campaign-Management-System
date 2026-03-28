package com.app.cms.template.service;

import com.app.cms.common.security.OrganizationContext;
import com.app.cms.template.mapper.TemplateMapper;
import com.app.cms.template.TemplateNotFoundException;
import com.app.cms.template.dto.TemplateDTO;
import com.app.cms.template.dto.TemplatePreviewResult;
import com.app.cms.template.entity.Template;
import com.app.cms.template.entity.TemplateStatus;
import com.app.cms.template.entity.TemplateType;
import com.app.cms.template.repository.TemplateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateServiceImpl implements TemplateService {
    private final TemplateRepository templateRepository;
    private final TemplateProcessor templateProcessor;
    private final TemplateMapper mapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public Template createTemplate(TemplateDTO dto) {

        log.info("creating template {} ", dto.getName());

        Long organizationId = OrganizationContext.getOrganizationId();

        if (templateRepository.existsByNameAndOrganizationId(dto.getName(), organizationId)) {
            throw new IllegalArgumentException("Template name already exists : " + dto.getName());
        }

        if (dto.getType().equals(TemplateType.EMAIL) &&
                (dto.getSubject() == null || dto.getSubject().isEmpty())) {
            throw new IllegalArgumentException("Subject is required for email template");
        }

        Template template = mapper.toEntity(dto);
        template.setOrganizationId(organizationId);

        List<String> extractedVars = templateProcessor.extractVariables(dto.getContent());
        if (dto.getType().equals(TemplateType.EMAIL)) {
            extractedVars.addAll(templateProcessor.extractVariables(dto.getSubject()));
        }

        try {
            template.setAvailableVariables(objectMapper.writeValueAsString(extractedVars));
        } catch (JsonProcessingException exception) {
            log.error("Error Serializing variables ", exception);
            template.setAvailableVariables("[]");
        }

        Template saved = templateRepository.save(template);
        log.info("Template Created With Id : {}", saved.getId());

        return saved;

    }

    public Template getTemplate(Long id) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return templateRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new TemplateNotFoundException("No Template Found With Id: " + id));
    }

    public Page<Template> getAllTemplates(Pageable pageable) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return templateRepository.findAllByOrganizationId(organizationId, pageable);
    }

    public Page<Template> getTemplatesByType(TemplateType type, Pageable pageable) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return templateRepository.findByTypeAndOrganizationId(type, organizationId, pageable);
    }

    public Page<Template> getTemplatesByStatus(TemplateStatus status, Pageable pageable) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return templateRepository.findByStatusAndOrganizationId(status, organizationId, pageable);
    }

    public Page<Template> getActiveTemplates(Pageable pageable) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return templateRepository.findActiveTemplatesByOrganization(organizationId, pageable);
    }

    public Page<Template> getActiveTemplatesByType(TemplateType type, Pageable pageable) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return templateRepository.findActiveTemplatesByTypeAndOrganization(type, organizationId, pageable);
    }

    public Page<Template> searchTemplates(String keyword, Pageable pageable) {
        Long organizationId = OrganizationContext.getOrganizationId();
        return templateRepository.searchTemplatesByOrganization(keyword, organizationId, pageable);
    }

    @Transactional
    public Template updateTemplate(Long id, TemplateDTO dto) {
        log.info("updating template {}", id);

        Long organizationId = OrganizationContext.getOrganizationId();

        Template template = getTemplate(id);

        if (!template.getName().equals(dto.getName()) &&
                templateRepository.existsByNameAndOrganizationId(dto.getName(), organizationId)) {
            throw new IllegalArgumentException("Template Name Already Exists " + dto.getName());
        }

        template.setName(dto.getName());
        template.setDescription(dto.getDescription());
        template.setStatus(dto.getStatus());
        template.setSubject(dto.getSubject());
        template.setContent(dto.getContent());

        List<String> extractedVars = templateProcessor.extractVariables(dto.getContent());

        if (template.getType() == TemplateType.EMAIL) {
            extractedVars.addAll(templateProcessor.extractVariables(dto.getSubject()));
        }

        try {
            template.setAvailableVariables(objectMapper.writeValueAsString(extractedVars));
        } catch (JsonProcessingException e) {
            log.error("Error Serializing variables ", e);
        }

        return templateRepository.save(template);
    }

    @Transactional
    public void deleteTemplate(Long id) {
        log.info("deleting template with id : {}", id);

        Long organizationId = OrganizationContext.getOrganizationId();

        if (!templateRepository.existsByIdAndOrganization(id, organizationId)) {
            throw new TemplateNotFoundException("Template not Found : " + id);
        }

        templateRepository.deleteByIdAndOrganizationId(id, organizationId);
    }

    @Transactional
    public Template activateTemplate(Long id) {
        Template template = getTemplate(id);
        template.setStatus(TemplateStatus.ACTIVE);
        return templateRepository.save(template);
    }

    @Transactional
    public Template archiveTemplate(Long id) {
        Template template = getTemplate(id);
        template.setStatus(TemplateStatus.ARCHIVED);
        return templateRepository.save(template);
    }

    public TemplatePreviewResult previewTemplate(Long id, Map<String, String> variables) {
        Template template = getTemplate(id);

        String processedSubject = null;
        String processedContent = templateProcessor.processTemplate(template.getContent(), variables);

        if (template.getType().equals(TemplateType.EMAIL)) {
            processedSubject = templateProcessor.processTemplate(template.getSubject(), variables);
        }

        return TemplatePreviewResult.builder()
                .subject(processedSubject)
                .content(processedContent)
                .build();
    }

    public List<String> getTemplateVariables(Long id) {
        Template template = getTemplate(id);

        try {
            return objectMapper.readValue(
                    template.getAvailableVariables(), new TypeReference<List<String>>() {
                    });
        } catch (JsonProcessingException e) {
            log.error("error extracting template variables : ", e);
            return List.of();
        }
    }

    public TemplatePreviewResult processTemplateForCampaign(Long templateId, Map<String, String> templateVariables) {
        return previewTemplate(templateId, templateVariables);
    }

}
