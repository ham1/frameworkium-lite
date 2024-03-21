package com.frameworkium.integration.frameworkium.tests;

import static org.assertj.core.api.Assertions.assertThat;

import com.frameworkium.integration.frameworkium.pages.JQueryDemoPage;
import com.frameworkium.integration.seleniumhq.pages.SeleniumDownloadPage;
import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.tests.BaseUITest;

import org.junit.jupiter.api.*;

@Tag("fw-bugs")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FrameworkiumBugsTest extends BaseUITest {

    @Override
    protected void beforeEachWithRetry() {
        assertThat(UITestLifecycle.get().getWebDriver().getPageSource()).isNotEmpty();
    }

    @BeforeEach
    public void configureBrowserBeforeUse_allows_browser_access_in_before_method() {
        assertThat(UITestLifecycle.get().getWebDriver().getPageSource()).isNotEmpty();
    }

    @Test
    public void ensure_jQueryAjaxDone_does_not_fail() {
        String headingText = JQueryDemoPage.open().waitForJQuery().getHeadingText();
        assertThat(headingText).isEqualTo("jQuery UI Demos");
    }

    @Test
    @Order(1)
    public void use_base_page_visibility() {
        SeleniumDownloadPage.open().waitForContent();
    }

    @Test
    @Order(2)
    public void ensure_BaseUITest_wait_is_updated_after_browser_reset() {
        // tests bug whereby BasePage.wait wasn't updated after browser reset
        SeleniumDownloadPage.open().waitForContent();
    }

    @Test
    public void use_various_loggers() {
        logger.info("Using BaseUITest logger");
        SeleniumDownloadPage.open().log();
    }
}
