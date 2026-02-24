package com.app.cms.template;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {
    private final TemplateRepository templateRepository;
    private final TemplateProcessor templateProcessor;
    private final TemplateMapper mapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public Template createTemplate(TemplateDTO dto){

        log.info("creating template {} ", dto.getName());

        if(templateRepository.existsByName(dto.getName())){
            throw new IllegalArgumentException("Template name already exists : "+dto.getName());
        }

        if(dto.getType().equals(TemplateType.EMAIL) &&
                (dto.getSubject() == null || dto.getSubject().isEmpty())){
            throw new IllegalArgumentException("Subject is required for email template");
        }

        Template template = mapper.toEntity(dto);

        List<String> extractedVars = templateProcessor.extractVariables(dto.getContent());
        if(dto.getType().equals(TemplateType.EMAIL)){
            extractedVars.addAll(templateProcessor.extractVariables(dto.getSubject()));
        }

        try{
            template.setAvailableVariables(objectMapper.writeValueAsString(extractedVars));
        }catch (JsonProcessingException exception){
            log.error("Error Serializing variables ", exception);
            template.setAvailableVariables("[]");
        }

        Template saved = templateRepository.save(template);
        log.info("Template Created With Id : {}",saved.getId());

        return saved;

    }

    public Template getTemplate(String id){
        return templateRepository.findById(id).
                orElseThrow(() -> new TemplateNotFoundException("No Template Found With Id: "+id));
    }

    public List<Template> getAllTemplates(){
        return templateRepository.findAll();
    }

    public List<Template> getTemplatesByType(TemplateType type){
        return templateRepository.findByType(type);
    }

    public List<Template> getActiveTemplates(){
        return templateRepository.findByStatus(TemplateStatus.ACTIVE);
    }

    public List<Template> getActiveTemplatesByType(TemplateType type){
        return templateRepository.findActiveTemplatesByType(type);
    }

    public List<Template> searchTemplates(String keyword){
        return templateRepository.searchTemplates(keyword);
    }

    @Transactional
    public Template updateTemplate(String id,TemplateDTO dto){
        log.info("updating template {}", id);

        Template template = getTemplate(id);

        if (!template.getName().equals(dto.getName()) &&
                templateRepository.existsByName(dto.getName())) {
                throw new IllegalArgumentException("Template Name Already Exists " + dto.getName());
        }

        template.setName(dto.getName());
        template.setDescription(dto.getDescription());
        template.setStatus(dto.getStatus());
        template.setSubject(dto.getSubject());
        template.setContent(dto.getContent());

        List<String> extractedVars = templateProcessor.extractVariables(dto.getContent());

        if(template.getType() == TemplateType.EMAIL){
            extractedVars.addAll(templateProcessor.extractVariables(dto.getSubject()));
        }

        try{
            template.setAvailableVariables(objectMapper.writeValueAsString(extractedVars));
        }catch (JsonProcessingException e){
            log.error("Error Serializing variables ",e);
        }

        return templateRepository.save(template);
    }
}
