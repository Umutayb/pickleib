package pickleib.utilities;

import com.github.webdriverextensions.WebComponent;
import org.openqa.selenium.WebElement;
import utils.StringUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static utils.StringUtilities.Color.BLUE;
import static utils.StringUtilities.Color.GRAY;

public class ElementAcquisition {

    public static class PageObjectModel extends WebUtilities {


        public StringUtilities strUtils = new StringUtilities();

        /**
         *
         * Acquire element {element name} from {page name}
         *
         * @param elementName target button name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement elementFromPage(String elementName, String pageName, Object objectRepository){
            log.new Info("Acquiring element " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," from the ") +
                    highlighted(BLUE, pageName)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            return getElementFromPage(elementName, pageName, objectRepository);
        }

        /**
         *
         * Acquire component element {element name} of {component field name} component on the {page name}
         *
         * @param elementName target button name
         * @param componentFieldName specified component field name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement elementFromComponent(String elementName, String componentFieldName, String pageName, Object objectRepository) {
            log.new Info("Acquiring element " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," from the ") +
                    highlighted(BLUE, pageName)
            );
            return getElementFromComponent(elementName, componentFieldName, pageName, objectRepository);
        }

        /**
         *
         * Acquire listed component element {element name} of {component field name} from {component list name} list on the {page name}
         *
         * @param elementName target button name
         * @param componentFieldName specified component field name
         * @param componentListName specified component list name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement listedElementFromComponent(
                String elementName,
                String componentFieldName,
                String componentListName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring listed element named " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, componentListName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, componentFieldName) +
                    highlighted(GRAY," component on the ") +
                    highlighted(BLUE, pageName)
            );

            componentFieldName = strUtils.firstLetterDeCapped(componentFieldName);
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentListName = strUtils.firstLetterDeCapped(componentListName);
            elementName = strUtils.contextCheck(elementName);
            List<WebElement> elements = getElementsFromComponent(
                    componentListName,
                    componentFieldName,
                    pageName,
                    objectRepository
            );
            return acquireNamedElementAmongst(elements, elementName);
        }

        /**
         *
         * Select component named {component name} from {component list name} component list on the {page name} and acquire the {element name} element
         *
         * @param componentName specified component name
         * @param componentListName specified component list name
         * @param pageName specified page instance name
         * @param elementName target button name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement listedComponentElement(
                String componentName,
                String componentListName,
                String pageName,
                String elementName,
                Object objectRepository
        ) {
            log.new Info("Acquiring listed element named " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, componentListName) +
                    highlighted(GRAY," on the ") +
                    highlighted(BLUE, componentName) +
                    highlighted(GRAY," component on the ") +
                    highlighted(BLUE, pageName)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentListName = strUtils.firstLetterDeCapped(componentListName);
            List<WebComponent> componentList = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireNamedComponentAmongst(componentList, componentName);
            return getElementFromComponent(elementName, component);
        }


        /**
         *
         * Select exact component named {component name} from {component list name} component list on the {page name} and acquire the {element name} element
         *
         * @param componentName specified component name
         * @param componentListName specified component list name
         * @param pageName specified page instance name
         * @param elementName target element name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement exactNamedListedComponentElement(
                String componentName,
                String componentListName,
                String pageName,
                String elementName,
                Object objectRepository
        ) {
            log.new Info("Acquiring exact listed element named " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, componentListName) +
                    highlighted(GRAY," component list on the ") +
                    highlighted(BLUE, pageName)
            );
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentListName = strUtils.firstLetterDeCapped(componentListName);
            List<WebComponent> components = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireExactNamedComponentAmongst(components, componentName, elementName);
            return getElementFromComponent(elementName, component);
        }

        /**
         *
         * Select component named {component name} from {component list name} component list on the {page name} and acquire listed element {element name} of {element list name}
         *
         * @param componentName specified component name
         * @param componentListName specified component list name
         * @param pageName specified page instance name
         * @param elementName target button name
         * @param elementListName target element list name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement listedElementAmongstListedComponents(
                String componentName,
                String componentListName,
                String pageName,
                String elementName,
                String elementListName,
                Object objectRepository
        ) {
            log.new Info("Acquiring listed element named " +
                    highlighted(BLUE, elementName) +
                    highlighted(GRAY," selected from ") +
                    highlighted(BLUE, componentListName) +
                    highlighted(GRAY," component list on the ") +
                    highlighted(BLUE, pageName)
            );
            elementName = strUtils.contextCheck(elementName);
            componentName = strUtils.contextCheck(componentName);
            pageName = strUtils.firstLetterDeCapped(pageName);
            componentListName = strUtils.firstLetterDeCapped(componentListName);
            List<WebComponent> components = getComponentsFromPage(componentListName, pageName, objectRepository);
            WebComponent component = acquireNamedComponentAmongst(components, componentName);
            List<WebElement> elements = getElementsFromComponent(elementListName, component);
            return acquireNamedElementAmongst(elements, elementName);
        }

        /**
         *
         * Acquire listed attribute element that has {attribute value} value for its {attribute name} attribute from {list name} list on the {page name}
         *
         * @param attributeName target attribute name
         * @param attributeValue expected attribute value
         * @param listName target list name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement listedElementByAttribute(
                String attributeName,
                String attributeValue,
                String listName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring element by " +
                    highlighted(BLUE, attributeName) +
                    highlighted(GRAY," attribute selected from ") +
                    highlighted(BLUE, listName) +
                    highlighted(GRAY, " list on the ") +
                    highlighted(BLUE, pageName)
            );
            List<WebElement> elements = getElementsFromPage(
                    listName,
                    strUtils.firstLetterDeCapped(pageName),
                    objectRepository
            );
            return acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue);
        }

        /**
         *
         * Acquire listed attribute element of {component name} component that has {attribute value} value for its {attribute name} attribute from {list name} list on the {page name}
         *
         * @param componentName specified component name
         * @param attributeValue expected attribute value
         * @param attributeName target attribute name
         * @param listName target list name
         * @param pageName specified page instance name
         * @param objectRepository instance that includes specified page instance
         */
        public WebElement listedElementByAttribute(
                String componentName,
                String attributeValue,
                String attributeName,
                String listName,
                String pageName,
                Object objectRepository
        ) {
            log.new Info("Acquiring element by " +
                    highlighted(BLUE, attributeName) +
                    highlighted(GRAY," attribute selected from ") +
                    highlighted(BLUE, listName) +
                    highlighted(GRAY, " list on the ") +
                    highlighted(BLUE, pageName)
            );
            List<WebElement> elements = getElementsFromComponent(
                    listName,
                    strUtils.firstLetterDeCapped(componentName),
                    strUtils.firstLetterDeCapped(pageName),
                    objectRepository
            );
            return acquireElementUsingAttributeAmongst(elements, attributeName, attributeValue);
        }

        /**
         * Acquire component form input on the {page name}
         *
         * @param signForms        Input table
         * @param pageName         specified page instance name
         * @param signForms        table that has key as "Input" and value as "Input Element" (dataTable.asMaps())
         * @param objectRepository instance that includes specified page instance
         */

        public List<Bundle<WebElement, String, String>> elementList(List<Map<String, String>> signForms, String pageName, Object objectRepository) {
            List<Bundle<WebElement, String, String>> bundles = new ArrayList<>();
            for (Map<String, String> form : signForms) {
                String inputName = form.get("Input Element");
                String input = strUtils.contextCheck(form.get("Input"));
                log.new Info("Filling " +
                        highlighted(BLUE, inputName) +
                        highlighted(GRAY, " on the ") +
                        highlighted(BLUE, pageName) +
                        highlighted(GRAY, " with the text: ") +
                        highlighted(BLUE, input)
                );
                Bundle<WebElement, String, String> bundle = new Bundle<>(
                        getElementFromPage(inputName, pageName, objectRepository),
                        input,
                        inputName
                );
                bundles.add(bundle);
            }
            return bundles;
        }

    }
}
