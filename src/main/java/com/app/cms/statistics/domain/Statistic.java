package com.app.cms.statistics.domain;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class Statistic {
    private String id;
    private int totalSent;
    private int totalDelivered;
    private int totalOpened;
    private int totalClicked;
    private int totalBounced;
    private int totalFailed;
    private float openRate;
    private float clickRate;
    private float bounceRate;
    private float deliveryRate;
    @CreationTimestamp
    private LocalDateTime generatedAt;
}
