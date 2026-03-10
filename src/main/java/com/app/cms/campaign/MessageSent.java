package com.app.cms.campaign;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class MessageSent {
    private Long id;
    private String recipient;
    private String subject;
    private String content;
    private MessageStatus status;
    private MessageType type;
    private String errorMessage;
    @CreationTimestamp
    private LocalDateTime sentAt;
}
