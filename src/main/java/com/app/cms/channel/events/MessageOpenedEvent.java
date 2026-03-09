package com.app.cms.channel.events;

import java.time.LocalDateTime;

public record MessageOpenedEvent (
        Long messageId,
        Long campaignId,
        Long contactId,
        LocalDateTime openedAt
){}
