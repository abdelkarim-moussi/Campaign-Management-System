package com.app.cms;

import com.app.cms.common.security.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CampaignManagementSystemApplication {

    private final UserRepository userRepository;

    public CampaignManagementSystemApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(CampaignManagementSystemApplication.class, args);
    }

}
