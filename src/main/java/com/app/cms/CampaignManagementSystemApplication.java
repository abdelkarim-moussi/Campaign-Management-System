package com.app.cms;

import com.app.cms.common.security.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CampaignManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampaignManagementSystemApplication.class, args);
    }

}
