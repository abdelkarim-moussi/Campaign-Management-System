package com.app.cms.automation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workflow_actions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkflowAction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String actionType;
    private int delay;
}
