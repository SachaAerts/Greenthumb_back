@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"user", "infrastructure"},
        type = ApplicationModule.Type.OPEN
)
package com.GreenThumb.api.plant;

import org.springframework.modulith.ApplicationModule;