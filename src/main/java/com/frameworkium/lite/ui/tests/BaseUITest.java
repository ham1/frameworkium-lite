package com.frameworkium.lite.ui.tests;

import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.driver.Driver;
import com.frameworkium.lite.ui.listeners.CaptureListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;

@Tag("base-ui")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(CaptureListener.class)
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
    protected void configureBrowserBeforeTest(TestInfo testInfo) {
        UITestLifecycle.get().beforeTestMethod(testInfo.getTestMethod().orElseThrow());
    }

    /**
     * Tears down the browser after the test method.
     */
    protected void tearDownDriver() {
        UITestLifecycle.get().afterTestMethod();
    }

    /*
     * JUnit does not allow retries in Before or After methods, we have to implement our own
     */
    private static final int MAX_RETRIES = 3;

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        configureBrowserBeforeTest(testInfo);
        onThrowable(Exception.class, this::beforeEachWithRetry, MAX_RETRIES);
    }

    protected void beforeEachWithRetry() {
        // Default implementation does nothing. Override in subclasses if needed.
    }

    @BeforeAll
    void beforeAll() {
        onThrowable(Exception.class, this::beforeAllWithRetry, MAX_RETRIES);
    }

    protected void beforeAllWithRetry() {
        // Default implementation does nothing. Override in subclasses if needed.
    }

    @AfterEach
    void afterEach() {
        try {
            onThrowable(Exception.class, this::afterEachWithRetry, MAX_RETRIES);
        } finally {
            tearDownDriver();
        }
    }

    protected void afterEachWithRetry() {
        // Default implementation does nothing. Override in subclasses if needed.
    }

    @AfterAll
    void afterAll() {
        onThrowable(Exception.class, this::afterAllWithRetry, MAX_RETRIES);
    }

    protected void afterAllWithRetry() {
        // Default implementation does nothing. Override in subclasses if needed.
    }

    public void onThrowable(Class<?> exception, Runnable runnable, int retryCount) {
        if (retryCount < 1) {
            throw new IllegalStateException(
                    "No more retries left, too many " + exception.getName());
        }
        try {
            runnable.run();
        } catch (Throwable ex) {
            if (ex.getClass().isAssignableFrom(InterruptedException.class)) {
                throw ex;
            }
            if (ex.getClass().equals(exception)) {
                logger.trace("Caught {}", ex.getMessage().getClass(), ex);
                onThrowable(exception, runnable, retryCount - 1);
            }

            throw ex;
        }
    }
}
