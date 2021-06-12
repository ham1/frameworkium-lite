package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.lite.htmlelements.element.Button;
import com.frameworkium.lite.htmlelements.element.FileInput;
import com.frameworkium.lite.ui.annotations.Visible;
import com.frameworkium.lite.ui.pages.BasePage;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class FileUploadPage extends BasePage<FileUploadPage> {

    @Visible
    @FindBy(css = "input#file-upload")
    private FileInput input;

    @FindBy(css = "input#file-submit")
    private Button uploadButton;

    public static FileUploadPage open() {
        return new FileUploadPage().get("https://the-internet.herokuapp.com/upload");
    }

    public FileUploadedPage upload(String filename) {
        input.setFileToUpload(filename);
        uploadButton.click();
        return new FileUploadedPage().get();
    }
}
