package com.app.cms.campaign;

import com.app.cms.template.Template;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;
    private String objective;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus status = CampaignStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignChannel channel;

    @Column(nullable = false)
    private Long templateId;

    @Transient
    private Template template;

    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;

    private Long createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "campaign", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CampaignContact> campaignContacts = new ArrayList<>();

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    private void OnUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isDraft() {
        return this.status == CampaignStatus.DRAFT;
    }

    public boolean isScheduled() {
        return this.status == CampaignStatus.SCHEDULED;
    }

    public boolean isSent() {
        return this.status == CampaignStatus.SENT;
    }

    public boolean canBeSent() {
        return this.status == CampaignStatus.SCHEDULED ||
                this.status == CampaignStatus.DRAFT;
    }

    public int getTotalContacts() {
        return campaignContacts.size();
    }

}
