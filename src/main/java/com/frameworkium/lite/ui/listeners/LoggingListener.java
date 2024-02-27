package com.frameworkium.lite.ui.listeners;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    public void afterClick(WebElement element) {
        logger.debug("clicked element with {}", () -> getLocatorFromElement(element));
    }

    @Override
    public void afterSendKeys(WebElement element, CharSequence... keysToSend) {
        logger.debug("changed value of element with {}", () -> getLocatorFromElement(element));
    }

    @Override
    public void afterFindElement(WebDriver driver, By locator, WebElement result) {
        logger.debug("found element {}", locator);
    }

    @Override
    public void afterBack(WebDriver.Navigation navigation) {
        logger.debug("after back");
    }

    @Override
    public void afterForward(WebDriver.Navigation navigation) {
        logger.debug("after forward");
    }

    @Override
    public void beforeRefresh(WebDriver.Navigation navigation) {
        logger.debug("before Navigate Refresh");
    }

    @Override
    public void afterRefresh(WebDriver.Navigation navigation) {
        logger.debug("after Navigate Refresh");
    }

    @Override
    public void afterTo(WebDriver.Navigation navigation, String url) {
        logger.debug("navigated to {}", url);
    }

    @Override
    public void afterExecuteScript(WebDriver driver, String script, Object[] args, Object result) {
        logger.debug("ran script {}", () -> StringUtils.abbreviate(script, 128));
    }

    @Override
    public void beforeSendKeys(WebElement element, CharSequence... keysToSend) {
        logger.debug("before send keys to element with {}", () -> getLocatorFromElement(element));
    }

    @Override
    public void beforeClick(WebElement element) {
        logger.debug("click element with {}", () -> getLocatorFromElement(element));
    }

    @Override
    public void beforeFindElement(WebDriver driver, By locator) {
        logger.debug("before find element by {}", locator);
    }

    @Override
    public void beforeBack(WebDriver.Navigation navigation) {
        logger.debug("before back");
    }

    @Override
    public void beforeForward(WebDriver.Navigation navigation) {
        logger.debug("before forward");
    }

    @Override
    public void beforeExecuteScript(WebDriver driver, String script, Object[] args) {
        // Only log part of a long script
        logger.debug("running script {}", () -> StringUtils.abbreviate(script, 512));
    }

    @Override
    public void onError(Object target, Method method, Object[] args, InvocationTargetException e) {
        var thrw = e.getTargetException();
        // Lots of caught exceptions being logged here
        if (thrw instanceof NoSuchElementException) {
            // Don't log entire stack trace and message as it's too frequent
            String firstLineOfMessage = thrw.getMessage().split("\n")[0];
            logger.trace(firstLineOfMessage);
        } else {
            logger.trace("on exception", thrw);
        }
    }

}
