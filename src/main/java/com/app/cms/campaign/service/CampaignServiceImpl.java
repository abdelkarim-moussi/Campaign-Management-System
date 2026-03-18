package com.app.cms.campaign.service;

import com.app.cms.campaign.entity.CampaignStatus;
import com.app.cms.campaign.dto.CampaignSummaryDto;
import com.app.cms.campaign.entity.MessageStatus;
import com.app.cms.campaign.dto.CampaignDto;
import com.app.cms.campaign.entity.Campaign;
import com.app.cms.campaign.entity.CampaignChannel;
import com.app.cms.campaign.entity.CampaignContact;
import com.app.cms.campaign.events.CampaignCreatedEvent;
import com.app.cms.campaign.events.CampaignSentEvent;
import com.app.cms.campaign.repository.CampaignContactRepository;
import com.app.cms.campaign.repository.CampaignRepository;
import com.app.cms.channel.service.ChannelService;
import com.app.cms.channel.dto.EmailDto;
import com.app.cms.channel.dto.SendResult;
import com.app.cms.channel.dto.SmsDto;
import com.app.cms.common.security.OrganizationContext;
import com.app.cms.contact.entity.Contact;
import com.app.cms.contact.service.ContactService;
import com.app.cms.template.entity.Template;
import com.app.cms.template.dto.TemplatePreviewResult;
import com.app.cms.template.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ChannelService channelService;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Campaign createCampaign(CampaignDto dto) {
        log.info("Creating campaign: {}", dto.getName());

        Long organizationId = OrganizationContext.getOrganizationId();

        if (campaignRepository.existsByNameAndOrganizationId(dto.getName(), organizationId)) {
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
        campaign.setOrganizationId(organizationId);
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
            cc.setOrganizationId(organizationId);
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
                organizationId,
                savedCampaign.getName(),
                savedCampaign.getCreatedAt()));

        return savedCampaign;
    }

    @Override
    public Campaign getCampaign(Long id) {

        Long organizationId = OrganizationContext.getOrganizationId();

        Campaign campaign = campaignRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Campaign not found: " + id));

        campaign.setTemplate(templateService.getTemplate(campaign.getTemplateId()));

        return campaign;
    }

    @Override
    public List<Campaign> getAllCampaigns() {

        Long organizationId = OrganizationContext.getOrganizationId();

        return campaignRepository.findAllByOrganizationId(organizationId);
    }

    @Override
    public Page<Campaign> getAllCampaigns(Pageable pageable) {

        Long organizationId = OrganizationContext.getOrganizationId();

        return campaignRepository.findAllByOrganizationId(organizationId, pageable);
    }

    @Override
    public List<Campaign> getCampaignsByStatus(CampaignStatus status) {

        Long organizationId = OrganizationContext.getOrganizationId();

        return campaignRepository.findByStatusAndOrganizationId(status, organizationId);
    }

    @Override
    public Page<Campaign> getCampaignsByStatus(CampaignStatus status, Pageable pageable) {

        Long organizationId = OrganizationContext.getOrganizationId();

        return campaignRepository.findByStatusAndOrganizationId(status, organizationId, pageable);
    }

    @Override
    public List<Campaign> searchCampaigns(String keyword) {

        Long organizationId = OrganizationContext.getOrganizationId();

        return campaignRepository.searchCampaignsByOrganizationId(keyword, organizationId);
    }

    @Override
    public Page<Campaign> searchCampaigns(String keyword, Pageable pageable) {

        Long organizationId = OrganizationContext.getOrganizationId();

        return campaignRepository.searchCampaignsByOrganizationId(keyword, organizationId, pageable);
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

        Long organizationId = OrganizationContext.getOrganizationId();
        campaignRepository.deleteByIdAndOrganizationId(id, organizationId);
    }

    @Override
    @Transactional
    public void addContactsToCampaign(Long campaignId, List<Long> contactIds) {
        log.info("Adding {} contacts to campaign {}", contactIds.size(), campaignId);

        Long organizationId = OrganizationContext.getOrganizationId();

        Campaign campaign = getCampaign(campaignId);

        if (!campaign.isDraft()) {
            throw new IllegalStateException("Can only add contacts to DRAFT campaigns");
        }

        List<Contact> contacts = contactService.getContactsByIds(contactIds);

        for (Contact contact : contacts) {

            if (!campaignContactRepository.existsByCampaignIdAndContactIdAndOrganizationId(campaignId, contact.getId(),
                    organizationId)) {
                CampaignContact cc = CampaignContact.builder()
                        .organizationId(organizationId)
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

        Long organizationId = OrganizationContext.getOrganizationId();

        Campaign campaign = getCampaign(campaignId);

        if (!campaign.isDraft()) {
            throw new IllegalStateException("Can only remove contacts from DRAFT campaigns");
        }

        CampaignContact cc = campaignContactRepository
                .findByCampaignIdAndContactIdAndOrganizationId(campaignId, contactId, organizationId);

        if (cc != null) {
            campaignContactRepository.delete(cc);
        }
    }

    @Override
    @Transactional
    public void sendCampaign(Long campaignId) {
        log.info("Starting to send campaign: {}", campaignId);

        Long organizationId = OrganizationContext.getOrganizationId();

        Campaign campaign = getCampaign(campaignId);

        if (!campaign.canBeSent()) {
            throw new IllegalStateException("Campaign cannot be sent in status: " + campaign.getStatus());
        }

        campaign.setStatus(CampaignStatus.SENDING);
        campaignRepository.save(campaign);

        Template template = templateService.getTemplate(campaign.getTemplateId());

        List<CampaignContact> campaignContacts = campaignContactRepository.findByCampaignIdAndOrganizationId(campaignId,
                organizationId);

        log.info("Sending campaign to {} contacts", campaignContacts.size());

        int successCount = 0;
        int failureCount = 0;

        for (CampaignContact cc : campaignContacts) {
            try {

                Contact contact = cc.getContact();
                cc.setOrganizationId(organizationId);

                Map<String, String> variables = new HashMap<>();
                variables.put("firstName", contact.getFirstName());
                variables.put("lastName", contact.getLastName());
                variables.put("email", contact.getEmail());
                variables.put("company", contact.getCompany() != null ? contact.getCompany() : "");

                TemplatePreviewResult processed = templateService.processTemplateForCampaign(template.getId(),
                        variables);

                SendResult result;

                if (campaign.getChannel() == CampaignChannel.EMAIL) {
                    EmailDto emailDto = new EmailDto(
                            campaign.getId(),
                            contact.getId(),
                            contact.getEmail(),
                            processed.getSubject(),
                            processed.getContent(),
                            null,
                            null);
                    try {
                        Thread.sleep(1100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    result = channelService.sendEmail(emailDto);

                } else if (campaign.getChannel() == CampaignChannel.SMS) {
                    SmsDto smsDto = new SmsDto(
                            campaign.getId(),
                            contact.getId(),
                            contact.getPhone(),
                            processed.getContent(),
                            null);
                    result = channelService.sendSms(smsDto);

                } else {
                    throw new IllegalArgumentException("Unsupported channel: " + campaign.getChannel());
                }

                if (result.isSuccess()) {
                    cc.setStatus(MessageStatus.SENT);
                    cc.setMessageSentId(result.getMessageId());
                    cc.setSentAt(LocalDateTime.now());
                    successCount++;
                } else {
                    cc.setStatus(MessageStatus.FAILED);
                    failureCount++;
                }

                campaignContactRepository.save(cc);

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
                organizationId,
                campaign.getName(),
                campaign.getChannel(),
                campaignContacts.size(),
                campaign.getSentAt()));
    }

    @Override
    public CampaignSummaryDto getCampaignSummary(Long campaignId) {

        Long organizationId = OrganizationContext.getOrganizationId();

        Campaign campaign = getCampaign(campaignId);

        int total = campaignContactRepository.countByCampaignIdAndOrganizationId(campaignId, organizationId);
        int sent = campaignContactRepository.countByCampaignIdAndStatusAndOrganizationId(campaignId, MessageStatus.SENT,
                organizationId);
        int delivered = campaignContactRepository.countByCampaignIdAndStatusAndOrganizationId(campaignId,
                MessageStatus.DELIVERED, organizationId);
        int opened = campaignContactRepository.countByCampaignIdAndStatusAndOrganizationId(campaignId,
                MessageStatus.OPENED, organizationId);
        int failed = campaignContactRepository.countByCampaignIdAndStatusAndOrganizationId(campaignId,
                MessageStatus.FAILED, organizationId);

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

        Long organizationId = OrganizationContext.getOrganizationId();

        return campaignContactRepository.findByCampaignIdAndOrganizationId(campaignId, organizationId);
    }

    @Override
    public Page<CampaignContact> getCampaignContacts(Long campaignId, Pageable pageable) {

        Long organizationId = OrganizationContext.getOrganizationId();

        return campaignContactRepository.findByCampaignIdAndOrganizationId(campaignId, organizationId, pageable);
    }
}
