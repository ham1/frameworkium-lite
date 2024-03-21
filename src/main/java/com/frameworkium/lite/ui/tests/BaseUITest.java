package com.frameworkium.lite.ui.tests;

import com.frameworkium.lite.common.BaseTest;
import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.driver.Driver;
import com.frameworkium.lite.ui.listeners.CaptureListener;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;

@Tag("base-ui")
@ExtendWith(CaptureListener.class)
public abstract class BaseUITest extends BaseTest {

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        configureBrowserBeforeTest(testInfo);
        retryOnThrowable(this::beforeEachWithRetry, MAX_RETRIES);
    }

    @AfterEach
    void afterEach() {
        try {
            retryOnThrowable(this::afterEachWithRetry, MAX_RETRIES);
        } finally {
            tearDownDriver();
        }
    }

    /**
     * Runs before each test method, it initialises the following:
     * <ul>
     * <li>{@link Driver} and {@link WebDriver}
     * <li>{@link Wait}
     * <li>{@link ScreenshotCapture}
     * <li>userAgent
     * </ul>
     */
    protected void configureBrowserBeforeTest(TestInfo testInfo) {
        UITestLifecycle.get().beforeTestMethod(testInfo.getTestMethod().orElseThrow());
    }

    /**
     * Tears down the browser after the test method.
     */
    protected void tearDownDriver() {
        UITestLifecycle.get().afterTestMethod();
    }
}
