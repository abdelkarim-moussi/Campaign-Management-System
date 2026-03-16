package com.app.cms.user.sevice;

import com.app.cms.common.security.OrganizationContext;
import com.app.cms.user.dto.OrganizationDto;
import com.app.cms.user.entity.Organization;
import com.app.cms.user.mapper.OrganizationMapper;
import com.app.cms.user.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationServiceImpl implements OrganizationService{
    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;


    public OrganizationDto getCurrentOrganization() {
        Long organizationId = OrganizationContext.getOrganizationId();

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        return organizationMapper.toDto(org);
    }


    @Transactional
    public OrganizationDto updateOrganization(OrganizationDto dto) {
        Long organizationId = OrganizationContext.getOrganizationId();

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        if (dto.getName() != null) {
            org.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            org.setEmail(dto.getEmail());
        }

        Organization updated = organizationRepository.save(org);

        log.info("Organization {} updated", org.getId());

        return organizationMapper.toDto(updated);
    }
}
