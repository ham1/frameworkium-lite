package com.frameworkium.integration.seleniumhq.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.frameworkium.integration.seleniumhq.pages.HomePage;
import com.frameworkium.lite.ui.tests.BaseUITest;

import org.testng.annotations.Test;

public class SeleniumTest extends BaseUITest {

    @Test
    public void component_example_test() {
        var latestVersion = HomePage.open().getHeader().clickDownloadLink().getLatestVersion();

        assertThat(latestVersion).matches("\\d\\.\\d+\\.\\d+.*");
    }
}
