package com.app.cms.campaign;

import com.app.cms.campaign.events.CampaignCreatedEvent;
import com.app.cms.contact.Contact;
import com.app.cms.contact.ContactService;
import com.app.cms.template.Template;
import com.app.cms.template.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    @Transactional
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

        List<Contact> contacts = contactService.getContactsByIds(dto.getContactIds());
        if (contacts.isEmpty()) {
            throw new IllegalArgumentException("No valid contacts found");
        }


        Campaign campaign = Campaign.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .objective(dto.getObjective())
                .channel(dto.getChannel())
                .templateId(dto.getTemplateId())
                .build();


        if (dto.getScheduledAt() != null && dto.getScheduledAt().isAfter(LocalDateTime.now())) {
            campaign.setStatus(CampaignStatus.SCHEDULED);
            campaign.setScheduledAt(dto.getScheduledAt());
        } else {
            campaign.setStatus(CampaignStatus.DRAFT);
        }

        Campaign savedCampaign = campaignRepository.save(campaign);


        for (Contact contact : contacts) {
            CampaignContact cc = CampaignContact.builder()
                    .campaign(savedCampaign)
                    .contact(contact)
                    .status(MessageStatus.PENDING)
                    .build();

            campaignContactRepository.save(cc);
        }

        log.info("Campaign created with ID: {} and {} contacts",
                savedCampaign.getId(), contacts.size());

        eventPublisher.publishEvent(new CampaignCreatedEvent(
                savedCampaign.getId(),
                savedCampaign.getName(),
                savedCampaign.getCreatedAt()
        ));

        return savedCampaign;
    }

    @Override
    public Campaign getCampaign(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found: " + id));

        campaign.setTemplate(templateService.getTemplate(campaign.getTemplateId()));

        return campaign;
    }

    @Override
    public List<Campaign> getAllCampaigns() {
        return campaignRepository.findAll();
    }

    @Override
    public List<Campaign> getCampaignsByStatus(CampaignStatus status) {
        return campaignRepository.findByStatus(status);
    }

    @Override
    public List<Campaign> searchCampaigns(String keyword) {
        return campaignRepository.searchCampaigns(keyword);
    }

    @Override
    public Campaign updateCampaign(Long id, CampaignDto dto) {
        log.info("Updating campaign: {}", id);

        Campaign campaign = getCampaign(id);

        if (campaign.getStatus() != CampaignStatus.DRAFT &&
                campaign.getStatus() != CampaignStatus.SCHEDULED) {
            throw new IllegalStateException("Cannot update campaign in status: " + campaign.getStatus());
        }

        campaign.setName(dto.getName());
        campaign.setDescription(dto.getDescription());
        campaign.setObjective(dto.getObjective());
        campaign.setScheduledAt(dto.getScheduledAt());

        return campaignRepository.save(campaign);
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
