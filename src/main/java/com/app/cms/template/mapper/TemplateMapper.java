package com.app.cms.template;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Builder;

@Mapper(builder = @Builder(disableBuilder = true))
public interface TemplateMapper {

    @Mapping(target = "availableVariables", ignore = true)
    Template toEntity(TemplateDTO dto);

    @Mapping(target = "availableVariables", ignore = true)
    TemplateDTO toDTO(Template template);
}
