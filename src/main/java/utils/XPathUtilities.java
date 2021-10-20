package utils;

import org.junit.Assert;
import static resources.Colors.*;

//This is where the xPaths are being generated
public class XPathUtilities {
    public static String acquireXpath(String locatorType, String locatorID, String specificClass, String secondaryLocatorType, String secondaryLocatorID){

        String xPath = pathTemplateSwitch(locatorType, locatorID, false);

        if(secondaryLocatorID != null && secondaryLocatorType != null){
            String path = pathTemplateSwitch(secondaryLocatorType, secondaryLocatorID, true);
            xPath = xPath + path;
        }

        if(specificClass != null && !specificClass.equals("")){
            String classPath = "//*[contains(@class, '" +specificClass+ "')]";
            xPath = classPath + xPath;
        }

        //System.out.println(GRAY+"The xpath is acquired as: "+xPath+RESET); //Print for debugging, comment in if needed
        return xPath;
    }

    static String pathTemplateSwitch(String locatorType, String locatorID, Boolean strict){

        String xPath;

        if (!strict){
            switch (locatorType.toLowerCase()){
                case "xpath":

                case "full xpath":

                case "css":
                    xPath = locatorID;
                    break;

                case "text":
                    xPath = "//*[contains(text(), '" +locatorID+ "')]";
                    break;

                case "name":
                    xPath = "//*[contains(@name, '" +locatorID+ "')]";
                    break;

                case "title":
                    xPath = "//*[contains(@title, '" +locatorID+ "')]";
                    break;

                case "type":
                    xPath = "//*[contains(@type, '" +locatorID+ "')]";
                    break;

                case "id":
                    xPath = "//*[contains(@id, '" +locatorID+ "')]";
                    break;

                case "alt":
                    xPath = "//*[contains(@alt, '" +locatorID+ "')]";
                    break;

                case "class":
                    xPath = "//*[contains(@class, '" +locatorID+ "')]";
                    break;

                case "placeholder":
                    xPath = "//*[contains(@placeholder, '" +locatorID+ "')]";
                    break;

                case "src":
                    xPath = "//*[@src='" +locatorID+ "']";
                    break;

                case "href":
                    xPath = "//*[contains(@href, '" +locatorID+ "')]";
                    break;

                case "value":
                    xPath = "//*[contains(@value, '" +locatorID+ "')]";
                    break;

                case "data testid":
                    xPath = "//*[contains(@data-testid, '" +locatorID+ "')]";
                    break;

                case "kind":
                    xPath = "//*[contains(@kind, '" +locatorID+ "')]";
                    break;

                case "div class":
                    xPath = "//div[contains(@class, '" +locatorID+ "')]";
                    break;

                default:
                    Assert.fail(YELLOW+ "The locator type '" +locatorType+ "' was undefined, please add it to the" +
                            " framework along with its appropriate xpath characteristics" +RESET+RED+"\nTest Failed."+RESET);
                    return null;
            }
        }
        else {
            switch (locatorType.toLowerCase()){
                case "xpath":

                case "full xpath":
                    xPath = locatorID;
                    break;

                case "text":
                    xPath = "//*[text()='" +locatorID+ "']";
                    break;

                case "name":
                    xPath = "//*[@name= '" +locatorID+ "']";
                    break;

                case "title":
                    xPath = "//*[@title= '" +locatorID+ "']";
                    break;

                case "type":
                    xPath = "//*[@type= '" +locatorID+ "']";
                    break;

                case "id":
                    xPath = "//*[@id= '" +locatorID+ "']";
                    break;

                case "alt":
                    xPath = "//*[@alt= '" +locatorID+ "']";
                    break;

                case "class":
                    xPath = "//*[@class= '" +locatorID+ "']";
                    break;

                case "placeholder":
                    xPath = "//*[@placeholder= '" +locatorID+ "']";
                    break;

                case "src":
                    xPath = "//*[@src='" +locatorID+ "']";
                    break;

                case "href":
                    xPath = "//*[@href= '" +locatorID+ "']";
                    break;

                case "value":
                    xPath = "//*[@value= '" +locatorID+ "']";
                    break;

                case "data testid":
                    xPath = "//*[@data-testid= '" +locatorID+ "']";
                    break;

                case "kind":
                    xPath = "//*[@kind= '" +locatorID+ "']";
                    break;

                case "div class":
                    xPath = "//div[@class= '" +locatorID+ "']";
                    break;

                case "css":
                    return locatorID;

                default:
                    Assert.fail(YELLOW+ "The locator type '" +locatorType+ "' was undefined, please add it to the" +
                            " framework along with its appropriate xpath characteristics" +RESET+RED+"\nTest Failed."+RESET);
                    return null;
            }
        }

        return xPath;
    }

}