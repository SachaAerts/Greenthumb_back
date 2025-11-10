package com.GreenThumb.api;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ArchTest {
    @Test
    void verifyModularStructure() {
    ApplicationModules modules = ApplicationModules.of(BackApplication.class);
    modules.verify();

    }

}
