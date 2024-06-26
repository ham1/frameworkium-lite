package com.frameworkium.integration.theinternet.pages;

import static java.time.temporal.ChronoUnit.SECONDS;

import com.frameworkium.lite.htmlelements.element.Link;
import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;
import com.frameworkium.lite.ui.pages.PageFactory;

import org.openqa.selenium.support.FindBy;

import java.time.Duration;

public class WelcomePage extends BasePage<WelcomePage> {

    private static final String URL = "https://the-internet.herokuapp.com";

    @Visible
    @FindBy(linkText = "Checkboxes")
    private Link checkboxesLink;

    @FindBy(linkText = "Drag and Drop")
    private Link dragAndDropLink;

    @FindBy(linkText = "Dropdown")
    private Link dropdownLink;

    @FindBy(linkText = "Hovers")
    private Link hoversLink;

    @FindBy(linkText = "JavaScript Alerts")
    private Link javascriptAlertsLink;

    @FindBy(linkText = "Key Presses")
    private Link keyPressesLink;

    public static WelcomePage open() {
        return PageFactory.newInstance(WelcomePage.class, URL);
    }

    /**
     * Static factory method for this page object with specified timeout.
     *
     * @param timeout timeout in seconds
     * @return An instance of this page object with specified wait timeout
     */
    public static WelcomePage open(long timeout) {
        return new WelcomePage().get(URL, Duration.of(timeout, SECONDS));
    }

    public CheckboxesPage clickCheckboxesLink() {
        checkboxesLink.click();
        return PageFactory.newInstance(CheckboxesPage.class, Duration.of(15, SECONDS));
    }

    public DragAndDropPage clickDragAndDropLink() {
        dragAndDropLink.click();
        return PageFactory.newInstance(DragAndDropPage.class);
    }

    public HoversPage clickHoversLink() {
        hoversLink.click();
        return PageFactory.newInstance(HoversPage.class);
    }

    public DropDownPage clickDropDownLink() {
        dropdownLink.click();
        return new DropDownPage().get();
    }

    public JavaScriptAlertsPage clickJavascriptAlertsLink() {
        javascriptAlertsLink.click();
        return PageFactory.newInstance(JavaScriptAlertsPage.class);
    }

    public KeyPressesPage clickKeyPressesLink() {
        keyPressesLink.click();
        return PageFactory.newInstance(KeyPressesPage.class);
    }
}
