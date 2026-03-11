package com.app.cms.analytics;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final DashboardService dashboardService;


    @GetMapping("/dashboard")
    public ResponseEntity<DashboardDto> getDashboard() {
        return ResponseEntity.ok(dashboardService.getDashboardStats());
    }

    @GetMapping("/campaigns/{campaignId}")
    public ResponseEntity<CampaignStatsDto> getCampaignStats(@PathVariable Long campaignId) {
        return ResponseEntity.ok(analyticsService.getCampaignStats(campaignId));
    }


    @GetMapping("/campaigns")
    public ResponseEntity<List<CampaignStats>> getAllCampaignStats() {
        return ResponseEntity.ok(analyticsService.getAllCampaignStats());
    }


    @GetMapping("/campaigns/top-performing")
    public ResponseEntity<List<CampaignStats>> getTopPerformingCampaigns() {
        return ResponseEntity.ok(analyticsService.getTopPerformingCampaigns());
    }


    @GetMapping("/campaigns/{campaignId}/metrics")
    public ResponseEntity<List<PerformanceMetrics>> getPerformanceMetrics(@PathVariable Long campaignId) {
        return ResponseEntity.ok(dashboardService.getPerformanceMetrics(campaignId));
    }

    @GetMapping("/messages/{messageId}/tracking")
    public ResponseEntity<List<MessageTracking>> getMessageTracking(@PathVariable Long messageId) {
        return ResponseEntity.ok(analyticsService.getMessageTracking(messageId));
    }


    @GetMapping("/campaigns/{campaignId}/tracking")
    public ResponseEntity<List<MessageTracking>> getCampaignTracking(@PathVariable Long campaignId) {
        return ResponseEntity.ok(analyticsService.getCampaignTracking(campaignId));
    }


    @PostMapping("/track")
    public ResponseEntity<Void> trackEvent(
            @RequestParam Long messageId,
            @RequestParam Long campaignId,
            @RequestParam Long contactId,
            @RequestParam TrackingEventType eventType) {

        analyticsService.trackEvent(messageId, campaignId, contactId, eventType, null);
        return ResponseEntity.ok().build();
    }
}
