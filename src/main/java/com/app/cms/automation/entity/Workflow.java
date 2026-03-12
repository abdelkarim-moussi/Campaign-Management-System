package com.app.cms.automation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workflows")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Workflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowStatus status = WorkflowStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkflowTriggerType triggerType;

    @Column(columnDefinition = "TEXT")
    private String triggerParams;

    @OneToMany(mappedBy = "workflow", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    private List<WorkflowAction> actions = new ArrayList<>();


    private Integer totalExecutions = 0;
    private Integer successfulExecutions = 0;
    private Integer failedExecutions = 0;


    private Long createdBy;

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

    public boolean isActive() {
        return this.status == WorkflowStatus.ACTIVE;
    }

    public void incrementExecutions() {
        this.totalExecutions++;
    }

    public void incrementSuccessful() {
        this.successfulExecutions++;
    }

    public void incrementFailed() {
        this.failedExecutions++;
    }

    public Double getSuccessRate() {
        if (totalExecutions == 0) return 0.0;
        return ((double) successfulExecutions / totalExecutions) * 100;
    }
}
