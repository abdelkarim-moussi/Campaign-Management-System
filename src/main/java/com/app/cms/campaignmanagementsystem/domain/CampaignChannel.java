package com.app.cms.campaignmanagementsystem.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "campaign_channels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CampaignChannel {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private CampaignChannelType channelType;
    private boolean enabled;
}
