package com.app.cms.template;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TemplateMapper {
    Template toEntity(TemplateDTO dto);
    TemplateDTO toDTO(Template template);
}
