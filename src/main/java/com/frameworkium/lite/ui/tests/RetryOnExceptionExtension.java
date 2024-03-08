package com.frameworkium.lite.ui.tests;

import com.frameworkium.lite.ui.UITestLifecycle;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.openqa.selenium.ScriptTimeoutException;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class RetryOnExceptionExtension implements TestExecutionExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(RetryOnExceptionExtension.class);

    private int retries;
    private final int maxRetries;
    private final Set<Class<? extends Throwable>> retryOnExceptions;

    public RetryOnExceptionExtension(
            int maxRetries, Set<Class<? extends Throwable>> retryOnExceptions) {
        this.maxRetries = maxRetries;
        this.retryOnExceptions = retryOnExceptions;
        this.retries = 0;
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable)
            throws Throwable {
        if (throwable instanceof ScriptTimeoutException) {
            log.error("Caught a ScriptTimeoutException, retrying", throwable);
            return;
        }
        if (throwable instanceof UnreachableBrowserException
                || throwable instanceof SessionNotCreatedException) {
            log.error("Caught a GridException, retrying", throwable);
            UITestLifecycle.get().reinitialiseCurrentDriver();
            return true;
        }
        for (Class<? extends Throwable> ex : retryOnExceptions) {
            if (ex.isAssignableFrom(throwable.getClass()) && retries < maxRetries) {
                Method testMethod = context.getRequiredTestMethod();
                reRunTest(context, testMethod);
            } else {
                throw throwable;
            }
        }
    }

    private void reRunTest(ExtensionContext context, Method testMethod) {
        retries++;
        System.out.println("Retrying test " + testMethod.getName() + ": attempt " + retries);
        UITestLifecycle.get().beforeTestMethod(testMethod);
        try {
            context.getRoot()
                    .getStore(ExtensionContext.Namespace.GLOBAL)
                    .getOrComputeIfAbsent(context.getRequiredTestClass())
                    .getClass()
                    .getMethod(testMethod.getName())
                    .invoke(context.getTestInstance().orElse(null));

        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        } finally {
            UITestLifecycle.get().afterTestMethod();
        }
    }
}
