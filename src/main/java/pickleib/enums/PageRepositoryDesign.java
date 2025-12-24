package pickleib.enums;

public enum PageRepositoryDesign {
    pom,
    json;

    public static PageRepositoryDesign getDesign(String text) {
        if (text != null)
            for (PageRepositoryDesign design:values())
                if (design.name().equalsIgnoreCase(text))
                    return design;
        return null;
    }
}
