@ApplicationModule(
        displayName = "Analytics & Reporting",
        allowedDependencies = {"campaign", "campaign :: events", "channel", "channel :: events", "contact", "common :: security"}
)
package com.app.cms.analytics;

import org.springframework.modulith.ApplicationModule;