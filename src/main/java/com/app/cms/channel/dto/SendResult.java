package com.app.cms.channel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SendResult {
    private boolean success;
    private Long messageId;        // ID en base de données
    private String externalId;     // ID du provider (SendGrid, Twilio)
    private String errorMessage;
}
