package pickleib.utilities;

import context.ContextStore;
import properties.PropertyUtilities;
import java.util.Properties;

public class PropertyLoader {
    public static boolean loaded;

    static {load();}

    public static void load(){
        if (!loaded){
            Properties properties = PropertyUtilities.getProperties();
            Properties pickleibProperties = PropertyUtilities.loadPropertyFile("pickleib.properties");
            ContextStore.merge(pickleibProperties);

            if (!properties.isEmpty()){
                for (Object key:pickleibProperties.keySet())
                    properties.putIfAbsent(key, pickleibProperties.get(key));
            }
            else properties = pickleibProperties;

            PropertyUtilities.properties.putAll(properties);
        }
        loaded = true;
    }
}
