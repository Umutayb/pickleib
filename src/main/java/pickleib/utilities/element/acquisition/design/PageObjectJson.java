package pickleib.utilities.element.acquisition.design;

import collections.Pair;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.appium.java_client.AppiumBy;
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
import pickleib.utilities.interfaces.repository.ElementRepository;
import pickleib.web.driver.PickleibWebDriver;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.*;
import java.util.*;
import java.util.stream.Collectors;

import static pickleib.utilities.DriverInspector.getElementDriverType;
import static pickleib.utilities.DriverInspector.isAppiumDriver;
import static utils.StringUtilities.Color.BLUE;
import static utils.StringUtilities.Color.GRAY;
import static utils.StringUtilities.contextCheck;
import static utils.StringUtilities.highlighted;

/**
 * Concrete implementation of {@link ElementRepository} designed for the <b>Low-Code JSON</b> pattern.
 * <p>
 * This class acts as a bridge between a JSON-based Object Repository and the underlying WebDriver.
 * Instead of looking up Java fields annotated with {@code @FindBy}, it parses a JSON object
 * (representing the {@code page-repository.json} file) to locate elements dynamically at runtime.
 * </p>
 *
 *
 *
 * <p>
 * It supports multi-platform execution by automatically selecting the correct driver (Web or Appium)
 * based on the "platform" key defined in the page JSON.
 * </p>
 *
 * @author  Umut Ay Bora
 * @since   2.0.6
 */
public class PageObjectJson implements ElementRepository {

    RemoteWebDriver webDriver;
    RemoteWebDriver platformDriver;
    FluentWait<RemoteWebDriver> webWait;
    AppiumFluentWait<RemoteWebDriver> platformWait;
    JsonObject objectRepository;

    /**
     * Full constructor for dependency injection.
     *
     * @param webDriver        The driver instance for Web UI testing.
     * @param platformDriver   The driver instance for Mobile/Desktop testing (Appium).
     * @param objectRepository The parsed JSON object containing page and element definitions.
     * @param elementTimeout   The timeout duration (in seconds) for finding elements.
     */
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

    /**
     * Convenience constructor using Singleton drivers from {@link PickleibWebDriver} and {@link PickleibAppiumDriver}.
     * Defaults the element timeout to 30 seconds.
     *
     * @param objectRepository The parsed JSON object containing page and element definitions.
     */
    public PageObjectJson(JsonObject objectRepository) {
        this(PickleibWebDriver.get(), PickleibAppiumDriver.get(), objectRepository, 30);
    }

    /**
     * Acquires a single element from a page using the definition in the JSON repository.
     * <p>
     * This method looks up the page in the JSON object, finds the element definition,
     * determines the correct driver/platform, and uses {@link #elementFromPage(String, String, SelectorType...)}
     * to locate it.
     * </p>
     *
     * @param elementName The name of the element as defined in the JSON (e.g., "submitButton").
     * @param pageName    The name of the page as defined in the JSON (e.g., "LoginPage").
     * @return The located {@link WebElement}.
     * @throws org.openqa.selenium.TimeoutException if the element is not found within the timeout period.
     */
    public WebElement acquireElementFromPage(String elementName, String pageName) {
        return elementFromPage(elementName, pageName);
    }

    /**
     * Acquires a list of elements from a page using the definition in the JSON repository.
     * <p>
     * Useful for elements that share the same selector (e.g., a list of menu items).
     * </p>
     *
     * @param elementName The name of the element list definition in the JSON.
     * @param pageName    The name of the page in the JSON.
     * @return A list of located {@link WebElement}s.
     */
    public List<WebElement> acquireElementsFromPage(String elementName, String pageName) {
        return elementsFromPage(elementName, pageName);
    }

