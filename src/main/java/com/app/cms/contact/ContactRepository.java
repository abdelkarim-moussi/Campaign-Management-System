package com.app.cms.contact;

import com.app.cms.contact.domain.ContactEntity;
import com.app.cms.contact.domain.ContactStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<ContactEntity,Long> {
    Optional<ContactEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    List<ContactEntity> findByStatus(ContactStatus status);

    @Query("SELECT c FROM ContactEntity c JOIN c.tags t WHERE t.id IN :tagIds")
    List<ContactEntity> findByTagIds(List<Long> tagIds);

    @Query("SELECT c FROM ContactEntity c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ContactEntity> searchContacts(String keyword);
}
