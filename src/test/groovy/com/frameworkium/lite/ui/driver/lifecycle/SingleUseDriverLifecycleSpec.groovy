package com.frameworkium.lite.ui.driver.lifecycle

import com.frameworkium.lite.ui.driver.Driver
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import spock.lang.Specification

class SingleUseDriverLifecycleSpec extends Specification {

    WebDriver EFWebDriverMock = Mock(WebDriver)
    def driverMock = Mock(Driver) {
        getWebDriver() >> EFWebDriverMock
    }
    def driverSupplier = { driverMock }

    def sut = new SingleUseDriverLifecycle(driverSupplier)

    def "following expected lifecycle yields correct driver"() {
        when:
            sut.initDriverPool()
            sut.initBrowserBeforeTest()
        then:
            sut.getWebDriver() == EFWebDriverMock

        when:
            sut.tearDownDriver()
            sut.tearDownDriverPool()
        then:
            1 * EFWebDriverMock.quit()
            noExceptionThrown()
    }

    def "following subset of expected lifecycle yields correct driver for SingleUseDriverLifecycle"() {
        when:
            sut.initBrowserBeforeTest()
        then:
            sut.getWebDriver() == EFWebDriverMock
        when:
            sut.tearDownDriver()
        then:
            1 * EFWebDriverMock.quit()
            noExceptionThrown()
    }

    def "if a driver fails to tearDown exception will not be thrown"() {
        given:
            sut.initBrowserBeforeTest()
        when:
            sut.tearDownDriver()
        then:
            1 * EFWebDriverMock.quit() >> { throw new Exception("") }
    }

    def "reinit current driver quits existing and returns new"() {
        given:
            sut.initBrowserBeforeTest()
            assert sut.getWebDriver() == EFWebDriverMock
        when:
            sut.reinitialiseCurrentDriver()
            sut.initBrowserBeforeTest()
        then:
            sut.getWebDriver() == EFWebDriverMock
    }
}
