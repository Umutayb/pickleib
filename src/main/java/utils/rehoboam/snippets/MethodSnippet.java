package utils.rehoboam.snippets;

public class MethodSnippet {
    String snippet =
            "     public void click%elementName%(){\n" +
            "          log.new info(\"Clicking %elementName% button\");\n" +
            "          clickElement(%elementName%Button);\n" +
            "     }\n";

    public MethodSnippet(String elementName){
        snippet = snippet.replace("%elementName%",elementName);
    }

    public String getSnippet(){return snippet;}
}
