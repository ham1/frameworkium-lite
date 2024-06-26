package com.frameworkium.lite.ui.capture;

import org.openqa.selenium.*;

public class ElementHighlighter {

    public static final String UNHIGHLIGHT_ELEMENT_SCRIPT = "arguments[0].style.border='none'";
    public static final String HIGHLIGHT_ELEMENT_SCRIPT =
            "arguments[0].style.border='3px solid red'";

    private final JavascriptExecutor js;
    private WebElement previousElem;

    public ElementHighlighter(WebDriver driver) {
        js = (JavascriptExecutor) driver;
    }

    ElementHighlighter(JavascriptExecutor javascriptExecutor) {
        js = javascriptExecutor;
    }

    /**
     * Highlight a WebElement.
     *
     * @param webElement to highlight
     */
    public void highlightElement(WebElement webElement) {

        previousElem = webElement; // remember the new element
        try {
            // TODO: save the previous border
            js.executeScript(HIGHLIGHT_ELEMENT_SCRIPT, webElement);
        } catch (StaleElementReferenceException ignored) {
            // something went wrong, but no need to crash for highlighting
        }
    }

    /**
     * Unhighlight the previously highlighted WebElement.
     */
    public void unhighlightPrevious() {

        try {
            // unhighlight the previously highlighted element
            js.executeScript(UNHIGHLIGHT_ELEMENT_SCRIPT, previousElem);
        } catch (StaleElementReferenceException ignored) {
            // the page was reloaded/changed, the same element isn't there
        }
    }
}
