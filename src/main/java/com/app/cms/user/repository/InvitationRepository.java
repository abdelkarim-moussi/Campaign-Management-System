package com.app.cms.user.repository;

import com.app.cms.user.entity.Invitation;
import com.app.cms.user.entity.InvitationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvitationRepository extends JpaRepository<Invitation,Long> {
    Optional<Invitation> findByToken(String token);

    List<Invitation> findByOrganizationId(Long organizationId);

    List<Invitation> findByOrganizationIdAndStatus(Long organizationId, InvitationStatus status);

    boolean existsByEmailAndOrganizationIdAndStatus(String email, Long organizationId, InvitationStatus status);

    @Query("SELECT i FROM Invitation i WHERE i.status = 'PENDING' AND i.expiresAt < :now")
    List<Invitation> findExpiredInvitations(LocalDateTime now);
}
