package com.frameworkium.integration.angularjs.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.frameworkium.integration.angularjs.pages.DeveloperGuidePage;
import com.frameworkium.lite.ui.tests.BaseUITest;

import org.testng.annotations.Test;

public class DocumentationTest extends BaseUITest {

    @Test
    public void angular_documentation_test() {
        String guideTitle = DeveloperGuidePage.open()
                .searchDeveloperGuide("Bootstrap")
                .clickBootstrapSearchItem()
                .getGuideTitle();

        assertThat(guideTitle).endsWith("Bootstrap");
    }
}
