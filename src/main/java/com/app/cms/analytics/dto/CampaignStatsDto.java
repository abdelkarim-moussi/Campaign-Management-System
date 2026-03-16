package com.app.cms.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampaignStatsDto {
    private Long campaignId;
    private String campaignName;
    private Integer totalRecipients;
    private Integer totalSent;
    private Integer totalDelivered;
    private Integer totalOpened;
    private Integer totalClicked;
    private Integer totalFailed;
    private Double openRate;
    private Double clickRate;
    private Double deliveryRate;
    private LocalDateTime firstSentAt;
    private LocalDateTime lastUpdatedAt;
}
