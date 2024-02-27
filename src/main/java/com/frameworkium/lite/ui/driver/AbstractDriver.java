package com.frameworkium.lite.ui.driver;

import com.frameworkium.lite.common.properties.Property;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.listeners.CaptureListener;
import com.frameworkium.lite.ui.listeners.LoggingListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringDecorator;

import java.time.Duration;

public abstract class AbstractDriver implements Driver {

    protected static final Logger logger = LogManager.getLogger();

    private WebDriver webDriver;

    @Override
    public WebDriver getWebDriver() {
        return this.webDriver;
    }

    /** Creates the Decorated {@link WebDriver} object and maximises if required. */
    public void initialise() {
        var capabilities = getCapabilities();
        logger.debug("Browser Capabilities: {}", capabilities);
        var webDriver = getWebDriver(capabilities);
        this.webDriver = decorateWebDriver(webDriver);
        this.webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(21));
        if (Property.MAXIMISE.getBoolean()) {
            this.webDriver.manage().window().maximize();
        }
    }

    private WebDriver decorateWebDriver(WebDriver driverToBeDecorated) {
        var decoratedDriver = new EventFiringDecorator<>(new LoggingListener()).decorate(driverToBeDecorated);
        if (ScreenshotCapture.isRequired()) {
            var captureListener = new CaptureListener();
            decoratedDriver = new EventFiringDecorator<>(captureListener).decorate(decoratedDriver);
        }
        return decoratedDriver;
    }

}
