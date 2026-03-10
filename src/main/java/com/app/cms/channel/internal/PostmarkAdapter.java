package com.app.cms.channel.internal;

import com.app.cms.channel.EmailDto;
import com.app.cms.channel.SendResult;
import com.app.cms.channel.config.EmailConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostmarkAdapter {

    private final EmailConfig emailConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String POSTMARK_API_URL = "https://api.postmarkapp.com/email";

    public SendResult send(EmailDto emailDto) {
        log.info("Sending email via Postmark to: {}", emailDto.getTo());

        try {

            Map<String, Object> payload = new HashMap<>();

            payload.put("From", String.format("%s <%s>",
                    emailConfig.getFromName(),
                    emailConfig.getFromEmail()));

            payload.put("To", emailDto.getTo());

            payload.put("Subject", emailDto.getSubject());

            payload.put("HtmlBody", emailDto.getContent());

            // Text version (optional but recommended)
            String textContent = emailDto.getContent()
                    .replaceAll("<[^>]*>", "")  // Strip HTML tags
                    .replaceAll("\\s+", " ")
                    .trim();

            payload.put("TextBody", textContent);

            // Tracking
            payload.put("TrackOpens", true);
            payload.put("TrackLinks", "HtmlAndText");

            // Tag (identify campaign)
            if (emailDto.getCampaignId() != null) {
                payload.put("Tag", "campaign-" + emailDto.getCampaignId());
            }

            // Metadata (for tracking)
            Map<String, String> metadata = new HashMap<>();
            if (emailDto.getCampaignId() != null) {
                metadata.put("campaignId", emailDto.getCampaignId().toString());
            }
            if (emailDto.getContactId() != null) {
                metadata.put("contactId", emailDto.getContactId().toString());
            }
            payload.put("Metadata", metadata);

            // Http Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Postmark-Server-Token", emailConfig.getApiKey());
            headers.set("Accept", "application/json");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            // Send
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    POSTMARK_API_URL,
                    request,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                Integer errorCode = (Integer) body.get("ErrorCode");

                if (errorCode != null && errorCode == 0) {
                    // Success
                    String messageId = (String) body.get("MessageID");
                    log.info("Email sent successfully via Postmark. MessageID: {}", messageId);
                    return new SendResult(true, null, messageId, null);
                } else {
                    // Error returned by Postmark
                    String errorMessage = (String) body.get("Message");
                    log.error("Postmark returned error: {}", errorMessage);
                    return new SendResult(false, null, null, errorMessage);
                }
            } else {
                log.error("Postmark returned error status: {}", response.getStatusCode());
                return new SendResult(false, null, null,
                        "Postmark error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to send email via Postmark: {}", e.getMessage(), e);
            return new SendResult(false, null, null, e.getMessage());
        }
    }
}