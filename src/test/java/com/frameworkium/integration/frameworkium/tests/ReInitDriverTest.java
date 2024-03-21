package com.frameworkium.integration.frameworkium.tests;

import com.frameworkium.integration.seleniumhq.pages.HomePage;
import com.frameworkium.lite.ui.tests.BaseUITest;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.UnreachableBrowserException;

public class ReInitDriverTest extends BaseUITest {

    private static boolean retry = true;

    @Override
    protected void beforeEachWithRetry() {
        System.out.println("beforeEachWithRetry");
        if (retry) {
            retry = false;
            throw new ArrayIndexOutOfBoundsException("");
        }
        retry = true;
    }

    /**
     * This only works when running with maven because of rerunFailingTestsCount
     */
    @Test
    public void browser_is_re_initialised_if_test_is_restarted() {
        if (retry) {
            retry = false;
            throw new UnreachableBrowserException("");
        }
        HomePage.open();
    }
}
