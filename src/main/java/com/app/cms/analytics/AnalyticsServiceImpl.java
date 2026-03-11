package com.app.cms.analytics;

import com.app.cms.campaign.events.CampaignSentEvent;
import com.app.cms.channel.events.MessageSentEvent;
import com.app.cms.common.security.OrganizationContext;
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
public class AnalyticsServiceImpl implements AnalyticsService {
    private final CampaignStatsRepository campaignStatsRepository;
    private final MessageTrackingRepository messageTrackingRepository;

    @ApplicationModuleListener
    public void onCampaignSent(CampaignSentEvent event) {
        log.info("Analytics: Campaign sent event received for campaign {}", event.campaignId());

        Long organizationId = OrganizationContext.getOrganizationId();

        CampaignStats stats = campaignStatsRepository
                .findByCampaignIdAndOrganizationId(event.campaignId(),organizationId)
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
    public void onMessageSent(MessageSentEvent event) {
        log.debug("Analytics: Message sent event received for message {}", event.messageId());

        Long organizationId = OrganizationContext.getOrganizationId();

        CampaignStats stats = campaignStatsRepository
                .findByCampaignIdAndOrganizationId(event.campaignId(),organizationId)
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

        Long organizationId = OrganizationContext.getOrganizationId();

        if (messageTrackingRepository.existsByMessageIdAndEventTypeAndOrganizationId(messageId, eventType, organizationId)) {
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


        campaignStatsRepository.findByCampaignIdAndOrganizationId(campaignId, organizationId).ifPresent(stats -> {
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

        Long organizationId = OrganizationContext.getOrganizationId();

        CampaignStats stats = campaignStatsRepository.findByCampaignIdAndOrganizationId(campaignId, organizationId)
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

        Long organizationId = OrganizationContext.getOrganizationId();

        return campaignStatsRepository.findRecentCampaignsByOrganizationId(organizationId);
    }


    public List<CampaignStats> getTopPerformingCampaigns() {

        Long organizationId = OrganizationContext.getOrganizationId();

        return campaignStatsRepository.findTopPerformingCampaignsByOrganizationId(organizationId);
    }


    public List<MessageTracking> getMessageTracking(Long messageId) {

        Long organizationId = OrganizationContext.getOrganizationId();

        return messageTrackingRepository.findByMessageIdAndOrganizationId(messageId, organizationId);
    }

    public List<MessageTracking> getCampaignTracking(Long campaignId) {

        Long organizationId = OrganizationContext.getOrganizationId();

        return messageTrackingRepository.findByCampaignIdAndOrganizationId(campaignId, organizationId);
    }
}
