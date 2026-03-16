package com.app.cms.automation.entity;


import com.app.cms.contact.entity.Contact;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_execution")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "organization_id", nullable = false)
    private Long organizationId;

    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    private Workflow workflow;

    @ManyToOne
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status = ExecutionStatus.PENDING;

    private Integer currentActionIndex = 0;

    private LocalDateTime resumeAt;

    @Column(columnDefinition = "TEXT")
    private String context;

    private LocalDateTime startedAt;
    private LocalDateTime completedAt;

    private String errorMessage;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public boolean isRunning() {
        return this.status == ExecutionStatus.RUNNING;
    }

    public boolean isWaiting() {
        return this.status == ExecutionStatus.WAITING;
    }

    public boolean shouldResume() {
        return isWaiting() && resumeAt != null && resumeAt.isBefore(LocalDateTime.now());
    }
}
