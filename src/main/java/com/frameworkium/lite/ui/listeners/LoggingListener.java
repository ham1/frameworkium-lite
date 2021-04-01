package com.frameworkium.lite.ui.listeners;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoggingListener implements WebDriverListener {

    private static final Logger logger = LogManager.getLogger();
    private static final Pattern p = Pattern.compile("->\\s(.*)(?=])");

    public static String getLocatorFromElement(WebElement element) {
        String str = element.toString();
        Matcher m = p.matcher(str);
        var groupFound = m.find() && m.groupCount() > 0;
        return groupFound ? m.group(1) : str;
    }

    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {
        // TODO check if this is worth logging
        // Lots of caught exceptions being logged here
        logger.trace("Event listener onError.", e);
    }

    // WebDriver

    @Override
    public void beforeFindElement(WebDriver driver, By locator) {
        logger.debug("before find element by {}", locator);
    }

    @Override
    public void afterFindElement(WebDriver driver, By locator, WebElement result) {
        logger.debug("after find element by {}", locator);
    }

    @Override
    public void beforeFindElements(WebDriver driver, By locator) {
        beforeFindElement(driver, locator);
    }

    @Override
    public void afterFindElements(WebDriver driver, By locator, List<WebElement> result) {
        logger.debug("after find elements by {}", locator);
    }

    @Override
    public void beforeExecuteScript(WebDriver driver, String script, Object[] args) {
        // Only log part of a long script
        logger.debug("running script {}", () -> StringUtils.abbreviate(script, 512));
    }

    @Override
    public void afterExecuteScript(WebDriver driver, String script, Object[] args, Object result) {
        // Only log part of a long script
        // We log more of script in beforeScript
        logger.debug("ran script {}", () -> StringUtils.abbreviate(script, 128));
    }

    @Override
    public void beforeExecuteAsyncScript(WebDriver driver, String script, Object[] args) {
        beforeExecuteScript(driver, script, args);
    }

    @Override
    public void afterExecuteAsyncScript(WebDriver driver, String script, Object[] args, Object result) {
        afterExecuteScript(driver, script, args, result);
    }

    // WebElement

    @Override
    public void beforeClick(WebElement element) {
        logger.debug("Before click element {}", () -> getLocatorFromElement(element));
    }

    @Override
    public void afterClick(WebElement element) {
        logger.debug("clicked element {}", () -> getLocatorFromElement(element));
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
        logger.debug("before send keys to element {}", () -> getLocatorFromElement(element));
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
        logger.debug("after send keys to element {}", () -> getLocatorFromElement(element));
    }
}
