package com.app.cms.analytics;

import com.app.cms.campaign.events.CampaignSentEvent;
import com.app.cms.channel.events.MessageSentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {
    private final CampaignStatsRepository campaignStatsRepository;
    private final MessageTrackingRepository messageTrackingRepository;

    @ApplicationModuleListener
    @Transactional
    public void onCampaignSent(CampaignSentEvent event) {
        log.info("Analytics: Campaign sent event received for campaign {}", event.campaignId());

        CampaignStats stats = campaignStatsRepository
                .findByCampaignId(event.campaignId())
                .orElse(new CampaignStats());

        stats.setCampaignId(event.campaignId());
        stats.setCampaignName(event.campaignName());
        stats.setTotalRecipients(event.totalRecipients());

        if (stats.getFirstSentAt() == null) {
            stats.setFirstSentAt(event.sentAt());
        }

        campaignStatsRepository.save(stats);

        log.info("Campaign stats initialized for campaign {}", event.campaignId());
    }

}
