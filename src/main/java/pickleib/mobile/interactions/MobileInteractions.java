package pickleib.mobile.interactions;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.driver.DriverFactory;
import pickleib.enums.ElementState;
import pickleib.enums.SwipeDirection;
import pickleib.mobile.driver.PickleibAppiumDriver;
import pickleib.mobile.utilities.MobileUtilities;
import pickleib.utilities.Interactions;
import pickleib.utilities.element.ElementInteractions;
import records.Bundle;
import java.util.List;
import java.util.Map;

import static utils.StringUtilities.Color.*;

@SuppressWarnings("unused")
public class MobileInteractions extends MobileUtilities implements Interactions {

    public ElementInteractions interact;
    protected RemoteWebDriver driver;
    protected WebDriverWait wait;
    boolean scroll = false;

    public MobileInteractions(RemoteWebDriver driver, WebDriverWait wait){
        super(driver);
        this.driver = driver;
        this.wait = wait;
        interact = new ElementInteractions(
                driver,
                wait,
                DriverFactory.DriverType.Mobile
        );
    }

    public MobileInteractions(){
        super(PickleibAppiumDriver.driver);
        this.driver = PickleibAppiumDriver.driver;
        this.wait = PickleibAppiumDriver.wait;
        interact = new ElementInteractions(
                driver,
                wait,
                DriverFactory.DriverType.Mobile
        );
    }

    public boolean isScrolling() {
        return scroll;
    }

    public void setScroll(boolean scroll) {
        this.scroll = scroll;
    }

    /**
     *
     * Scroll {direction}
     *
     * @param direction target direction (up or down)
     */
    public void swipeInDirection(SwipeDirection direction){swiper(direction);}

    /**
     *
     * Center the {element name} on the {page name}
     *
     * @param element target element
     * @param elementName target element name
     * @param pageName specified page instance name
     */
    public void center(WebElement element, String elementName, String pageName){
        log.info("Centering " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," on ") +
                highlighted(BLUE, pageName)
        );
        center(element);
    }

    /**
     * Center a given element
     *
     * @param element target element
     * @return the given element
     */
    public WebElement center(WebElement element){
        centerElement(element);
        return element;
    }

    public void addLocalStorageValues(Map<String, String> form) {
        interact.addLocalStorageValues(form);
    }

    public void addCookies(Map<String, String> cookies) {
        interact.addCookies(cookies);
    }

    public void deleteCookies() {
        interact.deleteCookies();
    }

    public void clickByText(String text) {
        interact.clickByText(text);
    }

    public void waitForSeconds(Integer duration) {
        interact.waitForSeconds(duration);
    }

    public void clickInteraction(WebElement button, String buttonName, String pageName) {
        interact.clickInteraction(button, buttonName, pageName);
    }

    public void clickInteraction(WebElement button) {
        interact.clickInteraction(button);

    }

    public void saveAttributeValue(WebElement element, String attributeName, String elementName, String pageName) {
        interact.saveAttributeValue(element, attributeName, elementName, pageName);
    }

    public void clickTowards(WebElement element, String elementName, String pageName) {
        interact.clickTowards(element, elementName, pageName);
    }

    public void clickIfPresent(WebElement element, String elementName, String pageName) {
        interact.clickIfPresent(element, elementName, pageName);
    }

    public void basicFill(WebElement inputElement, String inputName, String pageName, String input, boolean verify) {
        interact.basicFill(inputElement, inputName, pageName, input, verify);
    }

    public void fillForm(List<Bundle<WebElement, String, String>> bundles, String pageName) {
        interact.fillForm(bundles, pageName);
    }

    public void verifyText(WebElement element, String elementName, String pageName, String expectedText) {
        interact.verifyText(element, elementName, pageName, expectedText);
    }

    public void verifyContainsText(WebElement element, String elementName, String pageName, String expectedText) {
        interact.verifyContainsText(element, elementName, pageName, expectedText);
    }

    public void verifyListedText(List<Bundle<WebElement, String, String>> bundles, String pageName) {
        interact.verifyListedText(bundles, pageName);
    }

    public void verifyPresence(WebElement element, String elementName, String pageName) {
        interact.verifyPresence(element, elementName, pageName);
    }

    public void quitDriver() {
        interact.quitDriver();
    }

    public void verifyState(WebElement element, String elementName, String pageName, ElementState expectedState) {
        interact.verifyState(element, elementName, pageName, expectedState);
    }

    public boolean elementIs(WebElement element, String elementName, String pageName, ElementState expectedState) {
        return interact.elementIs(element, elementName, pageName, expectedState);
    }

    public void waitUntilAbsence(WebElement element, String elementName, String pageName) {
        interact.waitUntilAbsence(element, elementName, pageName);
    }

    public void waitUntilVisible(WebElement element, String elementName, String pageName) {
        interact.waitUntilVisible(element, elementName, pageName);
    }

    public void waitUntilElementContainsAttribute(WebElement element, String elementName, String pageName, String attributeName, String attributeValue) {
        interact.waitUntilElementContainsAttribute(element, elementName, pageName, attributeName, attributeValue);
    }

    public void verifyElementContainsAttribute(WebElement element, String elementName, String pageName, String attributeName, String attributeValue) {
        interact.verifyElementContainsAttribute(element, elementName, pageName, attributeName, attributeValue);
    }

    public void verifyElementAttributeContainsValue(WebElement element, String attributeName, String elementName, String pageName, String value) {
        interact.verifyElementAttributeContainsValue(element, attributeName, elementName, pageName, value);
    }

    public void verifyElementColor(WebElement element, String attributeName, String elementName, String pageName, String attributeValue) {
        interact.verifyElementColor(element, attributeName, elementName, pageName, attributeValue);
    }

    public void verifyPresenceOfListedElements(List<Bundle<WebElement, String, String>> bundles, WebElement element, List<WebElement> elements, String pageName, List<Map<String, String>> signForms) {
        interact.verifyPresenceOfListedElements(bundles, element, elements, pageName, signForms);
    }

    public void clickButtonByText(String buttonText, Boolean scroll) {
        interact.clickButtonByText(buttonText, scroll);
    }

    public void updateContext(String key, String value) {
        interact.updateContext(key, value);
    }

    public void pressKey(WebElement element, String elementName, String pageName, Keys... keys) {
        interact.pressKey(element, elementName, pageName, keys);
    }

    public void fillInputWithFile(WebElement inputElement, String inputName, String pageName, String absoluteFilePath) {
        interact.fillInputWithFile(inputElement, inputName, pageName, absoluteFilePath);
    }

    public void bundleInteraction(List<Bundle<String, WebElement, Map<String, String>>> bundles, String pageName) {
        interact.bundleInteraction(bundles, pageName);
    }
}