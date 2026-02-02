package com.app.cms.template;

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



    }
}
