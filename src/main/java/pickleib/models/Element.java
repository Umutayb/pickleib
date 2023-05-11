package pickleib.models;

import lombok.Data;

@Data
public class Element {
    String elementName;
    String xpath;
    String text;
    String name;
    String id;
    String type;
    String tagName;
    String className;
    String parentTagName;
    String cssSelector;
    String absoluteXPath;
}
