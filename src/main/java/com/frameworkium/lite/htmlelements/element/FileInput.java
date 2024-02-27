package com.frameworkium.lite.htmlelements.element;

import static com.frameworkium.lite.htmlelements.utils.HtmlElementUtils.existsInClasspath;
import static com.frameworkium.lite.htmlelements.utils.HtmlElementUtils.getResourceFromClasspath;

import org.openqa.selenium.WebElement;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents web page file upload element.
 *
 * <p>N.B. When using GridImpl, the LocalFileDetector is set to allow file uploads to work.
 * This was previously done in this class but was moved to GridImpl
 * as that's the only place where we can garuntee the driver is a RemoteWebDriver.
 */
public class FileInput extends TypifiedElement {

    /**
     * Specifies wrapped {@link WebElement}.
     *
     * @param wrappedElement {@code WebElement} to wrap.
     */
    public FileInput(WebElement wrappedElement) {
        super(wrappedElement);
    }

    /**
     * Sets a file to be uploaded.
     * <p>
     * File is searched in the following way: if a resource with a specified name exists in classpath,
     * then this resource will be used, otherwise file will be searched on file system.
     *
     * @param fileName Name of a file or a resource to be uploaded.
     */
    public void setFileToUpload(final String fileName) {
        sendKeys(getFilePath(fileName));
    }

    /**
     * Sets multiple files to be uploaded.
     * <p>
     * Files are searched in the following way:
     * if a resource with a specified name exists in classpath,
     * then this resource will be used, otherwise file will be searched on file system.
     *
     * @param fileNames a list of file Names to be uploaded.
     */
    public void setFilesToUpload(List<String> fileNames) {
        String filePaths =
                fileNames.stream().map(this::getFilePath).collect(Collectors.joining("\n"));
        sendKeys(filePaths);
    }

    /**
     * Submits selected file by simply submitting the whole form, which contains this file input.
     */
    @Override
    public void submit() {
        getWrappedElement().submit();
    }

    private String getFilePath(final String fileName) {
        if (existsInClasspath(fileName)) {
            return getPathForResource(fileName);
        }
        return getPathForSystemFile(fileName);
    }

    private String getPathForResource(final String fileName) {
        return getResourceFromClasspath(fileName).getPath();
    }

    private String getPathForSystemFile(final String fileName) {
        return new File(fileName).getPath();
    }
}
