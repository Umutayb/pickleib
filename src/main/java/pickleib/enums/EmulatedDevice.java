package pickleib.enums;

import java.util.HashMap;
import java.util.Map;

public enum EmulatedDevice {

    iphone11 {
        @Override
        public Map<String, Object> emulate() {
            Map<String, Object> deviceMetrics = new HashMap<>();
            deviceMetrics.put("width", 375);
            deviceMetrics.put("height", 667);
            deviceMetrics.put("pixelRatio", 2.0);
            Map<String, Object> mobileEmulation = new HashMap<>();
            mobileEmulation.put("deviceMetrics", deviceMetrics);
            mobileEmulation.put("userAgent", "Mozilla/5.0 (iPhone; CPU iPhone OS 11_0 like Mac OS X) AppleWebKit/604.1.34 (KHTML, like Gecko) Version/11.0 Mobile/15A5341f Safari/604.1");
            return mobileEmulation;
        }
        @Override
        public Map<String, Integer> getWidthAndHeight() {
            Map<String, Integer> widthAndHeight = new HashMap<>();
            widthAndHeight.put("width", 375);
            widthAndHeight.put("height", 667);
            return widthAndHeight;
        }
    };

    public abstract Map<String, Object> emulate();
    public abstract Map<String, Integer> getWidthAndHeight();
}
