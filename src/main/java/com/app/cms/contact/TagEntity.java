package com.app.cms.contact;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "tags")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class TagEntity {
    @Id
    private String id;
    private String name;
}
