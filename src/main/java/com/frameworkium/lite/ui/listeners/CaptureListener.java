package com.frameworkium.lite.ui.listeners;

import com.frameworkium.lite.ui.ExtraExpectedConditions;
import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.browsers.UserAgent;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.capture.model.Command;
import com.frameworkium.lite.ui.tests.BaseUITest;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverListener;
import org.testng.*;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.abbreviate;

/**
 * Assumes {@link ScreenshotCapture#isRequired()} is true for WebDriver events.
 */
public class CaptureListener implements WebDriverListener, ITestListener {

    private final Logger logger = LogManager.getLogger(this);

    private static final List<String> FRAMEWORKIUM_SCRIPTS = Arrays.asList(
            UserAgent.SCRIPT,
            ExtraExpectedConditions.JQUERY_AJAX_DONE_SCRIPT
    );

    private void takeScreenshotAndSend(Command command) {
        try {
            UITestLifecycle.get()
                    .getCapture()
                    .takeAndSendScreenshot(command, getWebDriver());
        } catch (Exception e) {
            logger.warn("Screenshot not sent, see trace log for details");
            logger.trace(e);
        }
    }

    private void takeScreenshotAndSend(String action, Throwable thrw) {
        var errorMessage = thrw.getMessage() + "\n" + ExceptionUtils.getStackTrace(thrw);
        UITestLifecycle.get()
                .getCapture()
                .takeAndSendScreenshotWithError(
                        new Command(action, "n/a", "n/a"),
                        getWebDriver(),
                        errorMessage);
    }

    private void sendFinalScreenshot(ITestResult result, String action) {
        try {
            if (!ScreenshotCapture.isRequired() || !isUITest(result)) {
                return;
            }

            var uiTestLifecycle = UITestLifecycle.get();
            if (uiTestLifecycle == null) {
                return;
            }

            var driver = uiTestLifecycle.getWebDriver();
            if (driver == null) {
                return;
            }

            var throwable = result.getThrowable();
            if (throwable != null) {
                takeScreenshotAndSend(action, throwable);
            } else {
                takeScreenshotAndSend(new Command(action, "n/a", "n/a"));
            }
        } catch (Exception e) {
            logger.debug("Failed to send final screenshot", e);
        }
    }

    private boolean isUITest(ITestResult result) {
        return BaseUITest.class.isAssignableFrom(result.getTestClass().getRealClass());
    }

    private void highlightElementOnClickAndSendScreenshot(WebElement element) {
        if (!ScreenshotCapture.isRequired()) {
            return;
        }
        // ElementHighlighter does not work with Selenium 4.0.0-beta-3 and RemoteWebDriver
        //        ElementHighlighter highlighter = new ElementHighlighter(getWebDriver());
        //        highlighter.highlightElement(element);
        takeScreenshotAndSend(new Command("click", element));
        //        highlighter.unhighlightPrevious();
    }

    private WebDriver getWebDriver() {
        return UITestLifecycle.get().getWebDriver();
    }

    /* WebDriver events */

    @Override
    public void beforeClick(WebElement element) {
        highlightElementOnClickAndSendScreenshot(element);
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
        takeScreenshotAndSend(new Command("sendKeys", "n/a", "n/a"));
    }

    @Override
    public void beforeAnyNavigationCall(WebDriver.Navigation navigation, Method method, Object[] args) {
        var argsAsStrings = Arrays.stream(args).map(Objects::toString).collect(Collectors.joining(","));
        takeScreenshotAndSend(new Command("nav", method.getName(), argsAsStrings));
    }

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        var command = new Command("nav", "url", url);
        takeScreenshotAndSend(command, driver);
    }

    @Override
    public void afterSwitchToWindow(String windowName, WebDriver driver) {
        var command = new Command("nav", "window", windowName);
        takeScreenshotAndSend(command, driver);
    }

    @Override
    public void beforeExecuteScript(WebDriver driver, String script, Object[] args) {
        // ignore scripts which are part of Frameworkium
        if (!isFrameworkiumScript(script)) {
            takeScreenshotAndSend(
                    new Command("script", "n/a", abbreviate(script, 42)));
        }
    }

    private boolean isFrameworkiumScript(String script) {
        return FRAMEWORKIUM_SCRIPTS.contains(script);
    }

    @Override
    public void beforeExecuteAsyncScript(WebDriver driver, String script, Object[] args) {
        beforeExecuteScript(driver, script, args);
    }

    /* Test end methods */

    @Override
    public void onTestSuccess(ITestResult result) {
        sendFinalScreenshot(result, "pass");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        sendFinalScreenshot(result, "fail");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        sendFinalScreenshot(result, "skip");
    }

    /* Methods we don't really want screenshots for. */

    @Override
    public void onTestStart(ITestResult result) {}

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override
    public void onStart(ITestContext context) {}

    @Override
    public void onFinish(ITestContext context) {}
}
