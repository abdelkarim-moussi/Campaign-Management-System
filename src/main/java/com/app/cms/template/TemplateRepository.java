package com.app.cms.template;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template, Long> {

        @Query("SELECT t FROM Template t WHERE t.id=:id AND t.organizationId=:orgId")
        Optional<Template> findByIdAndOrganizationId(Long id, Long orgId);

        List<Template> findAllByOrganizationId(Long orgId);

        List<Template> findByTypeAndOrganizationId(TemplateType type, Long orgId);

        List<Template> findByStatusAndOrganizationId(TemplateStatus status, Long orgId);

        @Query("SELECT COUNT(t) FROM Template t WHERE t.id=:id AND t.organizationId=:orgId")
        boolean existsByIdAndOrganization(Long id, Long orgId);

        List<Template> findByTypeAndStatusAndOrganizationId(TemplateType type, TemplateStatus status, Long orgId);

        Optional<Template> findByNameAndOrganizationId(String name, Long orgId);

        boolean existsByNameAndOrganizationId(String name, Long orgId);

        @Query("SELECT t FROM Template t WHERE " +
                        "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%') ) OR " +
                        "LOWER(t.description) LIKE LOWER(CONCAT('%',:keyword, '%') ) " +
                        "AND t.organizationId=:orgId")
        List<Template> searchTemplatesByOrganization(String keyword, Long orgId);

        @Query("SELECT t from Template t WHERE t.status = 'ACTIVE' AND t.type = :type " +
                        "AND t.organizationId=:orgId")
        List<Template> findActiveTemplatesByTypeAndOrganization(TemplateType type, Long orgId);

        @Query("SELECT t from Template t WHERE t.status = 'ACTIVE' AND t.organizationId=:orgId")
        List<Template> findActiveTemplatesByOrganization(Long orgId);

        @org.springframework.data.jpa.repository.Modifying
        @Query("DELETE FROM Template t WHERE t.id = :id AND t.organizationId = :orgId")
        void deleteByIdAndOrganizationId(@org.springframework.data.repository.query.Param("id") Long id,
                        @org.springframework.data.repository.query.Param("orgId") Long orgId);

}
