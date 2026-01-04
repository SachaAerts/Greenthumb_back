@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"notification", "infrastructure", "plant"},
        type = ApplicationModule.Type.OPEN
)
package com.GreenThumb.api.user;

import org.springframework.modulith.ApplicationModule;