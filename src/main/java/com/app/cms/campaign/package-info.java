@ApplicationModule(
        displayName = "Campaign Management",
        allowedDependencies = {"contact", "template", "channel", "common :: security"}
)
package com.app.cms.campaign;

import org.springframework.modulith.ApplicationModule;