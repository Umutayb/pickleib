package pickleib.enums;

import lombok.Getter;
import java.util.Map;

@Getter
public enum EmulatedDevice {

    iPadAir("iPad Air"),
    iPhone4("iPhone 4"),
    iPhone5SE("iPhone 5/SE"),
    iPhone6("iPhone 6"),
    iPhone7("iPhone 7"),
    iPhone8("iPhone 8"),
    iPhone6Plus("iPhone 6 Plus"),
    iPhone7Plus("iPhone 7 Plus"),
    iPhone8Plus("iPhone 8 Plus"),
    iPhoneX("iPhone X"),
    iPhone12Pro("iPhone 12 Pro"),
    Nexus4("Nexus 4"),
    Nexus5("Nexus 5"),
    Nexus5X("Nexus 5X"),
    Nexus6("Nexus 6"),
    Nexus6P("Nexus 6P"),
    Nexus7("Nexus 7"),
    Pixel2("Pixel 2"),
    Pixel2XL("Pixel 2 XL"),
    MicrosoftLumia550("Microsoft Lumia 550"),
    MicrosoftLumia950("Microsoft Lumia 950"),
    GalaxySIII("Galaxy S III"),
    GalaxyS5("Galaxy S5"),
    KindleFireHDX("Kindle Fire HDX"),
    iPadMini("iPad Mini"),
    iPad("iPad"),
    iPadPro("iPad Pro"),
    Nexus10("Nexus 10"),
    GalaxyNote3("Galaxy Note 3"),
    GalaxyNoteII("Galaxy Note II"),
    AppleiPhone4("Apple iPhone 4"),
    AppleiPhone5("Apple iPhone 5"),
    AppleiPhone6("Apple iPhone 6"),
    BlackBerryZ30("BlackBerry Z30"),
    GoogleNexus4("Google Nexus 4"),
    GoogleNexus5("Google Nexus 5"),
    GoogleNexus6("Google Nexus 6"),
    GoogleNexus7("Google Nexus 7"),
    GoogleNexus10("Google Nexus 10"),
    LGOptimusL70("LG Optimus L70"),
    NokiaN9("Nokia N9"),
    NokiaLumia520("Nokia Lumia 520"),
    SamsungGalaxySIII("Samsung Galaxy S III"),
    SamsungGalaxyS4("Samsung Galaxy S4"),
    SamsungGalaxyS20Ultra("Samsung Galaxy S20 Ultra"),
    AmazonKindleFireHDX("Amazon Kindle Fire HDX"),
    AppleiPadMini("Apple iPad Mini"),
    AppleiPad("Apple iPad"),
    BlackBerryPlayBook("BlackBerry PlayBook"),
    SamsungGalaxyNote3("Samsung Galaxy Note 3"),
    SamsungGalaxyNoteII("Samsung Galaxy Note II"),
    Laptopwithtouch("Laptop with touch"),
    LaptopwithHiDPIscreen("Laptop with HiDPI screen"),
    LaptopwithMDPIscreen("Laptop with MDPI screen");

    final String deviceName;

    EmulatedDevice(String deviceName){
        this.deviceName = deviceName;
    };

    public Map<String, String> emulate(){
        return Map.of("deviceName", this.deviceName);
    }

    public static EmulatedDevice getType(String text) {
        if (text != null)
            for (EmulatedDevice driverType:values())
                if (driverType.name().equalsIgnoreCase(text))
                    return driverType;
        return null;
    }
}
