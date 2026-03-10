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


    @Transactional
    public void trackEvent(Long messageId, Long campaignId, Long contactId,
                           TrackingEventType eventType, String metadata) {
        log.info("Tracking event: {} for message {}", eventType, messageId);

        if (messageTrackingRepository.existsByMessageIdAndEventType(messageId, eventType)) {
            log.debug("Event {} already tracked for message {}", eventType, messageId);
            return;
        }

        MessageTracking tracking = new MessageTracking();
        tracking.setMessageId(messageId);
        tracking.setCampaignId(campaignId);
        tracking.setContactId(contactId);
        tracking.setEventType(eventType);
        tracking.setEventAt(LocalDateTime.now());
        messageTrackingRepository.save(tracking);


        campaignStatsRepository.findByCampaignId(campaignId).ifPresent(stats -> {
            switch (eventType) {
                case OPENED:
                    stats.setTotalOpened(stats.getTotalOpened() + 1);
                    break;
                case CLICKED:
                    stats.setTotalClicked(stats.getTotalClicked() + 1);
                    break;
                case BOUNCED:
                    stats.setTotalBounced(stats.getTotalBounced() + 1);
                    break;
                case UNSUBSCRIBED:
                    stats.setTotalUnsubscribed(stats.getTotalUnsubscribed() + 1);
                    break;
                case SPAM_COMPLAINT:
                    stats.setTotalSpamComplaints(stats.getTotalSpamComplaints() + 1);
                    break;
            }

            stats.calculateRates();
            campaignStatsRepository.save(stats);
        });
    }


    public CampaignStatsDto getCampaignStats(Long campaignId) {
        CampaignStats stats = campaignStatsRepository.findByCampaignId(campaignId)
                .orElseThrow(() -> new RuntimeException("Campaign stats not found: " + campaignId));

        return new CampaignStatsDto(
                stats.getCampaignId(),
                stats.getCampaignName(),
                stats.getTotalRecipients(),
                stats.getTotalSent(),
                stats.getTotalDelivered(),
                stats.getTotalOpened(),
                stats.getTotalClicked(),
                stats.getTotalFailed(),
                stats.getOpenRate(),
                stats.getClickRate(),
                stats.getDeliveryRate(),
                stats.getFirstSentAt(),
                stats.getLastUpdatedAt()
        );
    }


    public List<CampaignStats> getAllCampaignStats() {
        return campaignStatsRepository.findRecentCampaigns();
    }


    public List<CampaignStats> getTopPerformingCampaigns() {
        return campaignStatsRepository.findTopPerformingCampaigns();
    }


    public List<MessageTracking> getMessageTracking(Long messageId) {
        return messageTrackingRepository.findByMessageId(messageId);
    }

    public List<MessageTracking> getCampaignTracking(Long campaignId) {
        return messageTrackingRepository.findByCampaignId(campaignId);
    }
}
