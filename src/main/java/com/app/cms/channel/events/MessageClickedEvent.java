package com.app.cms.channel.events;

import java.time.LocalDateTime;

public record MessageClickedEvent(
        Long messageId,
        Long campaignId,
        Long contactId,
        String url,
        LocalDateTime clickedAt
) {}
