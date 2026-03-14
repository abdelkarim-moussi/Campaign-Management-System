@ApplicationModule(
        displayName = "Marketing Automation",
        allowedDependencies = {"contact", "template", "channel", "campaign", "common :: security", "contact :: contact-events", "campaign :: events"}
)
package com.app.cms.automation;

import org.springframework.modulith.ApplicationModule;