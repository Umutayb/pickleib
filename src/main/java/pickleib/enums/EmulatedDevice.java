package pickleib.enums;

import java.util.HashMap;
import java.util.Map;

public enum EmulatedDevice {

    iphone12Pro {
        @Override
        public Map<String, String> emulate() {
            Map<String, String> deviceMetrics = new HashMap<>();
            deviceMetrics.put("deviceName", "iPhone 12 Pro");
            return deviceMetrics;
        }
        @Override
        public Map<String, Integer> getWidthAndHeight() {
            Map<String, Integer> widthAndHeight = new HashMap<>();
            widthAndHeight.put("width", 390);
            widthAndHeight.put("height", 844);
            return widthAndHeight;
        }
    },
    ipadAir {
        @Override
        public Map<String, String> emulate() {
            Map<String, String> deviceMetrics = new HashMap<>();
            deviceMetrics.put("deviceName", "iPad Air");
            return deviceMetrics;
        }
        @Override
        public Map<String, Integer> getWidthAndHeight() {
            Map<String, Integer> widthAndHeight = new HashMap<>();
            widthAndHeight.put("width", 820);
            widthAndHeight.put("height", 1180);
            return widthAndHeight;
        }
    };

    public abstract Map<String, String> emulate();
    public abstract Map<String, Integer> getWidthAndHeight();
}
