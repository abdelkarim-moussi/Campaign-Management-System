package com.app.cms.channel.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cms.email")
@Data
public class EmailConfig {
    private String provider = "postmark";  // postmark, sendgrid, mailgun, smtp
    private String apiKey; 
    private String fromEmail = "noreply@cms.com";
    private String fromName = "CMS Platform";

    // For SMTP (fallback)
    private String smtpHost;
    private Integer smtpPort = 587;
    private String smtpUsername;
    private String smtpPassword;
    private Boolean smtpUseTls = true;
}
