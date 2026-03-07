package com.app.cms.campaign.events;

import java.time.LocalDateTime;

public record CampaignCreatedEvent(
        Long campaignId,
        String campaignName,
        LocalDateTime createdAt) { }
