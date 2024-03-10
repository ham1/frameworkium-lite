package com.frameworkium.lite.common.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

public class TestLoggingListener implements TestExecutionListener {

    private final Logger logger = LogManager.getLogger();

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        logger.info("Test plan execution started.");
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        logger.info("Test plan execution finished.");
    }

    @Override
    public void executionStarted(TestIdentifier testIdentifier) {
        if (testIdentifier.isTest()) {
            logger.info("START {}", getTestIdentifier(testIdentifier));
        }
    }

    @Override
    public void executionFinished(
            TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        if (!testIdentifier.isTest()) {
            return;
        }
        var msg =
                switch (testExecutionResult.getStatus()) {
                    case SUCCESSFUL -> "PASS";
                    case ABORTED -> "ABORT";
                    case FAILED -> "FAIL";
                };
        logger.info("{}  {}", msg, getTestIdentifier(testIdentifier));
    }

    private String getTestIdentifier(TestIdentifier testIdentifier) {
        return testIdentifier
                .getSource()
                .map(source -> {
                    if (source instanceof MethodSource methodSource) {
                        return methodSource.getClassName() + "#" + methodSource.getMethodName();
                    }
                    return source.toString();
                })
                .orElse(testIdentifier.getDisplayName());
    }
}
