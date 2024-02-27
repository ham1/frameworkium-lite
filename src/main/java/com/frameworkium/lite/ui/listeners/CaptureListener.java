package com.frameworkium.lite.ui.listeners;

import static org.apache.commons.lang3.StringUtils.abbreviate;

import com.frameworkium.lite.ui.ExtraExpectedConditions;
import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.browsers.UserAgent;
import com.frameworkium.lite.ui.capture.ElementHighlighter;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.capture.model.Command;
import com.frameworkium.lite.ui.tests.BaseUITest;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;
import java.util.List;

/**
 * Assumes {@link ScreenshotCapture#isRequired()} is true for WebDriver events.
 */
public class CaptureListener implements WebDriverListener, ITestListener {

    private final Logger logger = LogManager.getLogger(this);

    private static final List<String> FRAMEWORKIUM_SCRIPTS =
            Arrays.asList(UserAgent.SCRIPT, ExtraExpectedConditions.JQUERY_AJAX_DONE_SCRIPT);

    private void takeScreenshotAndSend(Command command) {
        try {
            UITestLifecycle.get()
                    .getCapture()
                    .takeAndSendScreenshot(command, UITestLifecycle.get().getWebDriver());
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
                        UITestLifecycle.get().getWebDriver(),
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

            var thrw = result.getThrowable();
            if (thrw != null) {
                takeScreenshotAndSend(action, thrw);
            } else {
                var command = new Command(action, "n/a", "n/a");
                takeScreenshotAndSend(command);
            }
        } catch (Exception e) {
            logger.debug("Failed to send final screenshot", e);
        }
    }

    private boolean isUITest(ITestResult result) {
        return BaseUITest.class.isAssignableFrom(result.getTestClass().getRealClass());
    }

    private void highlightElementOnClickAndSendScreenshot(WebDriver driver, WebElement element) {
        if (!ScreenshotCapture.isRequired()) {
            return;
        }
        ElementHighlighter highlighter = new ElementHighlighter(driver);
        highlighter.highlightElement(element);
        var command = new Command("click", element);
        takeScreenshotAndSend(command);
        highlighter.unhighlightPrevious();
    }

    /* WebDriver events */
    @Override
    public void beforeClick(WebElement element) {
        try {
            highlightElementOnClickAndSendScreenshot(UITestLifecycle.get().getWebDriver(), element);
        } catch (Exception e) {
            logger.trace("Failed to highlight element before click and send screenshot", e);
        }
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
        takeScreenshotAndSend(new Command("change", "n/a", "n/a"));
    }

    @Override
    public void beforeTo(WebDriver.Navigation navigation, String url) {
        takeScreenshotAndSend(new Command("nav", "url", url));
    }

    @Override
    public void beforeGet(WebDriver driver, String url) {
        takeScreenshotAndSend(new Command("nav", "url", url));
    }

    @Override
    public void beforeExecuteScript(WebDriver driver, String script, Object[] args) {
        if (isFrameworkiumScript(script)) {
            return; // ignore scripts that are part of Frameworkium
        }
        takeScreenshotAndSend(new Command("script", "n/a", abbreviate(script, 42)));
    }

    private boolean isFrameworkiumScript(String script) {
        return FRAMEWORKIUM_SCRIPTS.contains(script);
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
}
