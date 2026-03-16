package com.app.cms.campaign.events;

import com.app.cms.campaign.entity.CampaignChannel;

import java.time.LocalDateTime;

public record CampaignSentEvent(
                Long campaignId,
                Long organizationId,
                String campaignName,
                CampaignChannel channel,
                int totalRecipients,
                LocalDateTime sentAt) {
}
