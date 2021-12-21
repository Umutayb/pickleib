package utils.rehoboam.snippets;

import java.util.List;

public class ClassSnippet {
    String snippet =
            "public class %className% extends Utilities {\n" +
            "//%Objects%" +
            "//%Elements%" +
            "//%Methods%" +
            "}";

    String className;

    public ClassSnippet(String className, List<ObjectSnippet> objects, List<ElementSnippet>  elements, List<MethodSnippet>  methods){
        StringBuilder objectSnippets = new StringBuilder();
        StringBuilder elementSnippets = new StringBuilder();
        StringBuilder methodSnippets = new StringBuilder();

        for (ObjectSnippet object:objects) {objectSnippets.append("\n").append(object.snippet);}
        for (ElementSnippet object:elements) {elementSnippets.append("\n").append(object.snippet);}
        for (MethodSnippet object:methods) {methodSnippets.append("\n").append(object.snippet);}

        snippet = snippet
                .replace("//%Objects%",objectSnippets)
                .replace("//%Elements%",elementSnippets)
                .replace("//%Methods%",methodSnippets)
                .replace("%className%",className);
        this.className = className;
    }

    public String getSnippet(){return snippet;}

    public String getClassName(){return className;}
}
