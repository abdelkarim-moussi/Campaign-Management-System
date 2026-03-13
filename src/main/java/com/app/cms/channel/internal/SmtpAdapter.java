package com.app.cms.channel.internal;

import com.app.cms.channel.EmailDto;
import com.app.cms.channel.SendResult;
import com.app.cms.channel.config.EmailConfig;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpAdapter {
    private final EmailConfig emailConfig;
    private final JavaMailSender mailSender;

    public SendResult send(EmailDto emailDto) {
        int maxRetries = 3;
        int retryCount = 0;
        int delayMs = 2000;

        while (retryCount < maxRetries) {
            try {
                log.info("Sending email via SMTP to: {} (Attempt {})", emailDto.getTo(), retryCount + 1);

                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(emailConfig.getFromEmail(), emailConfig.getFromName());
                helper.setTo(emailDto.getTo());
                helper.setSubject(emailDto.getSubject());
                helper.setText(emailDto.getContent(), true);

                mailSender.send(message);

                String messageId = "mailtrap-" + UUID.randomUUID().toString();
                log.info("Email sent successfully via SMTP. MessageID: {}", messageId);
                return new SendResult(true, null, messageId, null);

            } catch (Exception e) {
                String errorMsg = e.getMessage();
                log.warn("Attempt {} failed to send email to {}: {}", retryCount + 1, emailDto.getTo(), errorMsg);

                if (errorMsg != null && errorMsg.contains("Too many emails per second")) {
                    retryCount++;
                    if (retryCount < maxRetries) {
                        log.info("Rate limit hit. Retrying in {}ms...", delayMs);
                        try {
                            Thread.sleep(delayMs);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return new SendResult(false, null, null, "Interrupted during retry delay");
                        }
                        delayMs *= 2; // Exponential backoff
                        continue;
                    }
                }
                
                log.error("Failed to send email via SMTP after {} attempts: {}", retryCount + 1, errorMsg, e);
                return new SendResult(false, null, null, errorMsg);
            }
        }
        return new SendResult(false, null, null, "Max retries exceeded for email delivery");
    }
}
