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

    /**
     * Creates the Driver object and maximises if required.
     */
    public void initialise() {
        WebDriver webDriver = getWebDriver(getCapabilities());
        webDriver.manage().timeouts().setScriptTimeout(Duration.ofSeconds(21));

        if (Property.MAXIMISE.getBoolean()) {
            webDriver.manage().window().maximize();
        }

        this.webDriver = addListeners(webDriver);
    }

    private WebDriver addListeners(WebDriver webDriver) {
        // Add logging listener
        var decoratedWebDriver = new EventFiringDecorator(new LoggingListener()).decorate(webDriver);

        // Add capture listener (if required)
        if (ScreenshotCapture.isRequired()) {
            decoratedWebDriver = new EventFiringDecorator(new CaptureListener()).decorate(decoratedWebDriver);
        }
        return decoratedWebDriver;
    }

}
