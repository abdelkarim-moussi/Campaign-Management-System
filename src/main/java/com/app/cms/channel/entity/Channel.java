package com.app.cms.channel.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "channels")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long organizationId;
    private ChannelType channelType;
    private boolean enabled;
}
