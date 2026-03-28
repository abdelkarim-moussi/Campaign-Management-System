package com.app.cms.contact.repository;

import com.app.cms.contact.entity.ContactStatus;
import com.app.cms.contact.entity.Contact;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

        @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contact c WHERE c.id =:id AND c.organizationId=:orgId")
        boolean existsByIdAndOrganization(Long id, Long orgId);

        boolean existsByEmail(String email);

        @Query("SELECT c FROM Contact c JOIN c.tags t WHERE t.id IN :tagIds AND t.organizationId = :orgId")
        List<Contact> findByTagIdsAndOrganizationId(List<Long> tagIds, Long orgId);

        @Query("SELECT c FROM Contact c WHERE " +
                        "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "AND c.organizationId =:orgId")
        List<Contact> searchContactsByOrganization(String keyword, Long orgId);

        @Query("SELECT c FROM Contact c WHERE " +
                        "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                        "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        "AND c.organizationId =:orgId")
        Page<Contact> searchContactsByOrganization(String keyword, Long orgId, Pageable pageable);

        @Query("SELECT c FROM Contact c WHERE c.organizationId = :orgId")
        List<Contact> findAllByOrganization(@Param("orgId") Long orgId);

        @Query("SELECT c FROM Contact c WHERE c.organizationId = :orgId")
        Page<Contact> findAllByOrganization(@Param("orgId") Long orgId, Pageable pageable);

        @Query("SELECT c FROM Contact c WHERE c.id = :id AND c.organizationId = :orgId")
        Optional<Contact> findByIdAndOrganization(@Param("id") Long id,
                        @Param("orgId") Long orgId);

        @Query("SELECT c FROM Contact c WHERE c.organizationId = :orgId AND c.status = :status")
        List<Contact> findByStatusAndOrganization(@Param("status") ContactStatus status,
                        @Param("orgId") Long orgId);

        @Query("SELECT c FROM Contact c WHERE c.organizationId = :orgId AND c.status = :status")
        Page<Contact> findByStatusAndOrganization(@Param("status") ContactStatus status,
                        @Param("orgId") Long orgId, Pageable pageable);

        @Query("SELECT c FROM Contact c WHERE c.id IN :ids AND c.organizationId = :orgId")
        List<Contact> findByIdInAndOrganization(@Param("ids") List<Long> ids, @Param("orgId") Long orgId);

        @org.springframework.data.jpa.repository.Modifying
        @org.springframework.transaction.annotation.Transactional
        @Query("DELETE FROM Contact c WHERE c.id = :id AND c.organizationId = :orgId")
        void deleteByIdAndOrganizationId(@Param("id") Long id, @Param("orgId") Long orgId);

}
