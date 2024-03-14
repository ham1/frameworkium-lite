package com.frameworkium.lite.ui.listeners;

import static org.junit.platform.engine.TestExecutionResult.Status.SUCCESSFUL;

import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.tests.BaseUITest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriverException;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ScreenshotListener implements TestExecutionListener {

    private static final Logger logger = LogManager.getLogger();
    private final boolean captureEnabled = ScreenshotCapture.isRequired();

    @Override
    public void executionFinished(
            TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {

        if (!testIdentifier.isTest()
                || captureEnabled
                || testIdentifier.getSource().isEmpty()
                || !BaseUITest.class.isAssignableFrom(
                        testIdentifier.getSource().get().getClass())
                || testExecutionResult.getStatus().equals(SUCCESSFUL)) {
            return;
        }

        takeScreenshotAndSaveLocally(testIdentifier.getDisplayName());
    }

    private void takeScreenshotAndSaveLocally(String testName) {
        takeScreenshotAndSaveLocally(
                testName, (TakesScreenshot) UITestLifecycle.get().getWebDriver());
    }

    private void takeScreenshotAndSaveLocally(String testName, TakesScreenshot driver) {
        String screenshotDirectory = System.getProperty("screenshotDirectory");
        if (screenshotDirectory == null) {
            screenshotDirectory = "screenshots";
        }
        String fileName = String.format("%s_%s.png", System.currentTimeMillis(), testName);
        Path screenshotPath = Paths.get(screenshotDirectory);
        Path absolutePath = screenshotPath.resolve(fileName);
        if (createScreenshotDirectory(screenshotPath)) {
            writeScreenshotToFile(driver, absolutePath);
            logger.info("Written screenshot to " + absolutePath);
        } else {
            logger.error("Unable to create " + screenshotPath);
        }
    }

    private boolean createScreenshotDirectory(Path screenshotDirectory) {
        try {
            Files.createDirectories(screenshotDirectory);
        } catch (IOException e) {
            logger.error("Error creating screenshot directory", e);
        }
        return Files.isDirectory(screenshotDirectory);
    }

    private void writeScreenshotToFile(TakesScreenshot driver, Path screenshot) {
        try (OutputStream screenshotStream = Files.newOutputStream(screenshot)) {
            byte[] bytes = driver.getScreenshotAs(OutputType.BYTES);
            screenshotStream.write(bytes);
        } catch (IOException e) {
            logger.error("Unable to write " + screenshot, e);
        } catch (WebDriverException e) {
            logger.error("Unable to take screenshot.", e);
        }
    }
}
