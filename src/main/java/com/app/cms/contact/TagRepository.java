package com.app.cms.contact;

import com.app.cms.contact.domain.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagRepository extends JpaRepository<TagEntity,Long> {
    Optional<TagEntity> findByName(String name);
    boolean existsByName(String name);
}
