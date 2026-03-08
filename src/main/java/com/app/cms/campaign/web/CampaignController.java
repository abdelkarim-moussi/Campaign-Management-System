package com.app.cms.campaign.web;

import com.app.cms.campaign.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignController {

    private final CampaignService campaignService;

    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@Valid @RequestBody CampaignDto dto) {
        Campaign campaign = campaignService.createCampaign(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(campaign);
    }

    @GetMapping
    public ResponseEntity<List<Campaign>> getAllCampaigns(
            @RequestParam(required = false) CampaignStatus status) {

        if (status != null) {
            return ResponseEntity.ok(campaignService.getCampaignsByStatus(status));
        }

        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaign(id));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<CampaignSummaryDto> getCampaignSummary(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignSummary(id));
    }

    @GetMapping("/{id}/contacts")
    public ResponseEntity<List<CampaignContact>> getCampaignContacts(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignContacts(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Campaign>> searchCampaigns(@RequestParam String keyword) {
        return ResponseEntity.ok(campaignService.searchCampaigns(keyword));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Campaign> updateCampaign(
            @PathVariable Long id,
            @Valid @RequestBody CampaignDto dto) {
        return ResponseEntity.ok(campaignService.updateCampaign(id, dto));
    }

    @PostMapping("/{id}/contacts")
    public ResponseEntity<Void> addContactsToCampaign(
            @PathVariable Long id,
            @RequestBody List<Long> contactIds) {
        campaignService.addContactsToCampaign(id, contactIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/contacts/{contactId}")
    public ResponseEntity<Void> removeContactFromCampaign(
            @PathVariable Long id,
            @PathVariable Long contactId) {
        campaignService.removeContactFromCampaign(id, contactId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<Void> sendCampaign(@PathVariable Long id) {
        campaignService.sendCampaign(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.noContent().build();
    }
}