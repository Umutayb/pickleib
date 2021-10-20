package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

    Printer log = new Printer(StringUtilities.class);

    private Properties properties;

    public PropertiesReader(String propertyFileName){
        try {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(propertyFileName);
            this.properties = new Properties();
            this.properties.load(inputStream);
        }
        catch (IOException e) {log.new error(e.getMessage());}
    }

    public String getProperty(String propertyName) {
        return this.properties.getProperty(propertyName);
    }
}
