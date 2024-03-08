package com.frameworkium.lite.ui.tests;

import com.frameworkium.lite.ui.UITestLifecycle;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class BrowserSetupAndTearDown implements BeforeAllCallback, AfterAllCallback {

    private static boolean started = false;
    /**
     * Runs before the test suite to initialise a pool of drivers, if requested.
     */
    @Override
    public void beforeAll(ExtensionContext context) {
        if (!started) {
            UITestLifecycle.beforeSuite();
            started = true;
        }
    }

    /**
     * <ul>
     * <li>Ensures each driver in the pool has {@code quit()}
     * <li>Processes remaining screenshot backlog
     * </ul>
     */
    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        UITestLifecycle.get().afterTestSuite();
    }
}
