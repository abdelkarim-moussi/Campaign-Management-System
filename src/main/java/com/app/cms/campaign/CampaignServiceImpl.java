package com.app.cms.campaign;

import com.app.cms.contact.ContactService;
import com.app.cms.template.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignServiceImpl implements CampaignService{

    private final CampaignRepository campaignRepository;
    private final CampaignContactRepository campaignContactRepository;

    private final ContactService contactService;
    private final TemplateService templateService;

    private final ApplicationEventPublisher eventPublisher;


    @Override
    public Campaign createCampaign(CampaignDto dto) {
        return null;
    }

    @Override
    public Campaign getCampaign() {
        return null;
    }

    @Override
    public List<Campaign> getAllCampaigns() {
        return List.of();
    }

    @Override
    public List<Campaign> getCampaignsByStatus(CampaignStatus status) {
        return List.of();
    }

    @Override
    public List<Campaign> searchCampaigns(String keyword) {
        return List.of();
    }

    @Override
    public Campaign updateCampaign(Long id, CampaignDto dto) {
        return null;
    }

    @Override
    public void deleteCampaign(Long id) {

    }

    @Override
    public void addContactsToCampaign(Long campaignId, List<Long> contactIds) {

    }

    @Override
    public void removeContactFromCampaign(Long campaignId, Long contactId) {

    }

    @Override
    public void sendCampaign(Long campaignId) {

    }

    @Override
    public CampaignSummaryDto getCampaignSummary(Long campaignId) {
        return null;
    }

    @Override
    public List<CampaignContact> getCampaignContacts(Long campaignId) {
        return List.of();
    }
}
