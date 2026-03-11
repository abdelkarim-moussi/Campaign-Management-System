@ApplicationModule(
        displayName = "Analytics & Reporting",
        allowedDependencies = {"campaign","campaign :: events", "channel","channel :: events", "contact"}
)
package com.app.cms.analytics;

import org.springframework.modulith.ApplicationModule;