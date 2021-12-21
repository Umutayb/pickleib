package utils.rehoboam.snippets;

public class ElementSnippet {
    String snippet =
            "     @FindBy(%locatorType% = \"%locator%\")\n" +
            "     public WebElement %elementName%;\n";

    public ElementSnippet(String locatorType, String locator, String elementName){
        snippet = snippet
                .replace("%locatorType%",locatorType)
                .replace("%locator%",locator)
                .replace("%elementName%",elementName);
    }

    public String getSnippet(){return snippet;}
}
