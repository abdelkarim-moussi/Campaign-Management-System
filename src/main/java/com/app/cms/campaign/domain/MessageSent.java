package com.app.cms.campaign.domain;

import com.app.cms.campaign.MessageType;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

public class MessageSent {
    private String id;
    private String recipient;
    private String subject;
    private String content;
    private MessageStatus status;
    private MessageType type;
    private String errorMessage;
    @CreationTimestamp
    private Date sentAt;
}
