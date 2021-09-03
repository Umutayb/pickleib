package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {
    private Properties properties;

    public PropertiesReader(String propertyFileName){
        try {
            InputStream is = getClass().getClassLoader()
                    .getResourceAsStream(propertyFileName);
            this.properties = new Properties();
            this.properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String propertyName) {
        return this.properties.getProperty(propertyName);
    }
}
