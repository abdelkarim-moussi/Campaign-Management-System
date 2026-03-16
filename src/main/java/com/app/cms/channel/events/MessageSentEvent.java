package com.app.cms.channel.events;

import com.app.cms.channel.entity.MessageType;

import java.time.LocalDateTime;

public record MessageSentEvent(
                Long messageId,
                Long organizationId,
                Long campaignId,
                Long contactId,
                MessageType type,
                String recipient,
                boolean success,
                LocalDateTime sentAt) {
}
