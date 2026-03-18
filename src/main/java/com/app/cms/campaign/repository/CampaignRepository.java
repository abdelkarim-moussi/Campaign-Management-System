package com.app.cms.campaign.repository;

import com.app.cms.campaign.entity.CampaignStatus;
import com.app.cms.campaign.entity.Campaign;
import com.app.cms.campaign.entity.CampaignChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository extends JpaRepository<Campaign, Long> {

    Optional<Campaign> findByIdAndOrganizationId(Long campId, Long orgId);

    List<Campaign> findAllByOrganizationId(Long orgId);

    Page<Campaign> findAllByOrganizationId(Long orgId, Pageable pageable);

    List<Campaign> findByStatusAndOrganizationId(CampaignStatus status, Long orgId);

    Page<Campaign> findByStatusAndOrganizationId(CampaignStatus status, Long orgId, Pageable pageable);

    List<Campaign> findByChannelAndOrganizationId(CampaignChannel channel, Long orgId);

    List<Campaign> findByCreatedByAndOrganizationId(Long userId, Long orgId);

    @Query("SELECT c FROM Campaign c WHERE c.status = 'SCHEDULED' AND c.scheduledAt <= :now AND c.organizationId = :orgId")
    List<Campaign> findScheduledCampaignsToSendByOrganizationId(LocalDateTime now, Long orgId);

    @Query("SELECT c FROM Campaign c WHERE c.status = 'SCHEDULED' AND c.scheduledAt <= :now")
    List<Campaign> findScheduledCampaignsToSend(LocalDateTime now);

    @Query("SELECT c FROM Campaign c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%',:keyword,'%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%',:keyword,'%') ) " +
            "AND c.organizationId = :orgId")
    List<Campaign> searchCampaignsByOrganizationId(String keyword, Long orgId);

    @Query("SELECT c FROM Campaign c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%',:keyword,'%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%',:keyword,'%') ) " +
            "AND c.organizationId = :orgId")
    Page<Campaign> searchCampaignsByOrganizationId(String keyword, Long orgId, Pageable pageable);

    boolean existsByNameAndOrganizationId(String name, Long orgId);

    @org.springframework.data.jpa.repository.Modifying
    @Query("DELETE FROM Campaign c WHERE c.id = :id AND c.organizationId = :orgId")
    void deleteByIdAndOrganizationId(@org.springframework.data.repository.query.Param("id") Long id,
            @org.springframework.data.repository.query.Param("orgId") Long orgId);
}
