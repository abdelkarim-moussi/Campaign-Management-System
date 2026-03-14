package com.app.cms.analytics.repository;

import com.app.cms.analytics.entity.MessageTracking;
import com.app.cms.analytics.entity.TrackingEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageTrackingRepository extends JpaRepository<MessageTracking,Long> {
    List<MessageTracking> findByMessageIdAndOrganizationId(Long messageId, Long orgId);

    List<MessageTracking> findByCampaignIdAndOrganizationId(Long campaignId, Long orgId);

    List<MessageTracking> findByContactIdAndOrganizationId(Long contactId, Long orgId);

    List<MessageTracking> findByEventTypeAndOrganizationId(TrackingEventType eventType, Long orgId);

    List<MessageTracking> findByCampaignIdAndEventTypeAndOrganizationId(Long campaignId, TrackingEventType eventType, Long orgId);

    List<MessageTracking> findByEventAtBetweenAndOrganizationId(LocalDateTime start, LocalDateTime end, Long orgId);

    @Query("SELECT mt FROM MessageTracking mt WHERE " +
            "mt.campaignId = :campaignId AND mt.eventType = 'CLICKED'" +
            "AND mt.organizationId = :orgId")
    List<MessageTracking> findClicksByCampaignAndOrganizationId(Long campaignId, Long orgId);

    @Query("SELECT COUNT(mt) FROM MessageTracking mt WHERE " +
            "mt.campaignId = :campaignId AND mt.eventType = :eventType" +
            " AND mt.organizationId = :orgId")
    int countByEventAndOrganizationId(Long campaignId, TrackingEventType eventType, Long orgId);

    boolean existsByMessageIdAndEventTypeAndOrganizationId(Long messageId, TrackingEventType eventType,Long orgId);
}
