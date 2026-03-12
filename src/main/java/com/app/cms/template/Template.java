package com.app.cms.template;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "templates")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EntityListeners(value= AuditingEntityListener.class)
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long organizationId;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TemplateType type;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TemplateStatus status = TemplateStatus.DRAFT;

    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String availableVariables;

    private String createdBy;

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

    public boolean isEmail() {
        return this.type == TemplateType.EMAIL;
    }

    public boolean isSms() {
        return this.type == TemplateType.SMS;
    }

    public boolean isActive() {
        return this.status == TemplateStatus.ACTIVE;
    }
}
