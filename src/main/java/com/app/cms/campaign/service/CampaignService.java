package com.app.cms.campaign.service;

import com.app.cms.campaign.entity.CampaignStatus;
import com.app.cms.campaign.dto.CampaignSummaryDto;
import com.app.cms.campaign.dto.CampaignDto;
import com.app.cms.campaign.entity.Campaign;
import com.app.cms.campaign.entity.CampaignContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CampaignService {
    Campaign createCampaign(CampaignDto dto);
    Campaign getCampaign(Long id);
    List<Campaign> getAllCampaigns();
    Page<Campaign> getAllCampaigns(Pageable pageable);
    List<Campaign> getCampaignsByStatus(CampaignStatus status);
    Page<Campaign> getCampaignsByStatus(CampaignStatus status, Pageable pageable);
    List<Campaign> searchCampaigns(String keyword);
    Page<Campaign> searchCampaigns(String keyword, Pageable pageable);
    Campaign updateCampaign(Long id, CampaignDto dto);
    void deleteCampaign(Long id);
    void addContactsToCampaign(Long campaignId, List<Long> contactIds);
    void removeContactFromCampaign(Long campaignId, Long contactId);
    void sendCampaign(Long campaignId);
    CampaignSummaryDto getCampaignSummary(Long campaignId);
    List<CampaignContact> getCampaignContacts(Long campaignId);
    Page<CampaignContact> getCampaignContacts(Long campaignId, Pageable pageable);
}
