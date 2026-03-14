package com.app.cms.automation.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "workflow_logs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "execution_id", nullable = false)
    private WorkflowExecution execution;

    private Integer actionIndex;

    private String actionType;

    @Column(length = 1000)
    private String description;

    private Boolean success;

    @Column(length = 2000)
    private String errorDetails;

    private LocalDateTime executedAt;

    @PrePersist
    protected void onCreate() {
        if (executedAt == null) {
            executedAt = LocalDateTime.now();
        }
    }
}
