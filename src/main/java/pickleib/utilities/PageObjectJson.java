package pickleib.utilities;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import pickleib.driver.Driver;
import pickleib.element.ElementAcquisition;
import pickleib.models.Element;
import records.Pair;
import utils.StringUtilities;

import java.time.Duration;
import java.util.*;

public class PageObjectJson extends WebUtilities{
    ElementAcquisition.PageObjectJson acquire = new ElementAcquisition.PageObjectJson();


    public void getPageObjectJson(String url){

        Driver.initialize();
        WebDriver driver = Driver.driver;
        driver.get(url);

        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        mapper.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.CREATOR, JsonAutoDetect.Visibility.NONE);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        List<WebElement> pageElements = driver.findElements(By.cssSelector("*"));

        List<JsonObject> elements = new ArrayList<>();

        for (WebElement pageElement:pageElements)
            try {
                System.out.println(pageElement.getText());
                if (pageElement.isDisplayed()) {
                    JsonObject elementJson = generateElementJson(pageElement);
                }
            }
            catch (WebDriverException ignored){log.warning(ignored.getMessage());}

        driver.quit();

        log.success(elements.toString());
    }

    @SuppressWarnings("unchecked")
    public Optional<Element> getElement(WebElement pageElement){
        Element element = new Element();
        try {
            ElementAcquisition.PageObjectJson acquire = new ElementAcquisition.PageObjectJson();

            String elementText = pageElement.getText();
            String tagName = pageElement.getTagName();
            JsonObject attributes = getElementObject(pageElement);

            assert attributes != null;

            boolean tagNameNotParent = !tagName.equals("html") && !tagName.equals("head") && !tagName.equals("body");
            boolean hasText = !elementText.isEmpty() && !attributes.toString().isEmpty();
            boolean isSpan = tagName.equals("span");
            boolean isInput = tagName.equals("input");
            boolean isButton = tagName.equals("button");
            boolean isLabel = tagName.equals("button");
            boolean isEmpty = attributes.toString().equals("{}");

            if (tagNameNotParent && (hasText || isSpan || isInput || isButton || isLabel) && !isEmpty){
                boolean finalChild = pageElement.findElements(By.cssSelector("*")).size() == 0;

                if (finalChild || isSpan || isInput || isButton || isLabel) {
                    List<Pair<String, String>> attributePairs = new ArrayList<>();
                    for (String attribute:attributes.keySet())
                        attributePairs.add(new Pair<>(attribute, attributes.get(attribute).getAsJsonPrimitive().getAsString()));

                    String cssSelector = acquire.generateCssByAttributes(attributePairs.toArray(new Pair[0]));

                    String elementId = pageElement.getAttribute("id");
                    String className = pageElement.getAttribute("class");

                    if (elementId != null && !elementId.isEmpty()) element.setId(elementId);
                    if (hasText) element.setText(pageElement.getText());
                    if (pageElement.getAttribute("name") != null) element.setName(pageElement.getAttribute("name"));
                    if (className != null && !className.isEmpty()) element.setClassName(className);
                    element.setTagName(tagName);
                    element.setCssSelector(cssSelector);

                    String elementName;
                    if (hasText && !strUtils.normalize(strUtils.cleanText(elementText)).isEmpty()){
                        if (element.getClassName() != null)
                            elementName = strUtils.cleanText(strUtils.camelCase(strUtils.normalize(elementText + " " + pageElement.getAttribute("class"))));
                        else
                            elementName = strUtils.cleanText(strUtils.camelCase(strUtils.normalize(elementText + " " + pageElement.getAttribute("class") + " " + tagName)));
                    }
                    else
                        elementName = strUtils.cleanText(strUtils.camelCase(strUtils.normalize(pageElement.getAttribute("class") + " " + tagName)));

                    element.setElementName(strUtils.firstLetterDeCapped(elementName));

                    reflection.printObjectFields(element);
                    log.info("Selector: " + cssSelector);
                    log.info("Displayed: " + pageElement.isDisplayed());
                    log.info("ONLY: " + (driver.findElements(By.cssSelector(cssSelector)).size() == 1));
                    log.info("NUMBER: " + (driver.findElements(By.cssSelector(cssSelector)).size()));
                }
            }
        }
        catch (JsonProcessingException  ignored){}
        return Optional.of(element);
    }
    //TODO: write isNull(Object object) method verifying no field is non null
    //TODO: write a crawler class that iterates through links, has recursion upon navigating to a new page
    // (maybe event listener) and keeps record of all navigated urls, also only navigates if a link is of given
    // domain. Crawler should perform web element acquisition on each page it travels to, before keeping on crawling

    public Optional<Element> getElementt(WebElement pageElement) {
        Element element = new Element();
        try {
            ElementAcquisition.PageObjectJson acquire = new ElementAcquisition.PageObjectJson();
            String elementText = pageElement.getText();
            String tagName = pageElement.getTagName();
            JsonObject attributes = getElementObject(pageElement);
            if (attributes == null) {
                return Optional.empty();
            }

            boolean isParentTag = tagName.equals("html") || tagName.equals("head") || tagName.equals("body");
            boolean hasText = !elementText.isEmpty() && !attributes.toString().isEmpty();
            boolean isSpan = tagName.equals(ElementType.SPAN.type);
            boolean isInput = tagName.equals(ElementType.INPUT.type);
            boolean isButton = tagName.equals(ElementType.BUTTON.type);
            boolean isLabel = tagName.equals(ElementType.LABEL.type);
            boolean isEmpty = attributes.toString().equals("{}");
            if (!isParentTag && (hasText || isSpan || isInput || isButton || isLabel) && !isEmpty) {
                boolean isFinalChild = pageElement.findElements(By.cssSelector("*")).size() == 0;
                if (isFinalChild || isSpan || isInput || isButton || isLabel) {
                    List<Pair<String, String>> attributePairs = getAttributePairs(attributes);
                    String cssSelector = getCssSelector(attributePairs);
                    String elementId = pageElement.getAttribute("id");
                    String className = pageElement.getAttribute("class");
                    if (elementId != null && !elementId.isEmpty()) {
                        element.setId(elementId);
                    }
                    if (hasText) {
                        element.setText(elementText);
                    }
                    if (pageElement.getAttribute("name") != null) {
                        element.setName(pageElement.getAttribute("name"));
                    }
                    if (className != null && !className.isEmpty()) {
                        element.setClassName(className);
                    }
                    element.setTagName(tagName);
                    element.setCssSelector(cssSelector);
                    String elementName = getElementName(pageElement, elementText, className, tagName, hasText);
                    element.setElementName(strUtils.firstLetterDeCapped(elementName));
                    reflection.printObjectFields(element);
                    log.info("Selector: " + cssSelector);
                    log.info("Displayed: " + pageElement.isDisplayed());
                    log.info("ONLY: " + (driver.findElements(By.cssSelector(cssSelector)).size() == 1));
                    log.info("NUMBER: " + (driver.findElements(By.cssSelector(cssSelector)).size()));
                }
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON: " + e.getMessage(), e);
            return Optional.empty();
        }
        return Optional.of(element);
    }

    private List<Pair<String, String>> getAttributePairs(JsonObject attributes) {
        List<Pair<String, String>> attributePairs = new ArrayList<>();
        for (String attribute : attributes.keySet()) {
            attributePairs.add(new Pair<>(attribute, attributes.get(attribute).getAsJsonPrimitive().getAsString()));
        }
        return attributePairs;
    }

    @SuppressWarnings("unchecked")
    private String getCssSelector(List<Pair<String, String>> attributePairs) {
        ElementAcquisition.PageObjectJson acquire = new ElementAcquisition.PageObjectJson();
        return acquire.generateCssByAttributes(attributePairs.toArray(new Pair[0]));
    }
    @SuppressWarnings("unchecked")
    private String getXPath(List<Pair<String, String>> attributePairs) {
        ElementAcquisition.PageObjectJson acquire = new ElementAcquisition.PageObjectJson();
        return acquire.generateXPathByAttributes(attributePairs.toArray(new Pair[0]));
    }

    /**
     * Generate a xPath for a given element
     *
     * @param childElement web element gets generated a xPath from
     * @return returns generated xPath
     */
    protected String getXPath(@NotNull WebElement childElement) {
        String childTag = childElement.getTagName();
        if (childTag.equals("html")) {return "/html[1]";}
        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
        int count = 0;
        for (WebElement childrenElement : childrenElements) {
            String childrenElementTag = childrenElement.getTagName();
            if (childTag.equals(childrenElementTag)) count++;
            if (childElement.equals(childrenElement)) {
                return getXPath(parentElement, "/" + childTag + "[" + count + "]");
            }
        }
        return null;
    }

    /**
     * Generate a xPath for a given element
     *
     * @param childElement web element gets generated a xPath from
     * @param current empty string (at the beginning)
     * @return returns generated xPath
     */
    protected String getXPath(@NotNull WebElement childElement, String current) {
        String childTag = childElement.getTagName();
        if (childElement.getTagName().equals("html")) {return "/html[1]" + current;}
        WebElement parentElement = childElement.findElement(By.xpath(".."));
        List<WebElement> childrenElements = parentElement.findElements(By.xpath("*"));
        int count = 0;
        for (WebElement childrenElement : childrenElements) {
            String childrenElementTag = childrenElement.getTagName();
            if (childTag.equals(childrenElementTag)) count++;
            if (childElement.equals(childrenElement)) {
                return getXPath(parentElement, "/" + childTag + "[" + count + "]" + current);
            }
        }
        return null;
    }

    private String getElementName(WebElement pageElement, String elementText, String className, String tagName, boolean hasText) {
        if (hasText && !strUtils.normalize(strUtils.cleanText(elementText)).isEmpty()) {
            if (className != null) {
                return strUtils.cleanText(strUtils.camelCase(strUtils.normalize(elementText + " " + className)));
            } else {
                return strUtils.cleanText(strUtils.camelCase(strUtils.normalize(elementText + " " + tagName)));
            }
        } else {
            return strUtils.cleanText(strUtils.camelCase(strUtils.normalize(className + " " + tagName)));
        }
    }

    @SafeVarargs
    public final String generateXPathByAttributes(Pair<String, String>... attributePairs){
        StringBuilder selector = new StringBuilder();
        for (Pair<String, String> attributePair:attributePairs) {
            StringJoiner cssFormat = new StringJoiner(
                    "",
                    "//*[@",
                    "']"
            );
            selector.append(cssFormat.add(attributePair.alpha() + " = '" + attributePair.beta()));
        }
        return selector.toString();
    }

    public enum ElementType {
        SPAN("span"),
        INPUT("input"),
        BUTTON("button"),
        LABEL("label");

        private final String type;

        ElementType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return type;
        }
    }

    public JsonObject generateElementJson(WebElement element) {
        JsonObject jsonObject = new JsonObject();

        try {
            JsonObject attributes = getElementObject(element);
            List<Pair<String, String>> attributePairs = getAttributePairs(attributes);

            String cssSelector = getCssSelector(attributePairs);
            String xpath = getXPath(attributePairs);

            // Get element attributes
            for (String attributeName: attributes.keySet()) {
                jsonObject.add(attributeName, attributes.get(attributeName));
            }
            String text = element.getText();
            String name = element.getAttribute("name");
            String id = element.getAttribute("id");
            String type = element.getAttribute("type");
            String tagName = element.getTagName();
            String className = element.getAttribute("class");
            String parentTagName;
            if (!element.getTagName().equals("html"))
                parentTagName = element.findElement(By.xpath("..")).getTagName();
            else
                parentTagName = null;

            // Create JSON object
            if (!isBlank(xpath)) jsonObject.addProperty("xpath", xpath);
            if (!isBlank(text)) jsonObject.addProperty("text", text);
            if (!isBlank(name)) jsonObject.addProperty("name", name);
            if (!isBlank(id)) jsonObject.addProperty("id", id);
            if (!isBlank(type)) jsonObject.addProperty("type", type);
            if (!isBlank(tagName)) jsonObject.addProperty("tagName", tagName);
            if (!isBlank(className)) jsonObject.addProperty("className", className);
            if (!isBlank(parentTagName)) jsonObject.addProperty("parentTagName", parentTagName);
            if (!isBlank(cssSelector)) jsonObject.addProperty("cssSelector", cssSelector);
            //if (getXPath(element) != null && !getXPath(element).trim().isEmpty()) jsonObject.addProperty("absoluteXPath", getXPath(element));
            if (!isBlank(generateElementName(jsonObject))) jsonObject.addProperty("elementName", generateElementName(jsonObject));
        }
        catch (JsonProcessingException e) {throw new RuntimeException(e);}
        return jsonObject;
    }

    public static String generateElementName(JsonObject elementJson) {
        StringBuilder elementNameBuilder = new StringBuilder();
        StringUtilities strUtils = new StringUtilities();
        // Check for ID attribute
        if (!isBlank(elementJson.get("id"))) {
            elementNameBuilder.append(strUtils.camelCase(elementJson.get("id").getAsString())).append("_");
        }
        // Check for Name attribute
        if (!isBlank(elementJson.get("name"))) {
            elementNameBuilder.append(strUtils.camelCase(elementJson.get("name").getAsString())).append("_");
        }
        // Check for Text attribute
        if (!isBlank(elementJson.get("text"))) {
            elementNameBuilder.append(strUtils.camelCase(elementJson.get("text").getAsString())).append("_");
        }
        // Check for Class attribute
        if (!isBlank(elementJson.get("className"))) {
            elementNameBuilder.append(strUtils.camelCase(elementJson.get("className").getAsString())).append("_");
        }
        // Check for Tag Name attribute
        if (!isBlank(elementJson.get("tagName"))) {
            elementNameBuilder.append(strUtils.camelCase(elementJson.get("tagName").getAsString())).append("_");
        }
        // Check for Type attribute
        if (!isBlank(elementJson.get("type"))) {
            elementNameBuilder.append(strUtils.camelCase(elementJson.get("type").getAsString())).append("_");
        }
        // If no attributes found, use "element" as default
        if (elementNameBuilder.length() == 0) {
            elementNameBuilder.append("element_");
        }
        // Remove trailing underscore
        elementNameBuilder.setLength(elementNameBuilder.length() - 1);
        // Combine up to three attributes based on priority
        String[] attributes = elementNameBuilder.toString().split("_");
        StringBuilder combinedAttributesBuilder = new StringBuilder();
        for (int i = 0; i < attributes.length && i < 3; i++) {
            if (attributes[i].length() > 0)
                combinedAttributesBuilder.append(strUtils.firstLetterCapped(attributes[i]));
        }
        return formatName(combinedAttributesBuilder.toString());
    }

    public static String formatName(String name){
        StringUtilities strUtils = new StringUtilities();
        name = name.replaceAll("[^\\p{ASCII}]", "_");
        name = strUtils.firstLetterDeCapped(name);
        return strUtils.camelCase(name);
    }

    public static void main(String[] args) {
        Driver.initialize();
        WebDriver driver = Driver.driver;
        driver.get("https://demoqa.com");
        PageObjectJson pageObjectJson = new PageObjectJson();

        Document doc = Jsoup.parse(driver.getPageSource());

        List<String> tagNames = new ArrayList<>();
        List<String> texts = new ArrayList<>();
        for (org.jsoup.nodes.Element element:doc.children()) {
            tagNames.add(element.tagName());
            texts.add(element.lastElementChild().text() + "\n\n");
        }

        System.out.println("DOC: " + doc);

        //WebElement body = driver.findElement(By.cssSelector("body"));
//
        //JsonArray jsonArray = pageObjectJson.elementCrawler(body.findElements(By.cssSelector("*")));
        //JsonObject jsonObject = new JsonObject();
        //jsonObject.add("elements", jsonArray);
//
        //pageObjectJson.log.success(jsonObject.toString());
        //pageObjectJson.getPageObjectJson("https://www.citizenm.com/hotels/europe/amsterdam");
        driver.quit();
    }

    //TODO: write iterative json generator that loops through child elements for each element of a given element list
    //TODO: Write a json parser that will return an element json with a given unique name

    public JsonArray elementCrawler(List<WebElement> elements){
        JsonArray jsonArray = new JsonArray();
        for (WebElement element:elements) {
            try {
                boolean finalChild = element.findElements(By.cssSelector("*")).size() == 0;
                if (finalChild && !element.getTagName().equals("html") && !element.getTagName().equals("body")) {
                    JsonObject elementJson = generateElementJson(element);
                    String elementAlias = elementJson.get("elementName").getAsJsonPrimitive().getAsString();
                    boolean duplicateElementName = jsonArray.asList()
                            .stream()
                            .anyMatch(jsonElement ->
                                            jsonElement
                                                    .getAsJsonObject().get("elementName")
                                                    .getAsJsonPrimitive()
                                                    .getAsString()
                                                    .equals(elementAlias)
                            );
                    if (duplicateElementName)
                        elementJson.add("elementName", new JsonPrimitive(
                                strUtils.generateRandomString(
                                        elementAlias + "#",
                                        4,
                                        false,
                                        true)
                                )
                        );

                    jsonArray.add(elementJson);
                    log.info(jsonArray.toString());
                }
            }
            catch (WebDriverException webDriverException){
                log.warning(webDriverException.getMessage());
            }
        }
        return jsonArray;
    }

    public static <T> boolean  isBlank(T input){
        return !(input != null && !input.toString().trim().isEmpty());
    }
}
