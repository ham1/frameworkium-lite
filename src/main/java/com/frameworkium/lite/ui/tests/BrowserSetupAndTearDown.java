package com.frameworkium.lite.ui.tests;

import com.frameworkium.lite.ui.UITestLifecycle;

import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

public class BrowserSetupAndTearDown implements TestExecutionListener {

    /**
     * Runs before the test suite to initialise a pool of drivers, if requested.
     */
    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        UITestLifecycle.beforeSuite();
    }

    /**
     * <ul>
     * <li>Ensures each driver in the pool has {@code quit()}
     * <li>Processes remaining screenshot backlog
     * </ul>
     */
    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        UITestLifecycle.get().afterTestSuite();
    }
}
