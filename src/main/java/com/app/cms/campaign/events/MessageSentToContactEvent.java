package com.app.cms.campaign.events;

public record MessageSentToContactEvent(
        Long campaignId,
        Long contactId,
        Long messageSentId,
        String channel
) {}
