package com.frameworkium.integration.frameworkium.tests;

import com.frameworkium.integration.theinternet.pages.WelcomePage;
import com.frameworkium.lite.ui.tests.BaseUITest;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.UnreachableBrowserException;

public class ReInitDriverTest extends BaseUITest {

    private static boolean retry = true;

    @Test
    public void browser_is_re_initialised_if_test_is_restarted() {
        WelcomePage.open();
        if (retry) {
            retry = false;
            throw new UnreachableBrowserException("");
        }
    }
}
