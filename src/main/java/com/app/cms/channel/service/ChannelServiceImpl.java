package com.app.cms.channel.service;

import com.app.cms.channel.config.EmailConfig;
import com.app.cms.channel.config.SmsConfig;
import com.app.cms.channel.dto.EmailDto;
import com.app.cms.channel.dto.SendResult;
import com.app.cms.channel.dto.SmsDto;
import com.app.cms.channel.entity.MessageSent;
import com.app.cms.channel.entity.MessageStatus;
import com.app.cms.channel.entity.MessageType;
import com.app.cms.channel.events.MessageSentEvent;
import com.app.cms.channel.adapter.PostmarkAdapter;
import com.app.cms.channel.adapter.SmtpAdapter;
import com.app.cms.channel.adapter.TwilioAdapter;
import com.app.cms.channel.repository.MessageSentRepository;
import com.app.cms.common.security.OrganizationContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelServiceImpl implements ChannelService {
    private final MessageSentRepository messageSentRepository;
    private final PostmarkAdapter postmarkAdapter;
    private final SmtpAdapter smtpAdapter;
    private final TwilioAdapter twilioAdapter;
    private final EmailConfig emailConfig;
    private final SmsConfig smsConfig;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public SendResult sendEmail(EmailDto emailDto) {
        log.info("Sending email to: {}", emailDto.getTo());

        MessageSent message = setEmail(emailDto);

        MessageSent savedMessage = messageSentRepository.save(message);

        SendResult result = switch (emailConfig.getProvider().toLowerCase()) {
            case "postmark" -> postmarkAdapter.send(emailDto);
            case "smtp" -> smtpAdapter.send(emailDto);
            default -> new SendResult(false, null, null,
                    "Unknown email provider: " + emailConfig.getProvider());
        };

        if (result.isSuccess()) {
            savedMessage.setStatus(MessageStatus.SENT);
            savedMessage.setExternalId(result.getExternalId());
            savedMessage.setSentAt(LocalDateTime.now());
            log.info("Email sent successfully. Message ID: {}", savedMessage.getId());
        } else {
            savedMessage.setStatus(MessageStatus.FAILED);
            savedMessage.setErrorMessage(result.getErrorMessage());
            savedMessage.setSentAt(LocalDateTime.now());
            log.error("Failed to send email: {}", result.getErrorMessage());
        }

        messageSentRepository.save(savedMessage);

        // Publish Event
        eventPublisher.publishEvent(new MessageSentEvent(
                savedMessage.getId(),
                savedMessage.getOrganizationId(),
                savedMessage.getCampaignId(),
                savedMessage.getContactId(),
                MessageType.EMAIL,
                savedMessage.getRecipient(),
                result.isSuccess(),
                savedMessage.getSentAt()));

        result.setMessageId(savedMessage.getId());
        return result;
    }

    private MessageSent setEmail(EmailDto emailDto) {
        Long organizationId = OrganizationContext.getOrganizationId();

        MessageSent message = new MessageSent();
        message.setOrganizationId(organizationId);
        message.setCampaignId(emailDto.getCampaignId());
        message.setContactId(emailDto.getContactId());
        message.setType(MessageType.EMAIL);
        message.setStatus(MessageStatus.PENDING);
        message.setRecipient(emailDto.getTo());
        message.setSubject(emailDto.getSubject());
        message.setContent(emailDto.getContent());
        message.setProvider(emailConfig.getProvider().toUpperCase());
        return message;
    }

    @Transactional
    public SendResult sendSms(SmsDto smsDto) {
        log.info("Sending SMS to: {}", smsDto.getTo());

        MessageSent message = setSms(smsDto);

        MessageSent savedMessage = messageSentRepository.save(message);

        SendResult result = switch (smsConfig.getProvider().toLowerCase()) {
            case "twilio" -> twilioAdapter.send(smsDto);
            default -> new SendResult(false, null, null, "Unknown SMS provider");
        };

        if (result.isSuccess()) {
            savedMessage.setStatus(MessageStatus.SENT);
            savedMessage.setExternalId(result.getExternalId());
            savedMessage.setSentAt(LocalDateTime.now());
            log.info("SMS sent successfully. Message ID: {}", savedMessage.getId());
        } else {
            savedMessage.setStatus(MessageStatus.FAILED);
            savedMessage.setErrorMessage(result.getErrorMessage());
            log.error("Failed to send SMS: {}", result.getErrorMessage());
        }

        messageSentRepository.save(savedMessage);

        eventPublisher.publishEvent(new MessageSentEvent(
                savedMessage.getId(),
                savedMessage.getOrganizationId(),
                savedMessage.getCampaignId(),
                savedMessage.getContactId(),
                MessageType.SMS,
                savedMessage.getRecipient(),
                result.isSuccess(),
                savedMessage.getSentAt()));

        result.setMessageId(savedMessage.getId());
        return result;
    }

    private MessageSent setSms(SmsDto smsDto) {
        Long organizationId = OrganizationContext.getOrganizationId();

        MessageSent message = new MessageSent();
        message.setOrganizationId(organizationId);
        message.setCampaignId(smsDto.getCampaignId());
        message.setContactId(smsDto.getContactId());
        message.setType(MessageType.SMS);
        message.setStatus(MessageStatus.PENDING);
        message.setRecipient(smsDto.getTo());
        message.setContent(smsDto.getContent());
        message.setProvider(smsConfig.getProvider().toUpperCase());
        return message;
    }

    public MessageSent getMessage(Long id) {
        Long organizationId = OrganizationContext.getOrganizationId();

        return messageSentRepository.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Message not found: " + id));
    }

    public List<MessageSent> getMessagesByCampaign(Long campaignId) {
        Long organizationId = OrganizationContext.getOrganizationId();

        return messageSentRepository.findByCampaignIdAndOrganizationId(campaignId, organizationId);
    }

    public Page<MessageSent> getMessagesByCampaign(Long campaignId, Pageable pageable) {
        Long organizationId = OrganizationContext.getOrganizationId();

        return messageSentRepository.findByCampaignIdAndOrganizationId(campaignId, organizationId, pageable);
    }

    public List<MessageSent> getMessagesByContact(Long contactId) {
        Long organizationId = OrganizationContext.getOrganizationId();

        return messageSentRepository.findByContactIdAndOrganizationId(contactId, organizationId);
    }

    public Page<MessageSent> getMessagesByContact(Long contactId, Pageable pageable) {
        Long organizationId = OrganizationContext.getOrganizationId();

        return messageSentRepository.findByContactIdAndOrganizationId(contactId, organizationId, pageable);
    }

    @Transactional
    public void updateMessageStatus(String externalId, MessageStatus newStatus) {
        Long organizationId = OrganizationContext.getOrganizationId();

        messageSentRepository.findByExternalIdAndOrganizationId(externalId, organizationId).ifPresent(message -> {
            MessageStatus oldStatus = message.getStatus();
            message.setStatus(newStatus);

            switch (newStatus) {
                case SENT:
                    message.setSentAt(LocalDateTime.now());
                    break;
                case DELIVERED:
                    message.setDeliveredAt(LocalDateTime.now());
                    break;
                case OPENED:
                    message.setOpenedAt(LocalDateTime.now());
                    break;
                case CLICKED:
                    message.setClickedAt(LocalDateTime.now());
                    break;
                case FAILED:
                    message.setErrorMessage("Failed to send message");
                    break;
            }

            messageSentRepository.save(message);
            log.info("Message {} status updated: {} -> {}", externalId, oldStatus, newStatus);
        });
    }
}
