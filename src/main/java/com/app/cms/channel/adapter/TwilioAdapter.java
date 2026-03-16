package com.app.cms.channel.adapter;

import com.app.cms.channel.dto.SendResult;
import com.app.cms.channel.dto.SmsDto;
import com.app.cms.channel.config.SmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TwilioAdapter {
    private final SmsConfig smsConfig;
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String TWILIO_API_URL = "https://api.twilio.com/2010-04-01/Accounts/%s/Messages.json";

    public SendResult send(SmsDto smsDto) {
        log.info("Sending SMS via Twilio to: {}", smsDto.getTo());

        try {
            String url = String.format(TWILIO_API_URL, smsConfig.getAccountSid());

            // Prepare le payload (form-encoded)
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("From", smsConfig.getFromNumber());
            params.add("To", smsDto.getTo());
            params.add("Body", smsDto.getContent());

            // Headers with Basic Auth
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String auth = smsConfig.getAccountSid() + ":" + smsConfig.getAuthToken();
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            headers.set("Authorization", "Basic " + encodedAuth);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

            // Send
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> body = response.getBody();
                String sid = (String) body.get("sid");

                log.info("SMS sent successfully via Twilio. SID: {}", sid);
                return new SendResult(true, null, sid, null);
            } else {
                log.error("Twilio returned error: {}", response.getStatusCode());
                return new SendResult(false, null, null,
                        "Twilio error: " + response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Failed to send SMS via Twilio: {}", e.getMessage(), e);
            return new SendResult(false, null, null, e.getMessage());
        }
    }
}
