package pickleib.utilities;

import utils.PropertyUtility;

import java.util.Properties;

public class PropertyLoader {
    public static boolean loaded;

    static {load();}

    public static void load(){
        if (!loaded){
            Properties properties = PropertyUtility.getProperties();
            PropertyUtility propertyUtility = new PropertyUtility();
            Properties pickleibProperties = propertyUtility.getProperties("pickleib.properties");

            if (!properties.isEmpty()){
                for (Object key:pickleibProperties.keySet())
                    properties.putIfAbsent(key, pickleibProperties.get(key));
            }
            else properties = pickleibProperties;

            PropertyUtility.setProperties(properties);
        }
        loaded = true;
    }
}
