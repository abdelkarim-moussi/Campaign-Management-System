package com.app.cms.campaign;

import java.util.List;

public interface CampaignService {
    Campaign createCampaign(CampaignDto dto);
    Campaign getCampaign(Long id);
    List<Campaign> getAllCampaigns();
    List<Campaign> getCampaignsByStatus(CampaignStatus status);
    List<Campaign> searchCampaigns(String keyword);
    Campaign updateCampaign(Long id, CampaignDto dto);
    void deleteCampaign(Long id);
    void addContactsToCampaign(Long campaignId, List<Long> contactIds);
    void removeContactFromCampaign(Long campaignId, Long contactId);
    void sendCampaign(Long campaignId);
    CampaignSummaryDto getCampaignSummary(Long campaignId);
    List<CampaignContact> getCampaignContacts(Long campaignId);
}
