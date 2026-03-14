package com.app.cms.user.web;

import com.app.cms.user.dto.OrganizationDto;
import com.app.cms.user.sevice.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;


    @GetMapping("/current")
    public ResponseEntity<OrganizationDto> getCurrentOrganization() {
        OrganizationDto org = organizationService.getCurrentOrganization();
        return ResponseEntity.ok(org);
    }

    @PutMapping ("/current")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OrganizationDto> updateOrganization(@RequestBody OrganizationDto dto) {
        OrganizationDto org = organizationService.updateOrganization(dto);
        return ResponseEntity.ok(org);
    }
}
