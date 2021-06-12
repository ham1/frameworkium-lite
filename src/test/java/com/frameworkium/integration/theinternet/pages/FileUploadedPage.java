package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class FileUploadedPage extends BasePage<FileUploadedPage> {

    @Visible
    @FindBy(css = "#uploaded-files")
    private WebElement uploadedFiles;

    public String getUploadedFileNames() {
        return uploadedFiles.getText();
    }
}
