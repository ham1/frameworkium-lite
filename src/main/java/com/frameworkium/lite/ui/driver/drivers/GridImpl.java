package com.frameworkium.lite.ui.driver.drivers;

import static com.frameworkium.lite.common.properties.Property.BROWSER_VERSION;
import static com.frameworkium.lite.common.properties.Property.REMOTE_OTEL_TRACING;

import com.frameworkium.lite.common.properties.Property;
import com.frameworkium.lite.ui.driver.AbstractDriver;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;

public class GridImpl extends AbstractDriver {

    private final URL remoteURL;
    private final Capabilities capabilities;

    /**
     * Implementation of driver for using Selenium Grid.
     */
    public GridImpl(Capabilities capabilities) {
        this.capabilities = capabilities;
        try {
            this.remoteURL = new URL(Property.GRID_URL.getValue());
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Capabilities getCapabilities() {
        var mutableCapabilities = new MutableCapabilities(capabilities);
        if (BROWSER_VERSION.isSpecified()) {
            mutableCapabilities.setCapability("version", BROWSER_VERSION.getValue());
        }
        return mutableCapabilities;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        var remoteWebDriver =
                new RemoteWebDriver(remoteURL, capabilities, REMOTE_OTEL_TRACING.getBoolean());
        // Set local file detector to allow file uploads to work in FileInput
        remoteWebDriver.setFileDetector(new LocalFileDetector());
        return remoteWebDriver;
    }
}
