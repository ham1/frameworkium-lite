package com.frameworkium.integration.frameworkium.tests;

import com.frameworkium.integration.seleniumhq.pages.HomePage;
import com.frameworkium.lite.ui.tests.BaseUITest;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.RepeatedTest;
import org.openqa.selenium.remote.UnreachableBrowserException;

@Disabled("Can't retry failed tests in JUnit5?!?!")
public class ReInitDriverTest extends BaseUITest {

    private static boolean retry = true;

    @RepeatedTest(value = 2, name = "Reinitialise driver test")
    public void browser_is_re_initialised_if_test_is_restarted() {
        HomePage.open();
        if (retry) {
            retry = false;
            throw new UnreachableBrowserException("");
        }
    }
}
