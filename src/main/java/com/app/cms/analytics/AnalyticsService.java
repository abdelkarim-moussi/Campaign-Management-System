package com.app.cms.analytics;

import com.app.cms.campaign.events.CampaignSentEvent;
import com.app.cms.channel.events.MessageSentEvent;

import java.util.List;

public interface AnalyticsService {
    void onCampaignSent(CampaignSentEvent event);
    void onMessageSent(MessageSentEvent event);
    void trackEvent(Long messageId, Long campaignId, Long contactId,
                    TrackingEventType eventType, String metadata);
    CampaignStatsDto getCampaignStats(Long campaignId);
    List<CampaignStats> getAllCampaignStats();
    List<CampaignStats> getTopPerformingCampaigns();
    List<MessageTracking> getMessageTracking(Long messageId);
    List<MessageTracking> getCampaignTracking(Long campaignId);

}
