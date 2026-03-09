@ApplicationModule(
        displayName = "Campaign Management",
        allowedDependencies = {"contact", "template", "channel"}
)
package com.app.cms.campaign;

import org.springframework.modulith.ApplicationModule;