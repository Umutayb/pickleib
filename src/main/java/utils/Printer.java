package utils;

import resources.Colors;

public class Printer extends Colors {

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

    public void report(Object text){System.out.println(text);}
}
