package com.app.cms.channel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "campaign_channels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(nullable = false)
    private Long organizationId;
    private ChannelType channelType;
    private boolean enabled;
}
