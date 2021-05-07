package com.frameworkium.lite.ui.pages;

import com.frameworkium.lite.htmlelements.loader.HtmlElementLoader;
import com.frameworkium.lite.ui.UITestLifecycle;
import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.capture.ScreenshotCapture;
import com.frameworkium.lite.ui.capture.model.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;

public abstract class BasePage<T extends BasePage<T>> {

    protected final Logger logger = LogManager.getLogger(this);

    protected final WebDriver driver;
    protected Wait<WebDriver> wait;
    private Visibility visibility;

    protected BasePage() {
        this(UITestLifecycle.get().getWebDriver(), UITestLifecycle.get().getWait());
    }

    /**
     * Added to enable testing and, one day, remove coupling to BaseUITest.
     */
    protected BasePage(WebDriver driver, Wait<WebDriver> wait) {
        this.driver = driver;
        this.wait = wait;
        this.visibility = new Visibility(wait);
    }

    /**
     * Get the current page object. Useful for e.g.
     * <code>myPage.get().then().doSomething();</code>
     *
     * @return the current page object
     */
    @SuppressWarnings("unchecked")
    public T then() {
        return (T) this;
    }

    /**
     * Get current page object. Useful for e.g.
     * <code>myPage.get().then().with().aComponent().clickHome();</code>
     *
     * @return the current page object
     */
    @SuppressWarnings("unchecked")
    public T with() {
        return (T) this;
    }

    /**
     * Get new instance of a PageObject of type T, see {@link BasePage#get()}.
     *
     * @param url the url to open before initialising
     * @return PageObject of type T
     * @see BasePage#get()
     */
    public T get(String url) {
        driver.get(url);
        return get();
    }

    /**
     * Get new instance of a PageObject of type T,
     * see {@link BasePage#get(Duration)} for updating the timeout.
     *
     * @param url     the url to open before initialising
     * @param timeout the timeout for the new {@link Wait} for this page
     * @return new instance of a PageObject of type T, see {@link BasePage#get()}
     * @see BasePage#get()
     */
    public T get(String url, Duration timeout) {
        updatePageTimeout(timeout);
        return get(url);
    }

    /**
     * Get new instance of a PageObject of type T.
     *
     * @param timeout the timeout, in seconds, for the new {@link Wait} for this page
     * @return new instance of a PageObject of type T, see {@link BasePage#get()}
     * @see BasePage#get()
     */
    public T get(Duration timeout) {
        updatePageTimeout(timeout);
        return get();
    }

    /**
     * Initialises the PageObject.
     * <ul>
     * <li>Initialises fields with lazy proxies</li>
     * <li>Waits for Javascript events including document ready & JS frameworks (if applicable)</li>
     * <li>Processes Frameworkium visibility annotations e.g. {@link Visible}</li>
     * <li>Log page load to Capture</li>
     * </ul>
     *
     * @return the PageObject, of type T, populated with lazy proxies which are
     *         checked for visibility based upon appropriate Frameworkium annotations.
     */
    @SuppressWarnings("unchecked")
    public T get() {

        initPageObjectFields();
        visibility.waitForAnnotatedElementVisibility(this);
        takePageLoadedScreenshotAndSendToCapture();

        return (T) this;
    }

    /**
     * Method to initialise the fields in the page object.
     * Can be overridden where a custom implementation is desired.
     */
    protected void initPageObjectFields() {
        HtmlElementLoader.populatePageObject(this, driver);
    }

    private void updatePageTimeout(Duration timeout) {
        wait = UITestLifecycle.get().newWaitWithTimeout(timeout);
        visibility = new Visibility(wait);
    }

    private void takePageLoadedScreenshotAndSendToCapture() {
        if (ScreenshotCapture.isRequired()) {
            Command pageLoadCommand = new Command(
                    "load", "page", getSimplePageObjectName());
            UITestLifecycle.get().getCapture()
                    .takeAndSendScreenshot(pageLoadCommand, driver);
        }
    }

    private String getSimplePageObjectName() {
        String packageName = getClass().getPackage().getName();
        return packageName.substring(packageName.lastIndexOf('.') + 1)
                + "."
                + getClass().getSimpleName();
    }

    /**
     * @param javascript the Javascript to execute on the current page
     * @return One of Boolean, Long, String, List or WebElement. Or null.
     * @see JavascriptExecutor#executeScript(String, Object...)
     */
    protected Object executeJS(String javascript, Object... objects) {
        var jsExecutor = (JavascriptExecutor) driver;
        try {
            return jsExecutor.executeScript(javascript, objects);
        } catch (Exception e) {
            logger.error("Javascript execution failed!");
            logger.debug("Failed Javascript:\n{}", javascript, e);
            throw e;
        }
    }

}
