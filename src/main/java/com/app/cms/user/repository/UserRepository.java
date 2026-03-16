package com.app.cms.user.repository;

import com.app.cms.user.entity.User;
import com.app.cms.user.entity.UserRole;
import com.app.cms.user.entity.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByOrganizationId(Long organizationId);

    List<User> findByOrganizationIdAndStatus(Long organizationId, UserStatus status);

    Optional<User> findByIdAndOrganizationId(Long id, Long organizationId);

    @Query("SELECT COUNT(u) FROM User u WHERE u.organization.id = :organizationId")
    int countByOrganizationId(Long organizationId);

    @Query("SELECT u FROM User u WHERE u.organization.id = :organizationId AND u.role = :role")
    List<User> findByOrganizationIdAndRole(Long organizationId, UserRole role);

    Optional<User> findByEmailVerificationToken(String token);

    Optional<User> findByPasswordResetToken(String token);
}
