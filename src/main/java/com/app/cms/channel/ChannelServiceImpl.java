package com.app.cms.channel;

import com.app.cms.channel.config.EmailConfig;
import com.app.cms.channel.config.SmsConfig;
import com.app.cms.channel.events.MessageSentEvent;
import com.app.cms.channel.internal.PostmarkAdapter;
import com.app.cms.channel.internal.SmtpAdapter;
import com.app.cms.channel.internal.TwilioAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChannelServiceImpl implements ChannelService{
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

        MessageSent message = new MessageSent();
        message.setCampaignId(emailDto.getCampaignId());
        message.setContactId(emailDto.getContactId());
        message.setType(MessageType.EMAIL);
        message.setStatus(MessageStatus.PENDING);
        message.setRecipient(emailDto.getTo());
        message.setSubject(emailDto.getSubject());
        message.setContent(emailDto.getContent());
        message.setProvider(emailConfig.getProvider().toUpperCase());

        MessageSent savedMessage = messageSentRepository.save(message);

        // Send via provider
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
            log.error("Failed to send email: {}", result.getErrorMessage());
        }

        messageSentRepository.save(savedMessage);

        // Publish Event
        eventPublisher.publishEvent(new MessageSentEvent(
                savedMessage.getId(),
                savedMessage.getCampaignId(),
                savedMessage.getContactId(),
                MessageType.EMAIL,
                savedMessage.getRecipient(),
                result.isSuccess(),
                savedMessage.getSentAt()
        ));

        result.setMessageId(savedMessage.getId());
        return result;
    }
}
