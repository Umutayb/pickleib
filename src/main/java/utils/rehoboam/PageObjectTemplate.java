package utils.rehoboam;

import utils.FileUtilities;
import utils.rehoboam.snippets.ClassSnippet;
import utils.rehoboam.snippets.ElementSnippet;
import utils.rehoboam.snippets.MethodSnippet;
import utils.rehoboam.snippets.ObjectSnippet;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PageObjectTemplate {

    String webElementTemplate =
            "     @FindBy(css = \"[id='permanentAddress']\")\n" +
            "     public WebElement %elementName%;\n\n";

    String clickMethodTemplate =
            "     public void click%elementName%(){\n" +
            "          log.new info(\"Clicking %elementName% button\");\n" +
            "          clickElement(%elementName%Button);\n" +
            "     }\n\n";

    String objectInstanceTemplate =
            "     %objectName% %objectName%Instance = new %objectName%(%className%.class);\n\n";

    String classTemplate = "" +
            " public class %className% extends Utilities {\n" +
            " \n" +
            "//%Objects%" +
            "//%Elements%" +
            "//%Methods%" +
            " }";

    public static void main(String[] args) throws IOException {
        PageObjectTemplate strUtils = new PageObjectTemplate();
        List<ElementSnippet> elementSnippet = Collections.singletonList(new ElementSnippet(
                "css",
                "[id='permanentAddress']",
                "addressButton"));
        List<MethodSnippet> methodSnippet = Collections.singletonList(new MethodSnippet(
                "addressButton"));
        List<ObjectSnippet> objectSnippet = Collections.singletonList(new ObjectSnippet(
                "Printer",
                "TextBox"));
        ClassSnippet classSnippet = new ClassSnippet("TextBox",objectSnippet,elementSnippet,methodSnippet);

        System.out.println(classSnippet.getSnippet());
        FileUtilities fileUtil = new FileUtilities();
        fileUtil.classWriter(classSnippet.getSnippet(),classSnippet.getClassName());
    }

    public String dev(){

        System.out.println(
                webElementTemplate+
                        clickMethodTemplate.replaceAll("%elementName%","Submit"));

        String template = "package pages.components;\n " +
                "\n " +
                "import org.openqa.selenium.WebElement;\n " +
                "import org.openqa.selenium.support.FindBy;\n " +
                "import utils.Printer;\n " +
                "import utils.Utilities;\n " +
                "\n " +
                "import static resources.Colors.*;\n " +
                "\n " +
                "public class TextBox extends Utilities {\n " +
                "\n " +
                "    Printer log = new Printer(TextBox.class);\n " +
                "\n " +
                "    @FindBy(css = \"button[id='submit']\")\n " +
                "    public WebElement submitButton;\n " +
                "\n " +
                "    @FindBy(css = \"[id='userName']\")\n " +
                "    public WebElement nameInput;\n " +
                "\n " +
                "    @FindBy(css = \"[id='userEmail']\")\n " +
                "    public WebElement emailInput;\n " +
                "\n " +
                "    @FindBy(css = \"[id='currentAddress']\")\n " +
                "    public WebElement currentAddressInput;\n " +
                "\n " +
                "    @FindBy(css = \"[id='permanentAddress']\")\n " +
                "    public WebElement permanentAddressInput;\n " +
                "\n " +
                "    public void clickSubmit(){\n " +
                "        log.new info(\"Clicking submit button\");\n " +
                "        clickElement(submitButton);\n " +
                "    }\n " +
                "\n " +
                "    public void fillNameInput(String text){\n " +
                "        log.new info(\"Filling name input with \"+BLUE+text);\n " +
                "        clearFillInput(nameInput, text, true);\n " +
                "    }\n " +
                "\n " +
                "    public void fillEmailInput(String text){\n " +
                "        log.new info(\"Filling email input with \"+BLUE+text);\n " +
                "        clearFillInput(emailInput, text, true);\n " +
                "    }\n " +
                "\n " +
                "    public void fillCurrentAddressInput(String text){\n " +
                "        log.new info(\"Filling current address input with \"+BLUE+text);\n " +
                "        clearFillInput(currentAddressInput, text, true);\n " +
                "    }\n " +
                "\n " +
                "    public void fillPermanentAddressInput(String text){\n " +
                "        log.new info(\"Filling permanent address input with \"+BLUE+text);\n " +
                "        clearFillInput(permanentAddressInput, text, false);\n " +
                "    }\n " +
                "\n " +
                "}";
        System.out.printf((template) + "%n","false");
        return template;
    }
}
