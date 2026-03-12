package com.app.cms.user.dto;

import com.app.cms.user.entity.OrganizationPlan;
import com.app.cms.user.entity.OrganizationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationDto {
    private Long id;
    private String name;
    private String slug;
    private String email;
    private OrganizationPlan plan;
    private OrganizationStatus status;
    private Integer maxContacts;
    private Integer maxCampaignsPerMonth;
    private Integer maxUsers;
    private Integer currentContacts;
    private Integer currentUsers;
    private LocalDateTime trialEndsAt;
    private LocalDateTime createdAt;
}
