package com.app.cms.analytics;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
@Entity
@Table(name = "campaign_stats")
public class CampaignStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long campaignId;

    private String campaignName;

    private Integer totalRecipients = 0;
    private Integer totalSent = 0;
    private Integer totalDelivered = 0;
    private Integer totalFailed = 0;
    private Integer totalBounced = 0;

    private Integer totalOpened = 0;
    private Integer totalClicked = 0;
    private Integer totalUnsubscribed = 0;
    private Integer totalSpamComplaints = 0;

    private Double deliveryRate = 0.0;    // (delivered / sent) * 100
    private Double openRate = 0.0;         // (opened / delivered) * 100
    private Double clickRate = 0.0;        // (clicked / delivered) * 100
    private Double bounceRate = 0.0;       // (bounced / sent) * 100
    private Double unsubscribeRate = 0.0;  // (unsubscribed / delivered) * 100

    private Double clickThroughRate = 0.0; // (clicked / opened) * 100

    private LocalDateTime firstSentAt;
    private LocalDateTime lastUpdatedAt;

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
        lastUpdatedAt = LocalDateTime.now();
    }

    public void calculateRates() {

        if (totalSent > 0) {
            deliveryRate = ((double) totalDelivered / totalSent) * 100;
            bounceRate = ((double) totalBounced / totalSent) * 100;
        }

        if (totalDelivered > 0) {
            openRate = ((double) totalOpened / totalDelivered) * 100;
            clickRate = ((double) totalClicked / totalDelivered) * 100;
            unsubscribeRate = ((double) totalUnsubscribed / totalDelivered) * 100;
        }

        if (totalOpened > 0) {
            clickThroughRate = ((double) totalClicked / totalOpened) * 100;
        }


        deliveryRate = Math.round(deliveryRate * 100.0) / 100.0;
        openRate = Math.round(openRate * 100.0) / 100.0;
        clickRate = Math.round(clickRate * 100.0) / 100.0;
        bounceRate = Math.round(bounceRate * 100.0) / 100.0;
        unsubscribeRate = Math.round(unsubscribeRate * 100.0) / 100.0;
        clickThroughRate = Math.round(clickThroughRate * 100.0) / 100.0;
    }
}
