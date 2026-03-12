
@ApplicationModule(
        displayName = "Commons",
        allowedDependencies = {"user", "user :: repositories", "user :: entities"}
)

package com.app.cms.common;

import org.springframework.modulith.ApplicationModule;