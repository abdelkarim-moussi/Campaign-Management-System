package com.app.cms.analytics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "message_tracking")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private Long messageId;

    private Long campaignId;

    private Long contactId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrackingEventType eventType;


    private String ipAddress;
    private String userAgent;
    private String url;
    private String device;
    private String browser;
    private String os;

    private String country;
    private String city;

    private LocalDateTime eventAt;

    @PrePersist
    protected void onCreate() {
        if (eventAt == null) {
            eventAt = LocalDateTime.now();
        }
    }
}
