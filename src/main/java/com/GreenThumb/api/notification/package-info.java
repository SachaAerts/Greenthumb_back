@org.springframework.modulith.ApplicationModule(
        allowedDependencies = {"plant", "apigateway", "user"},
        type = ApplicationModule.Type.OPEN
)
package com.GreenThumb.api.notification;

import org.springframework.modulith.ApplicationModule;