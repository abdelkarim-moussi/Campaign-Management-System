package com.app.cms.contact;

import com.app.cms.contact.domain.ContactEntity;
import com.app.cms.contact.domain.ContactStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<ContactEntity,Long> {
    Optional<ContactEntity> findByEmail(String email);
    List<ContactEntity> findByStatus(ContactStatus status);
}
