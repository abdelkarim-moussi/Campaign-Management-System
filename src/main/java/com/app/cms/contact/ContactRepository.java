package com.app.cms.contact;

import com.app.cms.contact.domain.Contact;
import com.app.cms.contact.domain.ContactStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact,Long> {
    Optional<Contact> findByEmail(String email);
    boolean existsByEmail(String email);
    List<Contact> findByStatus(ContactStatus status);

    @Query("SELECT c FROM Contact c JOIN c.tags t WHERE t.id IN :tagIds")
    List<Contact> findByTagIds(List<Long> tagIds);

    @Query("SELECT c FROM Contact c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Contact> searchContacts(String keyword);
}
