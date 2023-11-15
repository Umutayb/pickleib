package pickleib.utilities;

import context.ContextStore;
import properties.PropertyUtility;
import java.util.Properties;

public class PropertyLoader {
    public static boolean loaded;

    static {load();}

    public static void load(){
        if (!loaded){
            Properties properties = PropertyUtility.getProperties();
            Properties pickleibProperties = PropertyUtility.loadPropertyFile("pickleib.properties");
            ContextStore.loadProperties("pickleib.properties");

            if (!properties.isEmpty()){
                for (Object key:pickleibProperties.keySet())
                    properties.putIfAbsent(key, pickleibProperties.get(key));
            }
            else properties = pickleibProperties;

            PropertyUtility.properties.putAll(properties);
        }
        loaded = true;
    }
}
