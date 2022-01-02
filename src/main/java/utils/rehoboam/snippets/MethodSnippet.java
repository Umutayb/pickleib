package utils.rehoboam.snippets;

import utils.rehoboam.snippets.methods.ClickMethod;

public class MethodSnippet {
    String snippet;

    public MethodSnippet(String elementName){
        snippet = new ClickMethod(elementName).getClickSnippet();
    }

    public String getSnippet(){return snippet;}
}
