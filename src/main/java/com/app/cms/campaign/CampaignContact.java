package com.app.cms.campaign;

import com.app.cms.contact.Contact;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_contacts")
public class CampaignContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "campaign_id", nullable = false)
    private Campaign campaign;

    @ManyToOne
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.PENDING;

    private Long messageSentStatus;

    private LocalDateTime addedAt;

    private LocalDateTime senAt;

    @PrePersist
    private void onCreate (){
        addedAt = LocalDateTime.now();
    }
}
