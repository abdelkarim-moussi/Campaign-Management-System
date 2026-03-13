@ApplicationModule(
        displayName = "Marketing Automation",
        allowedDependencies = {"contact", "template", "channel", "campaign", "common :: security"}
)
package com.app.cms.automation;

import org.springframework.modulith.ApplicationModule;