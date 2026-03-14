package com.app.cms.channel;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_sent")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long organizationId;
    
    private Long campaignId;

    private Long contactId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.PENDING;

    @Column(nullable = false)
    private String recipient;

    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String provider;

    private String externalId;

    @Column(length = 1000)
    private String errorMessage;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime openedAt;
    private LocalDateTime clickedAt;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public boolean isEmail() {
        return this.type == MessageType.EMAIL;
    }

    public boolean isSms() {
        return this.type == MessageType.SMS;
    }

    public boolean isDelivered() {
        return this.status == MessageStatus.DELIVERED ||
                this.status == MessageStatus.OPENED ||
                this.status == MessageStatus.CLICKED;
    }

    public boolean isFailed() {
        return this.status == MessageStatus.FAILED ||
                this.status == MessageStatus.BOUNCED ||
                this.status == MessageStatus.REJECTED;
    }
}
