package com.app.cms.automation.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "workflow_actions")
public class WorkflowAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "workflow_id", nullable = false)
    @JsonIgnore
    private Workflow workflow;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType type;

    @Column(nullable = false)
    private Integer orderIndex;

    private Integer delayHours;

    @Column(columnDefinition = "TEXT")
    private String actionParams;

    @Column(nullable = false)
    private Boolean isActive = true;

    public boolean isWaitAction() {
        return this.type == ActionType.WAIT;
    }
}
