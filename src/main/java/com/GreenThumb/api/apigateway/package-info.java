@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"user", "plant", "tracking", "forum", "resources", "notification"},
        type = ApplicationModule.Type.OPEN
)
package com.GreenThumb.api.apigateway;

import org.springframework.modulith.ApplicationModule;