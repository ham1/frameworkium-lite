package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.lite.htmlelements.element.CheckBox;
import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;

import org.openqa.selenium.support.FindBy;

import java.util.List;
import java.util.stream.Stream;

public class CheckboxesPage extends BasePage<CheckboxesPage> {

    @Visible(checkAtMost = 1)
    @FindBy(css = "form input[type='checkbox']")
    private List<CheckBox> allCheckboxes;

    public CheckboxesPage checkAllCheckboxes() {
        allCheckboxes.forEach(CheckBox::select);
        return this;
    }

    public Stream<Boolean> getAllCheckboxCheckedStatus() {
        return allCheckboxes.stream().map(CheckBox::isSelected);
    }
}
