@ApplicationModule(
        displayName = "Marketing Automation",
        allowedDependencies = {"contact", "template", "channel", "campaign", "common :: security", "contact :: contact-events", "campaign :: events", "campaign :: entities", "campaign :: services", "channel :: services", "template :: services", "contact :: entity", "campaign :: entity"}
)
package com.app.cms.automation;

import org.springframework.modulith.ApplicationModule;