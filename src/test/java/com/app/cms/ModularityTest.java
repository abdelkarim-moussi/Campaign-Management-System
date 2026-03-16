package com.app.cms;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

public class ModularityTest {
    ApplicationModules modules = ApplicationModules.of(CampaignManagementSystemApplication.class);

//    @Test
//    void shouldBeCompliant() {
//        modules.verify();
//    }

    @Test
    void writeDocumentation() {
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }
}
