package com.app.cms.user.mapper;

import com.app.cms.user.dto.OrganizationDto;
import com.app.cms.user.entity.Organization;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    Organization toEntity(OrganizationDto dto);
    OrganizationDto toDto(Organization organization);
}
