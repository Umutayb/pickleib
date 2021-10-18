package utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import resources.Colors;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class Printer extends Colors {

    private final Log log;

    public <T> Printer(Class<T> className){log = LogFactory.getLog(className);}

    public class important {
        public important(Object text){report(PURPLE + text + RESET);}
    }

    public class info {
        public info(Object text) {report(GRAY + text + RESET);}
    }

    public class success {
        public success(Object text){report(GREEN + text + RESET);}
    }

    public class warning {
        public warning(Object text){report(YELLOW + text + RESET);}
    }

    public class error {
        public error(Object text){report(RED + text + RESET);}
    }

    public void report(Object text){
        Properties properties = new Properties();
        try {properties.load(new FileReader("src/test/resources/test.properties"));}
        catch (IOException e) {e.printStackTrace();}
        if (Boolean.parseBoolean(properties.getProperty("enableLogging")))
            log.info(text);
        else
            System.out.println(text);
    }
}
