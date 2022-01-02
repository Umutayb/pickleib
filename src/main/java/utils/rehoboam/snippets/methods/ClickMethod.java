package utils.rehoboam.snippets.methods;

import utils.StringUtilities;

public class ClickMethod extends StringUtilities {
    String clickSnippet =
            "     public void click%elementName%(){\n" +
                    "          PrinterInstance.new info(\"Clicking %elementName% button\");\n" +
                    "          clickElement(%elementName%);\n" +
                    "     }\n";

    public ClickMethod(String elementName){
        clickSnippet = clickSnippet
                .replace("%elementName%",elementName)
                .replaceFirst(elementName,firstLetterCapped(elementName));
    }

    public String getClickSnippet(){return clickSnippet;}
}
