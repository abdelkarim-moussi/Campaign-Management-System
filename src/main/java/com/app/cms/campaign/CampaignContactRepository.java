package com.app.cms.campaign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CampaignContactRepository extends JpaRepository<CampaignContact, Long> {

    List<CampaignContact> findByCampaignId(Long campaignId);

    List<CampaignContact> findByContactId(Long contactId);

    List<CampaignContact> findByCampaignIdAndStatus(Long campaignId, MessageStatus status);

    @Query("SELECT cc FROM CampaignContact cc WHERE " +
            "cc.campaign.id = :campaignId AND cc.contact.id = :contactId")
    CampaignContact findByCampaignIdAndContactId(Long campaignId, Long contactId);

    boolean existsByCampaignIdAndContactId(Long campaignId, Long contactId);

    @Query("SELECT COUNT(cc) FROM CampaignContact cc WHERE cc.campaign.id = :campaignId")
    int countByCampaignId(Long campaignId);

    @Query("SELECT COUNT(cc) FROM CampaignContact cc WHERE " +
            "cc.campaign.id = :campaignId AND cc.status = :status")
    int countByCampaignIdAndStatus(Long campaignId, MessageStatus status);
}
