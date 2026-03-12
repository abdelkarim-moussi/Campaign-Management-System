@ApplicationModule(
        displayName = "Marketing Automation",
        allowedDependencies = {"contact","template","channel","campaign"}
)
package com.app.cms.automation;

import org.springframework.modulith.ApplicationModule;