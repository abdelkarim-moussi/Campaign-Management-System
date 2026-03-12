package com.app.cms.campaign.events;

import java.time.LocalDateTime;

public record CampaignCreatedEvent(
                Long campaignId,
                Long organizationId,
                String campaignName,
                LocalDateTime createdAt) {
}
