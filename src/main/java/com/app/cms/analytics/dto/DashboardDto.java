package com.app.cms.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDto {

    private Integer totalCampaigns;
    private Integer totalMessagesSent;
    private Integer totalMessagesDelivered;
    private Integer totalMessagesOpened;
    private Integer totalMessagesClicked;

    private Double averageOpenRate;
    private Double averageClickRate;
    private Double averageDeliveryRate;

    private Integer campaignsThisMonth;
    private Integer messagesSentThisMonth;
    private Integer messagesSentToday;
}
