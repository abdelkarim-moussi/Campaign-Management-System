package com.app.cms.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String slug;

    private String email;
    private String phone;
    private String website;
    private String address;
    private String city;
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationPlan plan = OrganizationPlan.FREE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrganizationStatus status = OrganizationStatus.ACTIVE;

    private Integer maxContacts = 1000;
    private Integer maxCampaignsPerMonth = 10;
    private Integer maxUsers = 5;
    private Integer maxWorkflows = 3;

    private Integer currentContacts = 0;
    private Integer currentCampaignsThisMonth = 0;
    private Integer currentUsers = 0;

    private LocalDateTime trialEndsAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if (slug == null && name != null) {
            slug = generateSlug(name);
        }


        if (plan == OrganizationPlan.FREE) {
            trialEndsAt = LocalDateTime.now().plusDays(14);
            status = OrganizationStatus.TRIAL;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private String generateSlug(String name) {
        String slug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();

        return slug + "-" + System.currentTimeMillis();
    }


    public boolean canAddContact() {
        return currentContacts < maxContacts;
    }

    public boolean canCreateCampaign() {
        return currentCampaignsThisMonth < maxCampaignsPerMonth;
    }

    public boolean canAddUser() {
        return currentUsers < maxUsers;
    }

    public boolean isActive() {
        return status == OrganizationStatus.ACTIVE || status == OrganizationStatus.TRIAL;
    }

    public boolean isTrialExpired() {
        return status == OrganizationStatus.TRIAL &&
                trialEndsAt != null &&
                trialEndsAt.isBefore(LocalDateTime.now());
    }
}