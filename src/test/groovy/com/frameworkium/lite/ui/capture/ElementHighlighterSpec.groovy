package com.frameworkium.lite.ui.capture

import org.openqa.selenium.JavascriptExecutor
import org.openqa.selenium.StaleElementReferenceException
import org.openqa.selenium.WebElement
import spock.lang.Specification

class ElementHighlighterSpec extends Specification {

    JavascriptExecutor mockJSE = Mock(JavascriptExecutor)
    def mockElement = Mock(WebElement)

    ElementHighlighter sut = new ElementHighlighter(mockJSE)

    def "provided element is highlighted and same element is un-highlighted"() {
        given: "The Javascript we expect to run"
            def highlightJS = "arguments[0].style.border='3px solid red'"
            def unhighlightJS = "arguments[0].style.border='none'"
        when: "We highlight then un-highlight an element"
            sut.highlightElement(mockElement)
            sut.unhighlightPrevious()
        then: "The correct scripts are executed against the given element"
            1 * mockJSE.executeScript(highlightJS, mockElement)
            1 * mockJSE.executeScript(unhighlightJS, mockElement)
    }

    def "StaleElementReferenceException's are caught"() {
        when: "We highlight then un-highlight an element"
            sut.highlightElement(mockElement)
            sut.unhighlightPrevious()
        then: "StaleElementReferenceException are not thrown"
            2 * mockJSE.executeScript(_ as String, mockElement) >> {
                throw new StaleElementReferenceException("")
            }
            notThrown(StaleElementReferenceException)
    }
}