    /**
     * Acquires a specific element from a list based on its text content.
     * <p>
     * This method fetches a list of elements matching the definition and filters them
     * to find the one whose text matches {@code elementName}.
     * </p>
     *
     * @param elementName The text content to search for within the list (e.g., "Product A").
     * @param listName    The name of the list element in the JSON repository.
     * @param pageName    The name of the page in the JSON repository.
     * @return The matching {@link WebElement}.
     * @throws NoSuchElementException If no element in the list matches the text.
     */
    public WebElement acquireListedElementFromPage(String elementName, String listName, String pageName) {
        List<WebElement> all = elementsFromPage(listName, pageName);
        return all.stream()
                .filter(e -> checkElementTextMatch(e, contextCheck(elementName)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Element " + elementName + " not found in list " + listName));
    }

    /**
     * Acquires a listed element by matching a specific attribute value.
     * <p>
     * This iterates through the elements found by {@code listName} and checks the value
     * of {@code attributeName}.
     * </p>
     *
     * @param attributeName  The HTML attribute to inspect (e.g., "href", "class", "id").
     * @param attributeValue The expected value of the attribute.
     * @param listName       The name of the list element in the JSON repository.
     * @param pageName       The name of the page in the JSON repository.
     * @return The matching {@link WebElement}.
     * @throws NoSuchElementException If no element matches the attribute criteria.
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
     * Converts a Cucumber Data Table of inputs into a list of {@link ElementBundle}s.
     * <p>
     * This allows iterating over a form definition in a feature file and automatically
     * mapping the "Input Element" column to actual WebElements found via JSON.
     * </p>
     *
     * @param signForms A list of maps from a Cucumber DataTable (keys: "Input Element", "Input").
     * @param pageName  The name of the page where these elements exist.
     * @return A list of bundles linking the element to the data to be entered.
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
     * Acquires a single element bundle containing the element, its name, platform, and specification map.
     *
     * @param elementFieldName The name of the element in the JSON.
     * @param pageName         The name of the page in the JSON.
     * @param specifications   A map of additional data related to the element.
     * @return An {@link ElementBundle} wrapping the element and its metadata.
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
     * Bulk acquisition of element bundles based on a list of specifications.
     *
     * @param pageName       The name of the page in the JSON.
     * @param specifications A list of maps, where each map must contain an "Element Name" key.
     * @return A list of element bundles.
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

    /**
     * Determines if the specified WebElement matches the expected text value, accounting for
     * potential asynchronous content loading.
     * <p>
     * The method first checks the element's visible text, falling back to its 'value' attribute if empty.
     * Matches are case-insensitive and may include substring matches. If a NullPointerException occurs
     * (e.g., element stale), it waits for the text to be present.
     * </p>
     *
     * @param element  The WebElement to evaluate for a text match.
     * @param expected The text string to compare against the element's content.
     * @return {@code true} if the element's text matches or contains the expected text.
     */
    private boolean checkElementTextMatch(WebElement element, String expected) {
        try {
            if (element == null)
                return false;

            String elementText = !element.getText().isEmpty() ? element.getText() : element.getAttribute("value");
            return elementText.equalsIgnoreCase(expected) || elementText.contains(expected);
        }
        catch (NullPointerException nullPointer){
            log.warning(nullPointer.getLocalizedMessage());
            return getWaitForType(getElementDriverType(element))
                    .until(ExpectedConditions.textToBePresentInElement(element, expected));
        }
    }

    /**
     * Core method: Locates a single element defined in the JSON repository.
     * <p>
     * It reads the specific {@link SelectorType}s from the JSON, constructs a {@link ByAll} locator,
     * waits for the element's presence, and then returns it.
     * </p>
     *
     * @param elementName   The unique name of the element within the page JSON.
     * @param pageName      The name of the page in the JSON.
     * @param selectorTypes Optional filter to only use specific selector types (e.g., only CSS).
     * If empty, all available selectors in the JSON are used.
     * @return The found {@link WebElement}.
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
     * Core method: Locates a list of elements defined in the JSON repository.
     *
     * @param elementName   The unique name of the element within the page JSON.
     * @param pageName      The name of the page in the JSON.
     * @param selectorTypes Optional filter for selector types.
     * @return A list of {@link WebElement}s found.
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
     * Constructs a compound {@link org.openqa.selenium.By} object based on the provided JSON representation.
     * <p>
     * This method iterates through the requested {@link SelectorType}s, retrieves the raw string
     * from the JSON via {@link #getPlatformSelector}, and creates the appropriate Selenium/Appium {@link By} locator.
     * </p>
     *
     * @param elementJson   The JSON representation of the web element.
     * @param driver        The driver instance (used to determine platform specifics).
     * @param selectorTypes One or more SelectorType enums specifying which locators to generate.
     * @return A compound {@link ByAll} object.
     * @throws EnumConstantNotPresentException If an unsupported SelectorType is provided.
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
     * Extracts the raw selector string from the JSON structure for a specific platform and selector type.
     * <p>
     * The JSON structure is expected to be:
     * <pre>
     * "selectors": {
     * "web": [ { "css": "#myId" } ],
     * "android": [ { "accessibilityId": "myId" } ]
     * }
     * </pre>
     * </p>
     *
     * @param elementJson  The JSON object for the element.
     * @param driver       The driver, used to identify the current platform (web, android, ios).
     * @param selectorType The type of selector to retrieve (e.g., css, xpath).
     * @return The selector string, or null if not found.
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
     * Generates a WebElement dynamically using a primary selector type (CSS or XPath) and a set of attribute pairs.
     * <p>
     * This is useful for finding elements that are not in the repository but have known attributes.
     * </p>
     *
     * @param selectorType   The strategy to use (CSS or XPath).
     * @param driverType     The driver to use (Selenium or Appium).
     * @param attributePairs Variable arguments of Key-Value pairs (e.g., "id", "submit").
     * @return The located WebElement.
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
     * Generates a list of WebElements dynamically using attributes.
     *
     * @param selectorType   The strategy to use (CSS or XPath).
     * @param driverType     The driver to use.
     * @param attributePairs Key-Value pairs of attributes.
     * @return A list of located WebElements.
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
     * Helper method to build a CSS selector string from attribute pairs.
     * <p>
     * Example input: {@code ("type", "submit"), ("class", "btn")}
     * <br>
     * Example output: {@code [type='submit'][class='btn']}
     * </p>
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
     * Helper method to build an XPath selector string from attribute pairs.
     * <p>
     * Example input: {@code ("type", "submit")}
     * <br>
     * Example output: {@code //*[@type='submit']}
     * </p>
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
     * Locates the specific JSON object definition for an element within a page JSON.
     *
     * @param elementName The name of the element to find.
     * @param pageJson    The JSON object representing the page.
     * @return The JSON object for the element, or null if not found.
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
     * Retrieves a JSON object representing a page from the main object repository.
     *
     * @param pageName         The name of the page to retrieve (e.g., "LoginPage").
     * @param objectRepository The main JSON object repository containing a "pages" array.
     * @return The JSON object representing the page.
     * @throws NullPointerException if the page with the given name is not found.
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
     * Helper method to get the correct driver instance (Web vs Appium) based on the "platform" key in the page JSON.
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
     * Helper method to return the active driver instance for a specific driver type.
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
     * Helper method to get the correct FluentWait instance based on the "platform" key in the page JSON.
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
     * Helper method to return the active FluentWait instance for a specific driver type.
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