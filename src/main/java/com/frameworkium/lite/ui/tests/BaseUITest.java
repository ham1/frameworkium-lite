package com.frameworkium.lite.ui.tests;

import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.driver.Driver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;

@Tag("base-ui")
public abstract class BaseUITest {

    /**
     * Logger for subclasses (logs with correct class i.e. not BaseUITest).
     */
    protected final Logger logger = LogManager.getLogger(this);

    /**
     * Runs before each test method, it initialises the following:
     * <ul>
     * <li>{@link Driver} and {@link WebDriver}
     * <li>{@link Wait}
     * <li>{@link ScreenshotCapture}
     * <li>userAgent
     * </ul>
     */
    @BeforeEach
    protected void configureBrowserBeforeTest(TestInfo testInfo) {
        UITestLifecycle.get().beforeTestMethod(testInfo.getTestMethod().orElseThrow());
    }

    /**
     * Tears down the browser after the test method.
     */
    @AfterEach
    protected void tearDownDriver() {
        UITestLifecycle.get().afterTestMethod();
    }
}
