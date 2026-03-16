package com.app.cms.analytics.service;

import com.app.cms.analytics.entity.CampaignStats;
import com.app.cms.analytics.dto.CampaignStatsDto;
import com.app.cms.analytics.entity.MessageTracking;
import com.app.cms.analytics.entity.TrackingEventType;
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
