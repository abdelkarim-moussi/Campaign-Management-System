package com.app.cms.campaign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CampaignScheduler {
    private final CampaignRepository campaignRepository;
    private final CampaignService campaignService;

    @Scheduled(cron = "0 * * * * *")
    public void checkScheduledCampaigns() {
        log.debug("Checking for scheduled campaigns to send...");

        LocalDateTime now = LocalDateTime.now();
        List<Campaign> campaignsToSend =
                campaignRepository.findScheduledCampaignsToSend(now);

        if (!campaignsToSend.isEmpty()) {
            log.info("Found {} campaigns to send", campaignsToSend.size());

            for (Campaign campaign : campaignsToSend) {
                try {
                    log.info("Sending scheduled campaign: {} (ID: {})",
                            campaign.getName(), campaign.getId());
                    campaignService.sendCampaign(campaign.getId());
                } catch (Exception e) {
                    log.error("Failed to send campaign {}: {}",
                            campaign.getId(), e.getMessage(), e);
                }
            }
        }
    }

}
