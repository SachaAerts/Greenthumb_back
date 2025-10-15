package com.Greanthumb.application;

import com.Greanthumb.api.BackApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ApplicationTests {

    @Test
    void writeDocumentationSnippets() {

        ApplicationModules modules = ApplicationModules
                .of(BackApplication.class);

        modules.verify();
    }
}
