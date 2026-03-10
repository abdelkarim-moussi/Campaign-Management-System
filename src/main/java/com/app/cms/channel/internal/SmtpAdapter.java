package com.app.cms.channel.internal;

import com.app.cms.channel.EmailDto;
import com.app.cms.channel.SendResult;
import com.app.cms.channel.config.EmailConfig;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpAdapter {
    private final EmailConfig emailConfig;
    private final JavaMailSender mailSender;

    public SendResult send(EmailDto emailDto) {
        log.info("Sending email via SMTP to: {}", emailDto.getTo());

        try {

            // Create message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(emailConfig.getFromEmail(), emailConfig.getFromName());
            helper.setTo(emailDto.getTo());
            helper.setSubject(emailDto.getSubject());
            helper.setText(emailDto.getContent(), true);  // true = HTML

            // Send
            mailSender.send(message);

            // Generate Local ID
            String messageId = "mailtrap-" + UUID.randomUUID().toString();

            log.info("Email sent successfully via SMTP. MessageID: {}", messageId);
            return new SendResult(true, null, messageId, null);

        } catch (Exception e) {
            log.error("Failed to send email via SMTP: {}", e.getMessage(), e);
            return new SendResult(false, null, null, e.getMessage());
        }
    }
}
