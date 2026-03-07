package com.app.cms.campaign;

import com.app.cms.contact.Contact;
import com.app.cms.contact.ContactService;
import com.app.cms.template.Template;
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
        log.info("Creating campaign: {}", dto.getName());

        if (campaignRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Campaign name already exists: " + dto.getName());
        }

        Template template = templateService.getTemplate(dto.getTemplateId());
        
        if (dto.getChannel() == CampaignChannel.EMAIL && !template.isEmail()) {
            throw new IllegalArgumentException("Email channel requires an email template");
        }
        if (dto.getChannel() == CampaignChannel.SMS && !template.isSms()) {
            throw new IllegalArgumentException("SMS channel requires an SMS template");
        }

        // Vérifier que les contacts existent
        List<Contact> contacts = contactService.getContactsByIds(dto.getContactIds());
        if (contacts.isEmpty()) {
            throw new IllegalArgumentException("No valid contacts found");
        }

        // Créer la campagne
        Campaign campaign = new Campaign();
        campaign.setName(dto.getName());
        campaign.setDescription(dto.getDescription());
        campaign.setObjective(dto.getObjective());
        campaign.setChannel(dto.getChannel());
        campaign.setTemplateId(dto.getTemplateId());

        // Définir le statut et la date
        if (dto.getScheduledAt() != null && dto.getScheduledAt().isAfter(LocalDateTime.now())) {
            campaign.setStatus(CampaignStatus.SCHEDULED);
            campaign.setScheduledAt(dto.getScheduledAt());
        } else {
            campaign.setStatus(CampaignStatus.DRAFT);
        }

        Campaign savedCampaign = campaignRepository.save(campaign);

        // Associer les contacts
        for (Contact contact : contacts) {
            CampaignContact cc = new CampaignContact();
            cc.setCampaign(savedCampaign);
            cc.setContact(contact);
            cc.setStatus(MessageStatus.PENDING);
            campaignContactRepository.save(cc);
        }

        log.info("Campaign created with ID: {} and {} contacts",
                savedCampaign.getId(), contacts.size());

        // Publier un event
        eventPublisher.publishEvent(new CampaignCreatedEvent(
                savedCampaign.getId(),
                savedCampaign.getName(),
                savedCampaign.getCreatedAt()
        ));

        return savedCampaign;
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
