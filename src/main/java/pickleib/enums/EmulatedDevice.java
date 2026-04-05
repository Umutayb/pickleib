package pickleib.enums;

import lombok.Getter;
import java.util.Map;

/** Chrome DevTools emulated device profiles for mobile testing. */
@Getter
public enum EmulatedDevice {

    /** iPad Air device profile. */
    iPadAir("iPad Air"),
    /** iPhone 4 device profile. */
    iPhone4("iPhone 4"),
    /** iPhone 5/SE device profile. */
    iPhone5SE("iPhone 5/SE"),
    /** iPhone 6 device profile. */
    iPhone6("iPhone 6"),
    /** iPhone 7 device profile. */
    iPhone7("iPhone 7"),
    /** iPhone 8 device profile. */
    iPhone8("iPhone 8"),
    /** iPhone 6 Plus device profile. */
    iPhone6Plus("iPhone 6 Plus"),
    /** iPhone 7 Plus device profile. */
    iPhone7Plus("iPhone 7 Plus"),
    /** iPhone 8 Plus device profile. */
    iPhone8Plus("iPhone 8 Plus"),
    /** iPhone X device profile. */
    iPhoneX("iPhone X"),
    /** iPhone 12 Pro device profile. */
    iPhone12Pro("iPhone 12 Pro"),
    /** Nexus 4 device profile. */
    Nexus4("Nexus 4"),
    /** Nexus 5 device profile. */
    Nexus5("Nexus 5"),
    /** Nexus 5X device profile. */
    Nexus5X("Nexus 5X"),
    /** Nexus 6 device profile. */
    Nexus6("Nexus 6"),
    /** Nexus 6P device profile. */
    Nexus6P("Nexus 6P"),
    /** Nexus 7 device profile. */
    Nexus7("Nexus 7"),
    /** Pixel 2 device profile. */
    Pixel2("Pixel 2"),
    /** Pixel 2 XL device profile. */
    Pixel2XL("Pixel 2 XL"),
    /** Microsoft Lumia 550 device profile. */
    MicrosoftLumia550("Microsoft Lumia 550"),
    /** Microsoft Lumia 950 device profile. */
    MicrosoftLumia950("Microsoft Lumia 950"),
    /** Galaxy S III device profile. */
    GalaxySIII("Galaxy S III"),
    /** Galaxy S5 device profile. */
    GalaxyS5("Galaxy S5"),
    /** Kindle Fire HDX device profile. */
    KindleFireHDX("Kindle Fire HDX"),
    /** iPad Mini device profile. */
    iPadMini("iPad Mini"),
    /** iPad device profile. */
    iPad("iPad"),
    /** iPad Pro device profile. */
    iPadPro("iPad Pro"),
    /** Nexus 10 device profile. */
    Nexus10("Nexus 10"),
    /** Galaxy Note 3 device profile. */
    GalaxyNote3("Galaxy Note 3"),
    /** Galaxy Note II device profile. */
    GalaxyNoteII("Galaxy Note II"),
    /** Apple iPhone 4 device profile. */
    AppleiPhone4("Apple iPhone 4"),
    /** Apple iPhone 5 device profile. */
    AppleiPhone5("Apple iPhone 5"),
    /** Apple iPhone 6 device profile. */
    AppleiPhone6("Apple iPhone 6"),
    /** BlackBerry Z30 device profile. */
    BlackBerryZ30("BlackBerry Z30"),
    /** Google Nexus 4 device profile. */
    GoogleNexus4("Google Nexus 4"),
    /** Google Nexus 5 device profile. */
    GoogleNexus5("Google Nexus 5"),
    /** Google Nexus 6 device profile. */
    GoogleNexus6("Google Nexus 6"),
    /** Google Nexus 7 device profile. */
    GoogleNexus7("Google Nexus 7"),
    /** Google Nexus 10 device profile. */
    GoogleNexus10("Google Nexus 10"),
    /** LG Optimus L70 device profile. */
    LGOptimusL70("LG Optimus L70"),
    /** Nokia N9 device profile. */
    NokiaN9("Nokia N9"),
    /** Nokia Lumia 520 device profile. */
    NokiaLumia520("Nokia Lumia 520"),
    /** Samsung Galaxy S III device profile. */
    SamsungGalaxySIII("Samsung Galaxy S III"),
    /** Samsung Galaxy S4 device profile. */
    SamsungGalaxyS4("Samsung Galaxy S4"),
    /** Samsung Galaxy S20 Ultra device profile. */
    SamsungGalaxyS20Ultra("Samsung Galaxy S20 Ultra"),
    /** Amazon Kindle Fire HDX device profile. */
    AmazonKindleFireHDX("Amazon Kindle Fire HDX"),
    /** Apple iPad Mini device profile. */
    AppleiPadMini("Apple iPad Mini"),
    /** Apple iPad device profile. */
    AppleiPad("Apple iPad"),
    /** BlackBerry PlayBook device profile. */
    BlackBerryPlayBook("BlackBerry PlayBook"),
    /** Samsung Galaxy Note 3 device profile. */
    SamsungGalaxyNote3("Samsung Galaxy Note 3"),
    /** Samsung Galaxy Note II device profile. */
    SamsungGalaxyNoteII("Samsung Galaxy Note II"),
    /** Laptop with touch device profile. */
    Laptopwithtouch("Laptop with touch"),
    /** Laptop with HiDPI screen device profile. */
    LaptopwithHiDPIscreen("Laptop with HiDPI screen"),
    /** Laptop with MDPI screen device profile. */
    LaptopwithMDPIscreen("Laptop with MDPI screen");

    final String deviceName;

    EmulatedDevice(String deviceName){
        this.deviceName = deviceName;
    };

    /** @return a map containing the device name for Chrome mobile emulation */
    public Map<String, String> emulate(){
        return Map.of("deviceName", this.deviceName);
    }

    /**
     * @param text the device name to look up (case-insensitive)
     * @return the matching {@link EmulatedDevice}, or {@code null} if not found
     */
    public static EmulatedDevice getType(String text) {
        if (text != null)
            for (EmulatedDevice driverType:values())
                if (driverType.name().equalsIgnoreCase(text))
                    return driverType;
        return null;
    }
}
