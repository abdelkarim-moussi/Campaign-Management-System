package com.app.cms.automation;

import jakarta.persistence.*;
import lombok.*;

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
    private Long organizationId;
    private String name;
    private String triggerEvent;
    private int delay;
    private boolean active;
}
