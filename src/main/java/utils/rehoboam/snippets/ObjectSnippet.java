package utils.rehoboam.snippets;

public class ObjectSnippet {
    String snippet = "     %objectName% %objectName%Instance = new %objectName%(%className%.class);\n";

    public ObjectSnippet(String objectName, String className){
        snippet = snippet
                .replace("%objectName%",objectName)
                .replace("%className%",className);
    }

    public String getSnippet(){return snippet;}
}
