package com.app.cms.template;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "templates")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String subject;
    private TemplateType templateType;
}
