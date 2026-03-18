package com.app.cms.campaign.repository;

import com.app.cms.campaign.entity.MessageStatus;
import com.app.cms.campaign.entity.CampaignContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CampaignContactRepository extends JpaRepository<CampaignContact, Long> {

    List<CampaignContact> findByCampaignIdAndOrganizationId(Long campaignId,Long orgId);

    Page<CampaignContact> findByCampaignIdAndOrganizationId(Long campaignId, Long orgId, Pageable pageable);

    List<CampaignContact> findByContactIdAndOrganizationId(Long contactId,Long orgId);

    List<CampaignContact> findByCampaignIdAndStatusAndOrganizationId(Long campaignId, MessageStatus status, Long orgId);

    @Query("SELECT cc FROM CampaignContact cc WHERE " +
            "cc.campaign.id = :campaignId AND cc.contact.id = :contactId AND cc.organizationId = :orgId")
    CampaignContact findByCampaignIdAndContactIdAndOrganizationId(Long campaignId, Long contactId, Long orgId);

    boolean existsByCampaignIdAndContactIdAndOrganizationId(Long campaignId, Long contactId, Long orgId);

    @Query("SELECT COUNT(cc) FROM CampaignContact cc WHERE cc.campaign.id = :campaignId" +
            " AND cc.organizationId = :orgId")
    int countByCampaignIdAndOrganizationId(Long campaignId,Long orgId);

    @Query("SELECT COUNT(cc) FROM CampaignContact cc WHERE " +
            "cc.campaign.id = :campaignId AND cc.status = :status " +
            "AND cc.organizationId = :orgId")
    int countByCampaignIdAndStatusAndOrganizationId(Long campaignId, MessageStatus status, Long orgId);
}
