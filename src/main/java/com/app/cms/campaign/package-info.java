@ApplicationModule(
        displayName = "Campaign Management",
        allowedDependencies = {"contact", "template", "channel", "common :: security", "template :: services", "channel :: services", "template :: entity", "contact :: entity", "template :: dto", "channel :: dto", "contact :: service"}
)
package com.app.cms.campaign;

import org.springframework.modulith.ApplicationModule;