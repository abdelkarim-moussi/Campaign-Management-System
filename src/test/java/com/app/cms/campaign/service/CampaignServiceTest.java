package com.app.cms.campaign.service;

import com.app.cms.campaign.dto.CampaignDto;
import com.app.cms.campaign.entity.*;
import com.app.cms.channel.dto.EmailDto;
import com.app.cms.channel.dto.SendResult;
import com.app.cms.channel.dto.SmsDto;
import com.app.cms.channel.service.ChannelService;
import com.app.cms.common.security.OrganizationContext;
import com.app.cms.campaign.events.CampaignCreatedEvent;
import com.app.cms.campaign.events.CampaignSentEvent;
import com.app.cms.campaign.repository.CampaignContactRepository;
import com.app.cms.campaign.repository.CampaignRepository;
import com.app.cms.contact.entity.Contact;
import com.app.cms.contact.service.ContactService;
import com.app.cms.template.dto.TemplatePreviewResult;
import com.app.cms.template.entity.Template;
import com.app.cms.template.entity.TemplateType;
import com.app.cms.template.service.TemplateService;
import com.app.cms.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CampaignServiceTest {

    @Mock private CampaignRepository campaignRepository;
    @Mock private CampaignContactRepository campaignContactRepository;
    @Mock private TemplateService templateService;
    @Mock private ContactService contactService;
    @Mock private ChannelService channelService;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private CampaignServiceImpl campaignService;

    private MockedStatic<OrganizationContext> mockedOrganizationContext;
    private final Long ORG_ID = 1L;
    private final Long CAMPAIGN_ID = 100L;

    @BeforeEach
    void setUp() {
        mockedOrganizationContext = mockStatic(OrganizationContext.class);
        mockedOrganizationContext.when(OrganizationContext::getOrganizationId).thenReturn(ORG_ID);
    }

    @AfterEach
    void tearDown() {
        mockedOrganizationContext.close();
    }

    // --- createCampaign Tests ---

    @Test
    void createCampaign_shouldSucceed_whenDataIsValid() {
        // Arrange
        CampaignDto dto = createValidEmailDto();
        Template template = new Template();
        template.setType(TemplateType.EMAIL);

        when(campaignRepository.existsByNameAndOrganizationId(dto.getName(), ORG_ID)).thenReturn(false);
        when(templateService.getTemplate(dto.getTemplateId())).thenReturn(template);
        when(contactService.getContactsByIds(any())).thenReturn(List.of(new Contact()));
        when(campaignRepository.save(any())).thenAnswer(i -> {
            Campaign c = i.getArgument(0);
            c.setId(CAMPAIGN_ID);
            return c;
        });

        // Act
        Campaign result = campaignService.createCampaign(dto);

        // Assert
        assertNotNull(result);
        assertEquals(CAMPAIGN_ID, result.getId());
        assertEquals(CampaignStatus.DRAFT, result.getStatus());
        verify(eventPublisher).publishEvent(any(CampaignCreatedEvent.class));
    }

    @Test
    void createCampaign_shouldThrowException_whenNameExists() {
        CampaignDto dto = createValidEmailDto();
        when(campaignRepository.existsByNameAndOrganizationId(dto.getName(), ORG_ID)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> campaignService.createCampaign(dto));
    }

    @Test
    void createCampaign_shouldThrowException_whenChannelTemplateMismatch() {
        CampaignDto dto = createValidEmailDto(); // Channel is EMAIL
        Template smsTemplate = new Template();
        smsTemplate.setType(TemplateType.SMS);

        when(campaignRepository.existsByNameAndOrganizationId(anyString(), anyLong())).thenReturn(false);
        when(templateService.getTemplate(any())).thenReturn(smsTemplate);

        assertThrows(IllegalArgumentException.class, () -> campaignService.createCampaign(dto));
    }

    // --- updateCampaign Tests ---

    @Test
    void updateCampaign_shouldSucceed_whenStatusIsDraft() {
        Campaign campaign = new Campaign();
        campaign.setStatus(CampaignStatus.DRAFT);
        CampaignDto dto = createValidEmailDto();

        when(campaignRepository.findByIdAndOrganizationId(CAMPAIGN_ID, ORG_ID)).thenReturn(Optional.of(campaign));
        when(campaignRepository.save(any())).thenReturn(campaign);

        Campaign result = campaignService.updateCampaign(CAMPAIGN_ID, dto);

        assertNotNull(result);
        verify(campaignRepository).save(campaign);
    }

    @Test
    void updateCampaign_shouldThrowException_whenStatusIsSent() {
        Campaign campaign = new Campaign();
        campaign.setStatus(CampaignStatus.SENT);

        when(campaignRepository.findByIdAndOrganizationId(CAMPAIGN_ID, ORG_ID)).thenReturn(Optional.of(campaign));

        assertThrows(IllegalStateException.class, () -> campaignService.updateCampaign(CAMPAIGN_ID, new CampaignDto()));
    }

    // --- sendCampaign Tests ---

    @Test
    void sendCampaign_shouldProcessEmailsCorrectly() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(CAMPAIGN_ID);
        campaign.setChannel(CampaignChannel.EMAIL);
        campaign.setStatus(CampaignStatus.DRAFT);
        campaign.setTemplateId(1L);

        Contact contact = new Contact();
        contact.setId(1L);
        contact.setEmail("test@test.com");
        contact.setFirstName("John");

        CampaignContact cc = new CampaignContact();
        cc.setContact(contact);

        Template template = new Template();
        template.setId(1L);

        TemplatePreviewResult preview = new TemplatePreviewResult("Subject", "Body");

        when(campaignRepository.findByIdAndOrganizationId(CAMPAIGN_ID, ORG_ID)).thenReturn(Optional.of(campaign));
        when(templateService.getTemplate(anyLong())).thenReturn(template);
        when(campaignContactRepository.findByCampaignIdAndOrganizationId(CAMPAIGN_ID, ORG_ID)).thenReturn(List.of(cc));
        when(templateService.processTemplateForCampaign(anyLong(), anyMap())).thenReturn(preview);
        when(channelService.sendEmail(any())).thenReturn(new SendResult(true, 1L, null,""));

        campaignService.sendCampaign(CAMPAIGN_ID);

        assertEquals(CampaignStatus.SENT, campaign.getStatus());
        assertEquals(MessageStatus.SENT, cc.getStatus());
        verify(channelService).sendEmail(any(EmailDto.class));
        verify(eventPublisher).publishEvent(any(CampaignSentEvent.class));
    }

    @Test
    void sendCampaign_shouldHandleSmsCorrectly() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setId(CAMPAIGN_ID);
        campaign.setChannel(CampaignChannel.SMS);
        campaign.setStatus(CampaignStatus.DRAFT);

        Contact contact = new Contact();
        contact.setPhone("123456789");

        CampaignContact cc = new CampaignContact();
        cc.setContact(contact);

        when(campaignRepository.findByIdAndOrganizationId(CAMPAIGN_ID, ORG_ID)).thenReturn(Optional.of(campaign));
        when(templateService.getTemplate(any())).thenReturn(new Template());
        when(campaignContactRepository.findByCampaignIdAndOrganizationId(CAMPAIGN_ID, ORG_ID)).thenReturn(List.of(cc));
        when(templateService.processTemplateForCampaign(any(), any())).thenReturn(new TemplatePreviewResult("Sub", "Content"));
        when(channelService.sendSms(any())).thenReturn(new SendResult(true,1L, null,""));

        campaignService.sendCampaign(CAMPAIGN_ID);

        verify(channelService).sendSms(any(SmsDto.class));
        verify(channelService, never()).sendEmail(any());
    }

    @Test
    void addContactsToCampaign_shouldThrowException_whenNotDraft() {
        Campaign campaign = new Campaign();
        campaign.setStatus(CampaignStatus.SENT);
        when(campaignRepository.findByIdAndOrganizationId(CAMPAIGN_ID, ORG_ID)).thenReturn(Optional.of(campaign));

        assertThrows(IllegalStateException.class, () -> campaignService.addContactsToCampaign(CAMPAIGN_ID, List.of(1L)));
    }

    @Test
    void removeContactFromCampaign_shouldDelete_whenExists() {
        Campaign campaign = new Campaign();
        campaign.setStatus(CampaignStatus.DRAFT);
        CampaignContact cc = new CampaignContact();

        when(campaignRepository.findByIdAndOrganizationId(CAMPAIGN_ID, ORG_ID)).thenReturn(Optional.of(campaign));
        when(campaignContactRepository.findByCampaignIdAndContactIdAndOrganizationId(CAMPAIGN_ID, 1L, ORG_ID)).thenReturn(cc);

        campaignService.removeContactFromCampaign(CAMPAIGN_ID, 1L);

        verify(campaignContactRepository).delete(cc);
    }


    private CampaignDto createValidEmailDto() {
        return CampaignDto.builder()
                .name("Test Campaign")
                .channel(CampaignChannel.EMAIL)
                .templateId(1L)
                .contactIds(List.of(1L))
                .build();
    }
}