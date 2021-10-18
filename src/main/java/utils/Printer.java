package utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import resources.Colors;

public class Printer extends Colors {

    Log log = LogFactory.getLog(Printer.class);

    public void print(Object text, String type){
        switch (type.toLowerCase()){
            case "warning":
                report(YELLOW+text+RESET);
                return;

            case "success":
                report(GREEN+text+RESET);
                return;

            case "error":
                report(RED +text+RESET);
                return;

            case "info":
                report(GRAY+text+RESET);
                return;

            default:
                report(text);
        }
    }

    public void report(Object text){log.info(text);}
}
