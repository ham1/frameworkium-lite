package com.frameworkium.integration.theinternet.pages;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

import com.frameworkium.lite.htmlelements.element.Button;
import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class JavaScriptAlertsPage extends BasePage<JavaScriptAlertsPage> {

    @Visible
    @FindBy(css = "button[onclick='jsAlert()']")
    private Button jsAlertButton;

    @FindBy(css = "p#result")
    private WebElement resultArea;

    public JavaScriptAlertsPage clickAlertButtonAndAccept() {
        jsAlertButton.click();
        driver.switchTo().alert().accept();
        wait.until(visibilityOf(resultArea));
        return this;
    }

    public String getResultText() {
        return resultArea.getText();
    }
}
