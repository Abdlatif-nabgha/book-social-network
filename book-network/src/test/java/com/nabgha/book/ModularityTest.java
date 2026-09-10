package com.nabgha.book;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.modulith.docs.Documenter.CanvasOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions;
import org.springframework.modulith.docs.Documenter.DiagramOptions.DiagramStyle;

public class ModularityTest {

    private final ApplicationModules modules = ApplicationModules.of(BookNetworkApiApplication.class);

    @Test
    void testModularity() {
        modules.verify();
    }

    @Test
    void generateModuleDocumentation() {
        new Documenter(modules)
                .writeDocumentation(
                        DiagramOptions.defaults().withStyle(DiagramStyle.C4),
                        CanvasOptions.defaults()
                );
    }
}
