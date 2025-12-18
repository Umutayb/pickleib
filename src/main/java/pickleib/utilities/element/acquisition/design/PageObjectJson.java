package pickleib.utilities.element.acquisition.design;

import collections.Pair;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.AppiumFluentWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.pagefactory.ByAll;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import pickleib.driver.DriverFactory;
import pickleib.enums.PrimarySelectorType;
import pickleib.enums.SelectorType;
import pickleib.platform.driver.PickleibAppiumDriver;
import pickleib.utilities.element.ElementBundle;
import pickleib.utilities.interfaces.repository.PageRepository;
import pickleib.web.driver.PickleibWebDriver;
import utils.Printer;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.*;
import java.util.*;
import java.util.stream.Collectors;

import static pickleib.utilities.platform.PlatformUtilities.getElementDriverType;
import static pickleib.utilities.platform.PlatformUtilities.isAppiumDriver;
import static utils.StringUtilities.Color.BLUE;
import static utils.StringUtilities.Color.GRAY;
import static utils.StringUtilities.contextCheck;
import static utils.StringUtilities.highlighted;

public class PageObjectJson implements PageRepository {

    RemoteWebDriver webDriver;
    RemoteWebDriver platformDriver;
    FluentWait<RemoteWebDriver> webWait;
    AppiumFluentWait<RemoteWebDriver> platformWait;
    JsonObject objectRepository;
    Printer log = new Printer(PageObjectJson.class);

    public PageObjectJson(
            RemoteWebDriver webDriver,
            RemoteWebDriver platformDriver,
            JsonObject objectRepository,
            int elementTimeout
    ) {
        this.webDriver = webDriver;
        this.platformDriver = platformDriver;
        this.objectRepository = objectRepository;

        webWait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(elementTimeout))
                .pollingEvery(Duration.ofMillis(500))
                .withMessage("Waiting for web element visibility...")
                .ignoring(WebDriverException.class);

