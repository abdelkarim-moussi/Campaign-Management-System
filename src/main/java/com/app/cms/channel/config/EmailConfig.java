package com.app.cms.channel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cms.email")
@Data
public class EmailConfig {
    private String provider = "sendgrid";  // sendgrid, mailgun, smtp
    private String apiKey;
    private String fromEmail = "noreply@cms.com";
    private String fromName = "CMS Platform";

    // For Smtp
    private String smtpHost;
    private Integer smtpPort;
    private String smtpUsername;
    private String smtpPassword;
}
