package com.app.cms.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.MEMBER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    private String avatarUrl;
    private String timezone;
    private String language = "fr";

    private Boolean emailVerified = false;
    private String emailVerificationToken;

    private String passwordResetToken;
    private LocalDateTime passwordResetExpiresAt;

    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }


    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isOwner() {
        return role == UserRole.OWNER;
    }

    public boolean isAdmin() {
        return role == UserRole.ADMIN || role == UserRole.OWNER;
    }

    public boolean canManageUsers() {
        return role == UserRole.OWNER || role == UserRole.ADMIN;
    }

    public boolean canCreateCampaigns() {
        return role != UserRole.VIEWER;
    }

    public boolean canDeleteResources() {
        return role == UserRole.OWNER || role == UserRole.ADMIN;
    }
}
