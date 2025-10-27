@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"user", "plant", "tracking", "forum", "resources"},
        type = ApplicationModule.Type.OPEN
)
package com.GreenThumb.api.apigateway;

import org.springframework.modulith.ApplicationModule;