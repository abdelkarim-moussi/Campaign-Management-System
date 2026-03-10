package com.app.cms.campaign;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
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
