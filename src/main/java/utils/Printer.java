package utils;

import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.TakesScreenshot;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.apache.commons.logging.Log;
import java.util.Properties;
import java.io.IOException;
import java.io.FileReader;
import resources.Colors;
import org.junit.Assert;
import java.io.File;

public class Printer extends Colors {

    private final Log log;

    NumericUtilities numeric = new NumericUtilities();

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

    public void captureScreen(String specName, RemoteWebDriver driver) {
        try {
             new info("Capturing page");

            String name = specName+"#"+numeric.randomNumber(1,10000)+".jpg";
            File sourceFile = new File("Screenshots");
            File fileDestination  = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(fileDestination, new File(sourceFile, name));

            new info("Screenshot saved as; "+name+" at the \"Screenshots\" file.");

        }catch (Exception gamma){
            Assert.fail(YELLOW+"Could not capture screen"+RED+"\n\t"+gamma+RESET);
            driver.quit();
        }
    }
}
