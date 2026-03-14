package com.app.cms.contact.repository;

import com.app.cms.contact.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByIdInAndOrganizationId(Collection<Long> ids, Long orgId);
}
