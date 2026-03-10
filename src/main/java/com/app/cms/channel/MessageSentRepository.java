package com.app.cms.channel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageSentRepository extends JpaRepository<MessageSent,Long> {
    List<MessageSent> findByCampaignId(Long campaignId);

    List<MessageSent> findByContactId(Long contactId);

    List<MessageSent> findByStatus(MessageStatus status);

    List<MessageSent> findByType(MessageType type);

    List<MessageSent> findByCampaignIdAndStatus(Long campaignId, MessageStatus status);

    Optional<MessageSent> findByExternalId(String externalId);

    @Query("SELECT COUNT(m) FROM MessageSent m WHERE m.campaignId = :campaignId")
    int countByCampaignId(Long campaignId);

    @Query("SELECT COUNT(m) FROM MessageSent m WHERE " +
            "m.campaignId = :campaignId AND m.status = :status")
    int countByCampaignIdAndStatus(Long campaignId, MessageStatus status);

    @Query("SELECT m FROM MessageSent m WHERE " +
            "m.sentAt BETWEEN :startDate AND :endDate")
    List<MessageSent> findBySentAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
