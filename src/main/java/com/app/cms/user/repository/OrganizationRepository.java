package com.app.cms.user.repository;

import com.app.cms.user.entity.Organization;
import com.app.cms.user.entity.OrganizationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization,Long> {

    Optional<Organization> findBySlug(String slug);

    boolean existsBySlug(String slug);

    boolean existsByName(String name);

    List<Organization> findByStatus(OrganizationStatus status);
}
