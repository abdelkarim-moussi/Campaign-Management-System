package com.app.cms.user.sevice;

import com.app.cms.user.dto.OrganizationDto;

public interface OrganizationService {
    OrganizationDto getCurrentOrganization();
    OrganizationDto updateOrganization(OrganizationDto dto);
}
