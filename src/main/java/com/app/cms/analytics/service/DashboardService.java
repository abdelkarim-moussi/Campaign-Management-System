package com.app.cms.analytics.service;

import com.app.cms.analytics.dto.DashboardDto;
import com.app.cms.analytics.entity.PerformanceMetrics;

import java.util.List;

public interface DashboardService {
    DashboardDto getDashboardStats();
    List<PerformanceMetrics> getPerformanceMetrics(Long campaignId);
}
