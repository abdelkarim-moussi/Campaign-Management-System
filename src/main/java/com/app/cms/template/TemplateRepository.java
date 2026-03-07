package com.app.cms.template;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TemplateRepository extends JpaRepository<Template,Long> {
    List<Template> findByType(TemplateType type);
    List<Template> findByStatus(TemplateStatus status);
    List<Template> findByTypeAndStatus(TemplateType type, TemplateStatus status);

    Optional<Template> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT t FROM Template t WHERE " +
            "LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%') ) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%',:keyword, '%') ) ")
    List<Template> searchTemplates(String keyword);

    @Query("SELECT t from Template t WHERE t.status = 'ACTIVE' AND t.type = :type")
    List<Template> findActiveTemplatesByType(TemplateType type);

    @Query("SELECT t from Template t WHERE t.status = 'ACTIVE'")
    List<Template> findActiveTemplates();


}
