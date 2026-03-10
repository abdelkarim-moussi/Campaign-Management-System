package com.app.cms.analytics;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageTrackingRepository extends JpaRepository<MessageTracking,Long> {
    List<MessageTracking> findByMessageId(Long messageId);

    List<MessageTracking> findByCampaignId(Long campaignId);

    List<MessageTracking> findByContactId(Long contactId);

    List<MessageTracking> findByEventType(TrackingEventType eventType);

    List<MessageTracking> findByCampaignIdAndEventType(Long campaignId, TrackingEventType eventType);

    List<MessageTracking> findByEventAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT mt FROM MessageTracking mt WHERE " +
            "mt.campaignId = :campaignId AND mt.eventType = 'CLICKED'")
    List<MessageTracking> findClicksByCampaign(Long campaignId);

    @Query("SELECT COUNT(mt) FROM MessageTracking mt WHERE " +
            "mt.campaignId = :campaignId AND mt.eventType = :eventType")
    int countByEvent(Long campaignId, TrackingEventType eventType);

    boolean existsByMessageIdAndEventType(Long messageId, TrackingEventType eventType);
}
