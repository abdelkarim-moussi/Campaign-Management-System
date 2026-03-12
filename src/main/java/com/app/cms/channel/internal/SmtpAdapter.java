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
import java.util.concurrent.locks.ReentrantLock;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmtpAdapter {
    private final EmailConfig emailConfig;
    private final JavaMailSender mailSender;

    private static final ReentrantLock LOCK = new ReentrantLock();
    private static long lastSendTime = 0;
    private static final long MIN_GAP_MS = 1200;

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

            // Rate limiting
            LOCK.lock();
            try {
                long now = System.currentTimeMillis();
                long timeSinceLast = now - lastSendTime;
                if (timeSinceLast < MIN_GAP_MS) {
                    long waitTime = MIN_GAP_MS - timeSinceLast;
                    log.debug("Rate limiting SMTP: waiting {}ms", waitTime);
                    Thread.sleep(waitTime);
                }
                
                // Send
                mailSender.send(message);
                lastSendTime = System.currentTimeMillis();
            } finally {
                LOCK.unlock();
            }

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
