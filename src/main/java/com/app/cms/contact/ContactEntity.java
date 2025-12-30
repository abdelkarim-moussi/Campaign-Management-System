package com.app.cms.contact;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "contacts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ContactEntity {

    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String company;
    private LocalDate createdAt;
}
