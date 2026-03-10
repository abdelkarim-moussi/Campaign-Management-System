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

    @ApplicationModuleListener
    @Transactional
    public void onMessageSent(MessageSentEvent event) {
        log.debug("Analytics: Message sent event received for message {}", event.messageId());

        CampaignStats stats = campaignStatsRepository
                .findByCampaignId(event.campaignId())
                .orElseGet(() -> {
                    CampaignStats newStats = new CampaignStats();
                    newStats.setCampaignId(event.campaignId());
                    return newStats;
                });

        if (event.success()) {
            stats.setTotalSent(stats.getTotalSent() + 1);
            stats.setTotalDelivered(stats.getTotalDelivered() + 1);

            MessageTracking tracking = new MessageTracking();
            tracking.setMessageId(event.messageId());
            tracking.setCampaignId(event.campaignId());
            tracking.setContactId(event.contactId());
            tracking.setEventType(TrackingEventType.SENT);
            tracking.setEventAt(event.sentAt());
            messageTrackingRepository.save(tracking);

            MessageTracking deliveryTracking = new MessageTracking();
            deliveryTracking.setMessageId(event.messageId());
            deliveryTracking.setCampaignId(event.campaignId());
            deliveryTracking.setContactId(event.contactId());
            deliveryTracking.setEventType(TrackingEventType.DELIVERED);
            deliveryTracking.setEventAt(event.sentAt());
            messageTrackingRepository.save(deliveryTracking);

        } else {
            stats.setTotalFailed(stats.getTotalFailed() + 1);
        }

        stats.calculateRates();

        campaignStatsRepository.save(stats);
    }


}
