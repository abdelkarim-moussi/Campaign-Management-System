package com.app.cms.campaign;

import com.app.cms.campaign.events.CampaignCreatedEvent;
import com.app.cms.campaign.events.CampaignSentEvent;
import com.app.cms.contact.Contact;
import com.app.cms.contact.ContactService;
import com.app.cms.template.Template;
import com.app.cms.template.TemplatePreviewResult;
import com.app.cms.template.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignServiceImpl implements CampaignService {

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

        Campaign campaign = new Campaign();
        campaign.setName(dto.getName());
        campaign.setDescription(dto.getDescription());
        campaign.setObjective(dto.getObjective());
        campaign.setChannel(dto.getChannel());
        campaign.setTemplateId(dto.getTemplateId());

        if (dto.getScheduledAt() != null && dto.getScheduledAt().isAfter(LocalDateTime.now())) {
            campaign.setStatus(CampaignStatus.SCHEDULED);
            campaign.setScheduledAt(dto.getScheduledAt());
        } else {
            campaign.setStatus(CampaignStatus.DRAFT);
        }

        Campaign savedCampaign = campaignRepository.save(campaign);

        for (Contact contact : contacts) {
            CampaignContact cc = new CampaignContact();
            cc.setCampaign(savedCampaign);
            cc.setContact(contact);
            cc.setStatus(MessageStatus.PENDING);

            campaignContactRepository.save(cc);
            savedCampaign.getCampaignContacts().add(cc);
        }

        log.info("Campaign created with ID: {} and {} contacts",
                savedCampaign.getId(), contacts.size());

        eventPublisher.publishEvent(new CampaignCreatedEvent(
                savedCampaign.getId(),
                savedCampaign.getName(),
                savedCampaign.getCreatedAt()));

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
    @Transactional
    public void deleteCampaign(Long id) {
        log.info("Deleting campaign: {}", id);

        Campaign campaign = getCampaign(id);

        if (campaign.getStatus() != CampaignStatus.DRAFT) {
            throw new IllegalStateException("Cannot delete campaign in status: " + campaign.getStatus());
        }

        campaignRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addContactsToCampaign(Long campaignId, List<Long> contactIds) {
        log.info("Adding {} contacts to campaign {}", contactIds.size(), campaignId);

        Campaign campaign = getCampaign(campaignId);

        if (!campaign.isDraft()) {
            throw new IllegalStateException("Can only add contacts to DRAFT campaigns");
        }

        List<Contact> contacts = contactService.getContactsByIds(contactIds);

        for (Contact contact : contacts) {

            if (!campaignContactRepository.existsByCampaignIdAndContactId(campaignId, contact.getId())) {
                CampaignContact cc = CampaignContact.builder()
                        .campaign(campaign)
                        .contact(contact)
                        .status(MessageStatus.PENDING)
                        .build();

                campaignContactRepository.save(cc);
            }
        }
    }

    @Override
    @Transactional
    public void removeContactFromCampaign(Long campaignId, Long contactId) {
        log.info("Removing contact {} from campaign {}", contactId, campaignId);

        Campaign campaign = getCampaign(campaignId);

        if (!campaign.isDraft()) {
            throw new IllegalStateException("Can only remove contacts from DRAFT campaigns");
        }

        CampaignContact cc = campaignContactRepository
                .findByCampaignIdAndContactId(campaignId, contactId);

        if (cc != null) {
            campaignContactRepository.delete(cc);
        }
    }

    @Override
    @Transactional
    public void sendCampaign(Long campaignId) {
        log.info("Starting to send campaign: {}", campaignId);

        Campaign campaign = getCampaign(campaignId);

        if (!campaign.canBeSent()) {
            throw new IllegalStateException("Campaign cannot be sent in status: " + campaign.getStatus());
        }

        campaign.setStatus(CampaignStatus.SENDING);
        campaignRepository.save(campaign);

        Template template = templateService.getTemplate(campaign.getTemplateId());

        List<CampaignContact> campaignContacts = campaignContactRepository.findByCampaignId(campaignId);

        log.info("Sending campaign to {} contacts", campaignContacts.size());

        int successCount = 0;
        int failureCount = 0;

        for (CampaignContact cc : campaignContacts) {
            try {

                Contact contact = cc.getContact();
                Map<String, String> variables = new HashMap<>();
                variables.put("firstName", contact.getFirstName());
                variables.put("lastName", contact.getLastName());
                variables.put("email", contact.getEmail());
                variables.put("company", contact.getCompany() != null ? contact.getCompany() : "");

                TemplatePreviewResult processed = templateService.processTemplateForCampaign(template.getId(),
                        variables);

                log.info("Sending to {}: {}", contact.getEmail(), processed.getContent());

                cc.setStatus(MessageStatus.SENT);
                cc.setSentAt(LocalDateTime.now());
                campaignContactRepository.save(cc);

                successCount++;

            } catch (Exception e) {
                log.error("Failed to send to contact {}: {}", cc.getContact().getId(), e.getMessage());
                cc.setStatus(MessageStatus.FAILED);
                campaignContactRepository.save(cc);
                failureCount++;
            }
        }

        campaign.setStatus(CampaignStatus.SENT);
        campaign.setSentAt(LocalDateTime.now());
        campaignRepository.save(campaign);

        log.info("Campaign {} sent: {} success, {} failures",
                campaignId, successCount, failureCount);

        eventPublisher.publishEvent(new CampaignSentEvent(
                campaign.getId(),
                campaign.getName(),
                campaign.getChannel(),
                campaignContacts.size(),
                campaign.getSentAt()));
    }

    @Override
    public CampaignSummaryDto getCampaignSummary(Long campaignId) {
        Campaign campaign = getCampaign(campaignId);

        int total = campaignContactRepository.countByCampaignId(campaignId);
        int sent = campaignContactRepository.countByCampaignIdAndStatus(campaignId, MessageStatus.SENT);
        int delivered = campaignContactRepository.countByCampaignIdAndStatus(campaignId, MessageStatus.DELIVERED);
        int opened = campaignContactRepository.countByCampaignIdAndStatus(campaignId, MessageStatus.OPENED);
        int failed = campaignContactRepository.countByCampaignIdAndStatus(campaignId, MessageStatus.FAILED);

        return new CampaignSummaryDto(
                campaign.getId(),
                campaign.getName(),
                campaign.getStatus(),
                campaign.getChannel(),
                total,
                sent,
                delivered,
                opened,
                failed,
                campaign.getScheduledAt(),
                campaign.getSentAt(),
                campaign.getCreatedAt());
    }

    @Override
    public List<CampaignContact> getCampaignContacts(Long campaignId) {
        return campaignContactRepository.findByCampaignId(campaignId);
    }
}
