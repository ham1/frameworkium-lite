package com.frameworkium.lite.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    /**
     * Logger for subclasses (logs with correct class i.e. not BaseUITest).
     */
    protected final Logger logger = LogManager.getLogger(this);

    /*
     * JUnit does not allow retries in Before or After methods, we have to implement our own
     */
    protected static final int MAX_RETRIES = 3;

    @BeforeEach
    void beforeEach(TestInfo testInfo) {
        retryOnThrowable(this::beforeEachWithRetry, MAX_RETRIES);
    }

    protected void beforeEachWithRetry() {
        // Default implementation does nothing. Override in subclasses if needed.
    }

    @BeforeAll
    void beforeAll() {
        retryOnThrowable(this::beforeAllWithRetry, MAX_RETRIES);
    }

    protected void beforeAllWithRetry() {
        // Default implementation does nothing. Override in subclasses if needed.
    }

    @AfterEach
    void afterEach() {
        retryOnThrowable(this::afterEachWithRetry, MAX_RETRIES);
    }

    protected void afterEachWithRetry() {
        // Default implementation does nothing. Override in subclasses if needed.
    }

    @AfterAll
    void afterAll() {
        retryOnThrowable(this::afterAllWithRetry, MAX_RETRIES);
    }

    protected void afterAllWithRetry() {
        // Default implementation does nothing. Override in subclasses if needed.
    }

    public void retryOnThrowable(Runnable runnable, int retryCount) {
        if (retryCount < 1) {
            throw new IllegalStateException("No more retries left");
        }
        try {
            runnable.run();
        } catch (Exception ex) {
            if (ex instanceof InterruptedException
                    || ex.getCause() instanceof InterruptedException) {
                Thread.currentThread().interrupt();
                throw ex;
            }
            logger.trace("Caught {}", ex.getClass(), ex);
            retryOnThrowable(runnable, retryCount - 1);
        }
    }
}