        platformWait = (AppiumFluentWait<RemoteWebDriver>) new AppiumFluentWait<>(platformDriver)
                .withTimeout(Duration.ofSeconds(elementTimeout))
                .pollingEvery(Duration.ofMillis(500))
                .withMessage("Waiting for platform element visibility...")
                .ignoring(WebDriverException.class);
    }

    public PageObjectJson(JsonObject objectRepository) {
        this(PickleibWebDriver.get(), PickleibAppiumDriver.get(), objectRepository, 30);
    }

    /**
     * Acquire element from page (default selectors: xpath, css, text)
     */
    public WebElement acquireElementFromPage(String elementName, String pageName) {
        return elementFromPage(elementName, pageName);
    }

    /**
     * Acquire element from page (default selectors: xpath, css, text)
     */
    public List<WebElement> acquireElementsFromPage(String elementName, String pageName) {
        return elementsFromPage(elementName, pageName);
    }

    /**
     * Acquire a listed element from a list on a page
     */
    public WebElement acquireListedElementFromPage(String elementName, String listName, String pageName) {
        List<WebElement> all = elementsFromPage(listName, pageName);
        all.forEach(element -> System.out.println("Element text: " + element.getText()));
        return all.stream()
                .filter(e -> elementMatches(e, elementName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Element " + elementName + " not found in list " + listName));
    }

    /**
     * Acquire a listed element by attribute value.
     */
    public WebElement acquireListedElementByAttribute(
            String attributeName,
            String attributeValue,
            String listName,
            String pageName)
    {
        attributeValue = contextCheck(attributeValue);
        List<WebElement> elements = elementsFromPage(listName, pageName);

        for (WebElement element : elements) {
            try {
                if (attributeValue.equals(element.getAttribute(attributeName))) {
                    return element;
                }
            } catch (Exception ignored) {}
        }
        throw new NoSuchElementException(
                "No element in list '" + listName + "' on page '" + pageName +
                        "' has attribute '" + attributeName + "' = '" + attributeValue + "'"
        );
    }

    /**
     * Acquire element list with bundle structure.
     */
    public List<ElementBundle<String>> acquireElementList(
            List<Map<String, String>> signForms, String pageName
    ) {
        List<ElementBundle<String>> bundles = new ArrayList<>();

        for (Map<String, String> form : signForms) {
            String input = form.get("Input");
            String elementName = form.get("Input Element");
            String platformName = getPageJson(pageName, objectRepository)
                    .get("platform")
                    .getAsJsonPrimitive()
                    .getAsString();
            WebElement element = acquireElementFromPage(elementName, pageName);

            bundles.add(new ElementBundle<>(element, elementName, platformName, input));
        }
        return bundles;
    }

    /**
     * Acquire a single element bundle from a page
     */
    public ElementBundle<Map<String, String>> acquireElementBundleFromPage(
            String elementFieldName, String pageName, Map<String, String> specifications
    ) {
        WebElement element = acquireElementFromPage(elementFieldName, pageName);
        String platformName = getPageJson(pageName, objectRepository)
                .get("platform")
                .getAsJsonPrimitive()
                .getAsString();

        return new ElementBundle<>(element, elementFieldName, platformName, specifications);
    }

    /**
     * Acquire multiple element bundles from a page
     */
    public List<ElementBundle<Map<String, String>>> acquireElementBundlesFromPage(
            String pageName, List<Map<String, String>> specifications
    ) {
        return specifications.stream()
                .map(spec ->
                        acquireElementBundleFromPage(spec.get("Element Name"), pageName, spec)
                )
                .collect(Collectors.toList());
    }

    private boolean elementMatches(WebElement element, String expected) {
        try {
            if (element == null)
                return false;

            String elementText = !element.getText().isEmpty() ? element.getText() : element.getAttribute("value");
            return elementText.equalsIgnoreCase(expected) || elementText.contains(expected);
        }
        catch (NullPointerException nullElement){
            log.warning(nullElement.getLocalizedMessage());
            return getWaitForType(getElementDriverType(element))
                    .until(ExpectedConditions.textToBePresentInElement(element, expected));
        }
    }

    /**
     *
     * Acquires an element selector by desired selector types from a given Json file
     *
     * @param elementName target element name
     * @param pageName page name that includes target element selectors
     * @param selectorTypes desired selector types
     * @return target element
     */
    public WebElement elementFromPage(String elementName, String pageName, SelectorType... selectorTypes){
        log.info("Acquiring element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," from the ") +
                highlighted(BLUE, pageName)
        );

        JsonObject pageJson = getPageJson(pageName, objectRepository);
        JsonObject elementJson = getElementJson(elementName, pageJson);

        RemoteWebDriver driver = getDriverForPage(pageJson);

        ByAll byAll = getElementByAll(elementJson, driver, selectorTypes);
        getWaitForPage(pageJson).until(ExpectedConditions.presenceOfElementLocated(byAll));
        return driver.findElement(byAll);
    }

    /**
     *
     * Acquires an element list selector by desired selector types from a given Json file
     *
     * @param elementName target element name
     * @param pageName page name that includes target element selectors
     * @param selectorTypes desired selector types
     * @return target element list
     */
    public List<WebElement> elementsFromPage(String elementName, String pageName, SelectorType... selectorTypes){
        log.info("Acquiring element " +
                highlighted(BLUE, elementName) +
                highlighted(GRAY," from the ") +
                highlighted(BLUE, pageName)
        );

        JsonObject pageJson = getPageJson(pageName, objectRepository);
        JsonObject elementJson = getElementJson(elementName, pageJson);

        RemoteWebDriver driver = getDriverForPage(pageJson);

        ByAll byAll = getElementByAll(elementJson, driver, selectorTypes);
        getWaitForPage(pageJson).until(ExpectedConditions.presenceOfAllElementsLocatedBy(byAll));
        return driver.findElements(byAll);
    }

    /**
     * Constructs a compound {@link org.openqa.selenium.By} object based on the provided JSON representation
     * of a web element and one or more {@link SelectorType}s. Each provided SelectorType is used to generate
     * a corresponding Selenium {@link org.openqa.selenium.By} locator for the specified type within the element's JSON.
     *
     * @param elementJson The JSON representation of the web element containing various locator information.
     * @param selectorTypes One or more SelectorType enums specifying the types of locators to be generated.
     * @return A compound By object, formed by combining individual locators based on the specified SelectorTypes.
     * @throws EnumConstantNotPresentException If an unsupported SelectorType is provided.
     * @see org.openqa.selenium.By
     * @see SelectorType
     */
    public ByAll getElementByAll(JsonObject elementJson, RemoteWebDriver driver, SelectorType... selectorTypes){
        List<By> locators = new ArrayList<>();
        if (selectorTypes.length > 0)
            for (SelectorType selectorType:selectorTypes) {
                try {
                    By locator;
                    switch (selectorType){
                        case id -> locator =
                                By.id(getPlatformSelector(elementJson, driver, selectorType));
                        case xpath -> locator =
                                By.xpath(getPlatformSelector(elementJson, driver, selectorType));
                        case css -> locator =
                                By.cssSelector(getPlatformSelector(elementJson, driver, selectorType));
                        case className -> locator =
                                By.className(getPlatformSelector(elementJson, driver, selectorType));
                        case tagName -> locator =
                                By.tagName(getPlatformSelector(elementJson, driver, selectorType));
                        case name -> locator =
                                By.name(getPlatformSelector(elementJson, driver, selectorType));
                        case accessibilityId -> locator =
                                AppiumBy.accessibilityId(getPlatformSelector(elementJson, driver, selectorType));
                        case androidDataMatcher -> locator =
                                AppiumBy.androidDataMatcher(getPlatformSelector(elementJson, driver, selectorType));
                        case androidViewMatcher -> locator =
                                AppiumBy.androidViewMatcher(getPlatformSelector(elementJson, driver, selectorType));
                        case androidViewTag -> locator =
                                AppiumBy.androidViewTag(getPlatformSelector(elementJson, driver, selectorType));
                        case androidUIAutomator -> locator =
                                AppiumBy.androidUIAutomator(getPlatformSelector(elementJson, driver, selectorType));
                        case iOSClassChain -> locator =
                                AppiumBy.iOSClassChain(getPlatformSelector(elementJson, driver, selectorType));
                        case iOSNsPredicateString -> locator =
                                AppiumBy.iOSNsPredicateString(getPlatformSelector(elementJson, driver, selectorType));
                        case text -> {
                            String text = elementJson.get("text").getAsJsonPrimitive().getAsString();
                            //TODO: text locator method might vary depending on platform! Implement dynamic text locator name
                            locator = By.xpath("//*[text()='" + text + "']");
                        }
                        default -> throw new EnumConstantNotPresentException(SelectorType.class, selectorType.name());
                    }
                    locators.add(locator);
                }
                catch (NullPointerException | IllegalArgumentException | IllegalStateException ignored){}
            }
        else return getElementByAll(
                elementJson,
                driver,
                SelectorType.values()
        );
        return new ByAll(locators.toArray(new By[0]));
    }

    /**
     * Retrieves a platform-specific selector string from the page repository JSON object.
     *
     * @param elementJson The JSON object containing the selector definitions.  It is expected to have a "selectors" field which is a JSON object.
     *                    This inner object should contain keys representing platform names (lowercase) and values that are JSON arrays of selector strings.
     * @param driver The {@link RemoteWebDriver} instance. Used to determine the platform (e.g., Windows, iOS, Android, Web).
     * @param selectorType The type of selector to retrieve.
     * @return The selector string for the specified platform and selector type, or {@code null} if no matching selector is found.
     * @throws IllegalArgumentException if `elementJson` does not contain a "selectors" field,
     *                                  or if the "selectors" field is not a JSON object,
     *                                  or if the platform key is not found within the "selectors" object,
     *                                  or if the value associated with the platform key is not a JSON array.
     */
    String getPlatformSelector(JsonObject elementJson, RemoteWebDriver driver, SelectorType selectorType){
        JsonObject elementSelectors = elementJson.get("selectors").getAsJsonObject();
        Platform platform = driver.getCapabilities().getPlatformName();
        String platformName = isAppiumDriver(driver) ? platform.name().toLowerCase() : "web";
        JsonArray selectors =  elementSelectors.get(platformName).getAsJsonArray();
        for (JsonElement selector : selectors){
            if (selector.getAsJsonObject().has(selectorType.getKey()))
                return selector.getAsJsonObject().get(selectorType.getKey()).getAsJsonPrimitive().getAsString();
        }
        return null;
    }

    /**
     * Generates an element using a primary selector by given element attributes (css or xpath)
     *
     * @param selectorType desired primary selector type
     * @param attributePairs target element attributes as 'label = value'
     * @return target element
     */
    @SafeVarargs
    public final WebElement getElementByAttributes(
            PrimarySelectorType selectorType,
            DriverFactory.DriverType driverType,
            Pair<String, String>... attributePairs)
    {
        By locator;
        switch (selectorType){
            case css ->     locator = By.cssSelector(generateCssByAttributes(attributePairs));
            case xpath ->   locator = By.xpath(generateXPathByAttributes(attributePairs));
            default -> throw new EnumConstantNotPresentException(PrimarySelectorType.class, selectorType.name());
        }
        getWaitForType(driverType).until(ExpectedConditions.presenceOfElementLocated(locator));
        return getDriverForType(driverType).findElement(locator);
    }

    /**
     *
     * Generates an element list using a primary selector by given element attributes (css or xpath)
     *
     * @param attributePairs target element attributes as 'label = value'
     * @return target element list
     */
    @SafeVarargs
    public final List<WebElement> getElementsByAttributes(
            PrimarySelectorType selectorType,
            DriverFactory.DriverType driverType,
            Pair<String, String>... attributePairs)
    {
        By locator;
        switch (selectorType){
            case css ->     locator = By.cssSelector(generateCssByAttributes(attributePairs));
            case xpath ->   locator = By.xpath(generateXPathByAttributes(attributePairs));
            default -> throw new EnumConstantNotPresentException(PrimarySelectorType.class, selectorType.name());
        }
        getWaitForType(driverType).until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        return getDriverForType(driverType).findElements(locator);
    }

    /**
     *
     * Generates cssSelector by element attributes
     *
     * @param attributePairs target element attributes as 'label = value'
     * @return target element selector
     */
    @SafeVarargs
    public final String generateCssByAttributes(Pair<String, String>... attributePairs){
        StringBuilder selector = new StringBuilder();
        for (Pair<String, String> attributePair:attributePairs) {
            StringJoiner cssFormat = new StringJoiner(
                    "",
                    "[",
                    "']"
            );
            selector.append(cssFormat.add(attributePair.alpha() + " = '" + attributePair.beta()));
        }
        return selector.toString();
    }

    /**
     *
     * Generates xPath by element attributes
     *
     * @param attributePairs target element attributes as 'label = value'
     * @return target element selector
     */
    @SafeVarargs
    public final String generateXPathByAttributes(Pair<String, String>... attributePairs){
        StringBuilder selector = new StringBuilder();
        selector.append("//*");
        for (Pair<String, String> attributePair:attributePairs) {
            StringJoiner cssFormat = new StringJoiner(
                    "",
                    "[@",
                    "']"
            );
            selector.append(cssFormat.add(attributePair.alpha() + " = '" + attributePair.beta()));
        }
        return selector.toString();
    }

    /**
     * Acquires specified selectors for target element from a given Json file.
     * Json file includes specified page names with element selectors.
     *
     * @param elementName specified target element name
     * @return target element selectors as JsonObject
     */
    public JsonObject getElementJson(String elementName, JsonObject pageJson){
        JsonArray elements = pageJson.getAsJsonArray("elements");
        for (JsonElement elementJson:elements)
            if (elementJson
                    .getAsJsonObject()
                    .get("elementName")
                    .getAsJsonPrimitive()
                    .getAsString()
                    .equalsIgnoreCase(elementName)
            ) return elementJson.getAsJsonObject();

        return null;
    }

    /**
     * Retrieves a JSON object representing a page from the object repository based on the page name.
     *
     * @param pageName The name of the page to retrieve.
     * @param objectRepository The JSON object repository containing page configurations.  Assumes the repository has a "pages" array.
     * @return The JSON object representing the page, or null if the page is not found.  Throws a NullPointerException if the page is not found.
     * @throws NullPointerException if the page with the given name is not found in the 'pages' array.
     */
    public JsonObject getPageJson(String pageName, JsonObject objectRepository) {
        JsonArray pages = objectRepository.getAsJsonArray("pages");
        return Objects.requireNonNull(
                pages.asList().stream().filter(
                        page -> page
                                .getAsJsonObject()
                                .get("name")
                                .getAsJsonPrimitive()
                                .getAsString()
                                .equalsIgnoreCase(pageName)
                ).findAny().orElse(null)
        ).getAsJsonObject();
    }

    /**
     * Retrieves a RemoteWebDriver instance for a given page JSON object.
     *
     * @param pageJson The JSON object representing the page.  Must contain a "platform" field.
     * @return The RemoteWebDriver instance for the specified platform.
     */
    public RemoteWebDriver getDriverForPage(JsonObject pageJson) {
        DriverFactory.DriverType driverType = DriverFactory.DriverType.getType(
                pageJson
                        .get("platform")
                        .getAsJsonPrimitive()
                        .getAsString()
        );
        return getDriverForType(driverType);
    }

    /**
     * Retrieves a RemoteWebDriver instance for a given DriverType.
     *
     * @param driverType The DriverType to retrieve the driver for.
     * @return The RemoteWebDriver instance for the specified DriverType.
     * @throws EnumConstantNotPresentException if the DriverType is not supported.
     */
    public RemoteWebDriver getDriverForType(DriverFactory.DriverType driverType){
        switch (driverType){
            case appium -> {
                return platformDriver;
            }
            case selenium -> {
                return webDriver;
            }
            default -> throw new EnumConstantNotPresentException(DriverFactory.DriverType.class, driverType.name());
        }
    }

    /**
     * Retrieves a RemoteWebDriver instance for a given page JSON object.
     *
     * @param pageJson The JSON object representing the page.  Must contain a "platform" field.
     * @return The RemoteWebDriver instance for the specified platform.
     */
    public FluentWait<RemoteWebDriver> getWaitForPage(JsonObject pageJson) {
        DriverFactory.DriverType driverType = DriverFactory.DriverType.getType(
                pageJson
                        .get("platform")
                        .getAsJsonPrimitive()
                        .getAsString()
        );
        return getWaitForType(driverType);
    }

    /**
     * Retrieves a RemoteWebDriver instance for a given DriverType.
     *
     * @param driverType The DriverType to retrieve the driver for.
     * @return The RemoteWebDriver instance for the specified DriverType.
     * @throws EnumConstantNotPresentException if the DriverType is not supported.
     */
    public FluentWait<RemoteWebDriver> getWaitForType(DriverFactory.DriverType driverType){
        switch (driverType){
            case appium -> {
                return platformWait;
            }
            case selenium -> {
                return webWait;
            }
            default -> throw new EnumConstantNotPresentException(DriverFactory.DriverType.class, driverType.name());
        }
    }

}
