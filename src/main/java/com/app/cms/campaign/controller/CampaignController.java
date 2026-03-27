package com.app.cms.campaign.controller;

import com.app.cms.campaign.dto.CampaignDto;
import com.app.cms.campaign.dto.CampaignSummaryDto;
import com.app.cms.campaign.entity.Campaign;
import com.app.cms.campaign.entity.CampaignContact;
import com.app.cms.campaign.entity.CampaignStatus;
import com.app.cms.campaign.service.CampaignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/v1/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER')")
    public ResponseEntity<Campaign> createCampaign(@Valid @RequestBody CampaignDto dto) {
        Campaign campaign = campaignService.createCampaign(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(campaign);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Page<Campaign>> getAllCampaigns(
            @RequestParam(required = false) CampaignStatus status,
            Pageable pageable) {

        if (status != null) {
            return ResponseEntity.ok(campaignService.getCampaignsByStatus(status, pageable));
        }

        return ResponseEntity.ok(campaignService.getAllCampaigns(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Campaign> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaign(id));
    }

    @GetMapping("/{id}/summary")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<CampaignSummaryDto> getCampaignSummary(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignSummary(id));
    }

    @GetMapping("/{id}/contacts")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Page<CampaignContact>> getCampaignContacts(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(campaignService.getCampaignContacts(id, pageable));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER','VIEWER')")
    public ResponseEntity<Page<Campaign>> searchCampaigns(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(campaignService.searchCampaigns(keyword, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER')")
    public ResponseEntity<Campaign> updateCampaign(
            @PathVariable Long id,
            @Valid @RequestBody CampaignDto dto) {
        return ResponseEntity.ok(campaignService.updateCampaign(id, dto));
    }

    @PostMapping("/{id}/contacts")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','MEMBER')")
    public ResponseEntity<Void> addContactsToCampaign(
            @PathVariable Long id,
            @RequestBody List<Long> contactIds) {
        campaignService.addContactsToCampaign(id, contactIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/contacts/{contactId}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> removeContactFromCampaign(
            @PathVariable Long id,
            @PathVariable Long contactId) {
        campaignService.removeContactFromCampaign(id, contactId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/send")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> sendCampaign(@PathVariable Long id) {
        campaignService.sendCampaign(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }
}