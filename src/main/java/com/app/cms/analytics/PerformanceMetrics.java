package com.app.cms.analytics;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PerformanceMetrics {
    private String metric;
    private Integer count;
    private Double percentage;
}
