package pickleib.utilities.element.acquisition.design;

import collections.Pair;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.pagefactory.ByAll;
import pickleib.driver.DriverFactory;
import pickleib.enums.PrimarySelectorType;
import pickleib.enums.SelectorType;
import pickleib.platform.driver.PickleibAppiumDriver;
import pickleib.utilities.element.ElementBundle;
import pickleib.utilities.interfaces.repository.PageRepository;
import pickleib.web.driver.PickleibWebDriver;
import utils.Printer;
import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.*;
import java.util.*;
import java.util.stream.Collectors;

import static utils.StringUtilities.Color.BLUE;
import static utils.StringUtilities.Color.GRAY;
import static utils.StringUtilities.highlighted;

public class PageObjectJson implements PageRepository {

    RemoteWebDriver webDriver;
    RemoteWebDriver platformDriver;
    JsonObject objectRepository;
    Printer log = new Printer(PageObjectJson.class);

    public PageObjectJson(RemoteWebDriver webDriver, RemoteWebDriver platformDriver, JsonObject objectRepository) {
        this.webDriver = webDriver;
        this.platformDriver = platformDriver;
        this.objectRepository = objectRepository;
    }

    public PageObjectJson(JsonObject objectRepository) {
        this.webDriver = PickleibWebDriver.get();
        this.platformDriver = PickleibAppiumDriver.get();
        this.objectRepository = objectRepository;
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
        if (element == null)
            return false;

        String elementText = !element.getText().isEmpty() ? element.getText() : element.getAttribute("value");
        return elementText.equalsIgnoreCase(expected) || elementText.contains(expected);
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
        assert elementJson != null;

        ByAll byAll = getElementByAll(elementJson, selectorTypes);
        return getDriverForPage(pageJson).findElement(byAll);
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

        assert elementJson != null;
        ByAll byAll = getElementByAll(elementJson, selectorTypes);
        return getDriverForPage(pageJson).findElements(byAll);
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
    public ByAll getElementByAll(JsonObject elementJson, SelectorType... selectorTypes){
        List<By> locators = new ArrayList<>();
        if (selectorTypes.length > 0)
            for (SelectorType selectorType:selectorTypes) {
                try {
                    By locator;
                    switch (selectorType){
                        case id ->
                                locator = By.id(elementJson.get("id").getAsJsonPrimitive().getAsString());
                        case name ->
                                locator = By.name(elementJson.get("name").getAsJsonPrimitive().getAsString());
                        case tagName ->
                                locator = By.tagName(elementJson.get("tagName").getAsJsonPrimitive().getAsString());
                        case className ->
                                locator = By.className(elementJson.get("class").getAsJsonPrimitive().getAsString());
                        case css ->
                                locator = By.cssSelector(elementJson.get("css").getAsJsonPrimitive().getAsString());
                        case xpath ->
                                locator = By.xpath(elementJson.get("xpath").getAsJsonPrimitive().getAsString());
                        case text -> {
                            String text = elementJson.get("text").getAsJsonPrimitive().getAsString();
                            locator = By.xpath("//*[text()='" + text + "']");//TODO: text locator method might vary depending on platform! Implement dynamic text locator name
                        }
                        default -> throw new EnumConstantNotPresentException(SelectorType.class, selectorType.name());

                    }
                    locators.add(locator);
                }
                catch (NullPointerException | IllegalStateException ignored){}

            }
        else return getElementByAll(
                elementJson,
                SelectorType.id,
                SelectorType.name,
                SelectorType.tagName,
                SelectorType.className,
                SelectorType.css,
                SelectorType.xpath,
                SelectorType.text
        );
        return new ByAll(locators.toArray(new By[0]));
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
            if (elementJson.getAsJsonObject().get("elementName").getAsJsonPrimitive().getAsString().equals(elementName))
                return elementJson.getAsJsonObject();

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
                                .equals(pageName)
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

}
