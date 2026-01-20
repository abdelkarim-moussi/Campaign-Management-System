package com.app.cms.campaign.domain;

import com.app.cms.common.security.UserEntity;
import com.app.cms.template.TemplateEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="campaigns")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String title;
    private String description;
    private String objective;
    private CampaignStatus campaignStatus;
    private LocalDateTime lunchDate;
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity owner;

    @OneToOne
    @JoinColumn(name = "template_id")
    private TemplateEntity template;

}
