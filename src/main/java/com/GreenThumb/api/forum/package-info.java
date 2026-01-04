@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"user", "infrastructure"},
        type = ApplicationModule.Type.OPEN
)
package com.GreenThumb.api.forum;

import org.springframework.modulith.ApplicationModule;