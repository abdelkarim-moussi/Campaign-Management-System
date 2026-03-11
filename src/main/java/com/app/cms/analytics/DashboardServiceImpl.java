package com.app.cms.analytics;

import com.app.cms.common.security.OrganizationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardServiceImpl implements DashboardService {
    private final CampaignStatsRepository campaignStatsRepository;
    private final MessageTrackingRepository messageTrackingRepository;

    public DashboardDto getDashboardStats() {
        log.info("Generating dashboard statistics");

        Long organizationId = OrganizationContext.getOrganizationId();

        List<CampaignStats> allStats = campaignStatsRepository.findAll();

        int totalCampaigns = allStats.size();
        int totalSent = allStats.stream()
                .mapToInt(CampaignStats::getTotalSent)
                .sum();
        int totalDelivered = allStats.stream()
                .mapToInt(CampaignStats::getTotalDelivered)
                .sum();
        int totalOpened = allStats.stream()
                .mapToInt(CampaignStats::getTotalOpened)
                .sum();
        int totalClicked = allStats.stream()
                .mapToInt(CampaignStats::getTotalClicked)
                .sum();


        Double avgOpenRate = campaignStatsRepository.getAverageOpenRateByOrganizationId(organizationId);
        Double avgClickRate = campaignStatsRepository.getAverageClickRateByOrganizationId(organizationId);
        Double avgDeliveryRate = totalSent > 0 ?
                ((double) totalDelivered / totalSent) * 100 : 0.0;


        LocalDateTime startOfMonth = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = YearMonth.now().atEndOfMonth().atTime(23, 59, 59);

        List<CampaignStats> thisMonthStats = campaignStatsRepository
                .findByCreatedAtBetweenAndOrganizationId(startOfMonth, endOfMonth, organizationId);

        int campaignsThisMonth = thisMonthStats.size();
        int messagesSentThisMonth = thisMonthStats.stream()
                .mapToInt(CampaignStats::getTotalSent)
                .sum();


        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);

        List<MessageTracking> todayMessages = messageTrackingRepository
                .findByEventAtBetweenAndOrganizationId(startOfDay, endOfDay, organizationId);

        int messagesSentToday = (int) todayMessages.stream()
                .filter(mt -> mt.getEventType() == TrackingEventType.SENT)
                .count();

        return new DashboardDto(
                totalCampaigns,
                totalSent,
                totalDelivered,
                totalOpened,
                totalClicked,
                avgOpenRate != null ? Math.round(avgOpenRate * 100.0) / 100.0 : 0.0,
                avgClickRate != null ? Math.round(avgClickRate * 100.0) / 100.0 : 0.0,
                Math.round(avgDeliveryRate * 100.0) / 100.0,
                campaignsThisMonth,
                messagesSentThisMonth,
                messagesSentToday
        );
    }


    public List<PerformanceMetrics> getPerformanceMetrics(Long campaignId) {

        Long organizationId = OrganizationContext.getOrganizationId();

        CampaignStats stats = campaignStatsRepository.findByCampaignIdAndOrganizationId(campaignId, organizationId)
                .orElseThrow(() -> new RuntimeException("Campaign stats not found"));

        return List.of(
                new PerformanceMetrics("Send", stats.getTotalSent(), 100.0),
                new PerformanceMetrics("Delivered", stats.getTotalDelivered(), stats.getDeliveryRate()),
                new PerformanceMetrics("Opened", stats.getTotalOpened(), stats.getOpenRate()),
                new PerformanceMetrics("Clicked", stats.getTotalClicked(), stats.getClickRate()),
                new PerformanceMetrics("Failed", stats.getTotalFailed(),
                        stats.getTotalSent() > 0 ? ((double) stats.getTotalFailed() / stats.getTotalSent()) * 100 : 0.0)
        );
    }
}
