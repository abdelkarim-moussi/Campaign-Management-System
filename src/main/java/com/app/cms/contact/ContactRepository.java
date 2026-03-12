package com.app.cms.contact;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact,Long> {

    @Query("SELECT c FROM Contact c WHERE c.id =:id AND c.organizationId=:orgId")
    boolean existsByIdAndOrganization(Long id, Long orgId);

    boolean existsByEmail(String email);

    @Query("SELECT c FROM Contact c JOIN c.tags t WHERE t.id IN :tagIds AND t.organizationId = :orgId")
    List<Contact> findByTagIdsAndOrganizationId(List<Long> tagIds, Long orgId);

    @Query("SELECT c FROM Contact c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "AND c.organizationId =:orgId")
    List<Contact> searchContactsByOrganization(String keyword,Long orgId);

    @Query("SELECT c FROM Contact c WHERE c.organizationId = :orgId")
    List<Contact> findAllByOrganization(@Param("orgId") Long orgId);

    @Query("SELECT c FROM Contact c WHERE c.id = :id AND c.organizationId = :orgId")
    Optional<Contact> findByIdAndOrganization(@Param("id") Long id,
                                              @Param("orgId") Long orgId);

    @Query("SELECT c FROM Contact c WHERE c.organizationId = :orgId AND c.status = :status")
    List<Contact> findByStatusAndOrganization(@Param("status") ContactStatus status,
                                              @Param("orgId") Long orgId);
    
}
