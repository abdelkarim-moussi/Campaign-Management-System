package com.app.cms.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CampaignStatsRepository extends JpaRepository<CampaignStats,Long> {
    Optional<CampaignStats> findByCampaignId(Long campaignId);

    List<CampaignStats> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT cs FROM CampaignStats cs ORDER BY cs.openRate DESC")
    List<CampaignStats> findTopPerformingCampaigns();

    @Query("SELECT cs FROM CampaignStats cs WHERE cs.totalSent > 0 " +
            "ORDER BY cs.lastUpdatedAt DESC")
    List<CampaignStats> findRecentCampaigns();

    @Query("SELECT AVG(cs.openRate) FROM CampaignStats cs WHERE cs.totalSent > 0")
    Double getAverageOpenRate();

    @Query("SELECT AVG(cs.clickRate) FROM CampaignStats cs WHERE cs.totalSent > 0")
    Double getAverageClickRate();
}
