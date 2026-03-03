package com.app.cms.campaign;

import com.app.cms.campaign.domain.Campaign;
import com.app.cms.campaign.domain.CampaignChannel;
import com.app.cms.campaign.domain.CampaignStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface CampaignRepository extends JpaRepository<Campaign,Long> {

    List<Campaign> findByStatus(CampaignStatus status);
    List<Campaign> findByChannel(CampaignChannel channel);
    List<Campaign> findByCreatedBy(Long userId);

    @Query("SELECT c FROM Campaign c WHERE c.status = 'SCHEDULED' AND c.scheduledAt <= :now")
    List<Campaign> findScheduledCampaignsToSend(LocalDateTime now);

    @Query("SELECT c FROM Campaign c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%',:keyword,'%')) " +
            "OR LOWER(c.description) LIKE LOWER(CONCAT('%',:keyword,'%') ) ")
    List<Campaign> searchCampaign(String keyword);

    boolean existsByName(String name);
}
