package com.frameworkium.lite.ui.driver.drivers;

import com.frameworkium.lite.common.properties.Property;
import com.frameworkium.lite.ui.driver.AbstractDriver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.*;

public class FirefoxImpl extends AbstractDriver {

    @Override
    public FirefoxOptions getCapabilities() {
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        if (Property.HEADLESS.getBoolean()) {
            firefoxOptions.addArguments("--headless");
        }
        firefoxOptions.setLogLevel(FirefoxDriverLogLevel.INFO);
        return firefoxOptions;
    }

    @Override
    public WebDriver getWebDriver(Capabilities capabilities) {
        FirefoxOptions firefoxOptions = capabilities instanceof FirefoxOptions options
                ? options
                : new FirefoxOptions().merge(capabilities);
        return new FirefoxDriver(firefoxOptions);
    }
}
