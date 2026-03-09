package com.app.cms.channel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cms.sms")
@Data
public class SmsConfig {
    private String provider = "twilio";  // twilio, nexmo
    private String accountSid;
    private String authToken;
    private String fromNumber;
}
