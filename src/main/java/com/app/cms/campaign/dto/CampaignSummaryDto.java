package com.app.cms.campaign.dto;

import com.app.cms.campaign.entity.CampaignChannel;
import com.app.cms.campaign.entity.CampaignStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CampaignSummaryDto {
    private Long id;
    private String name;
    private CampaignStatus status;
    private CampaignChannel channel;
    private int totalContacts;
    private int sentCount;
    private int deliveredCount;
    private int openedCount;
    private int failedCount;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime createdAt;
}
