package com.app.cms.campaign.dto;

import com.app.cms.campaign.entity.CampaignChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDto {
    @NotBlank(message = "campaign name is required")
    private String name ;

    private String description;

    private String objective;

    @NotNull(message = "channel is required")
    private CampaignChannel channel;

    @NotNull(message = "template Id is required")
    private Long templateId;

    @NotNull(message = "contact ids are required")
    private List<Long> contactIds;

    private LocalDateTime scheduledAt;

}
