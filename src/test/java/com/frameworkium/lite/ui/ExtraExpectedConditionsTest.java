package com.frameworkium.lite.ui;

import com.frameworkium.lite.htmlelements.element.Link;
import com.frameworkium.lite.ui.element.StreamTable;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests to ensure elements other than just WebElements can be passed into
 * {@link ExtraExpectedConditions}.
 * <p>
 * These tests do not check the correctness of the implementation, they are here
 * to prevent <a href="https://github.com/Frameworkium/frameworkium-core/issues/133">issue 133</a>
 */
public class ExtraExpectedConditionsTest {

    private final List<Link> links = new ArrayList<>();
    private final List<StreamTable> tables = new ArrayList<>();

    @Test
    public void testNotPresentOrInvisibleWorksWithTypifiedElements() {
        ExtraExpectedConditions.notPresentOrInvisible(links);
    }

    @Test
    public void testSizeGreaterThanWorksWithTypifiedElements() {
        ExtraExpectedConditions.sizeGreaterThan(links, 1);
    }

    @Test
    public void testSizeLessThanWorksWithTypifiedElements() {
        ExtraExpectedConditions.sizeLessThan(links, 3);
    }

    @Test
    public void testNotPresentOrInvisibleWorksWithHtmlElements() {
        ExtraExpectedConditions.notPresentOrInvisible(tables);
    }

    @Test
    public void testSizeGreaterThanWorksWithHtmlElements() {
        ExtraExpectedConditions.sizeGreaterThan(tables, 1);
    }

    @Test
    public void testSizeLessThanWorksWithHtmlElements() {
        ExtraExpectedConditions.sizeLessThan(tables, 3);
    }
}
