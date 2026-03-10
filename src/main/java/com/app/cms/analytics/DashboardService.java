package com.app.cms.analytics;

import java.util.List;

public interface DashboardService {
    DashboardDto getDashboardStats();
    List<PerformanceMetrics> getPerformanceMetrics(Long campaignId);
}
