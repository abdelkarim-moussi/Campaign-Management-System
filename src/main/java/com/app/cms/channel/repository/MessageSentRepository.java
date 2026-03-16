package com.app.cms.channel.repository;

import com.app.cms.channel.entity.MessageSent;
import com.app.cms.channel.entity.MessageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageSentRepository extends JpaRepository<MessageSent,Long> {

    List<MessageSent> findByCampaignIdAndOrganizationId(Long campaignId,Long orgId);

    Optional<MessageSent> findByIdAndOrganizationId(Long campaignId, Long orgId);

    List<MessageSent> findByContactIdAndOrganizationId(Long contactId, Long orgId);

    Optional<MessageSent> findByExternalIdAndOrganizationId(String externalId, Long orgId);

    @Query("SELECT COUNT(m) FROM MessageSent m WHERE m.campaignId = :campaignId")
    int countByCampaignId(Long campaignId);

    @Query("SELECT COUNT(m) FROM MessageSent m WHERE " +
            "m.campaignId = :campaignId AND m.status = :status")
    int countByCampaignIdAndStatus(Long campaignId, MessageStatus status);

    @Query("SELECT m FROM MessageSent m WHERE " +
            "m.sentAt BETWEEN :startDate AND :endDate")
    List<MessageSent> findBySentAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
