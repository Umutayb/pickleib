package utils;

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class TerminalUtilities {

    Printer log = new Printer(TerminalUtilities.class);

    public void runTerminalCommand(String command, String logText){
        try {
            String path = System.getProperty("user.dir");
            ProcessBuilder builder = new ProcessBuilder(
                    "Terminal.app", "usr/", "cd \""+path+"\" && "+command);
            builder.redirectErrorStream(true);
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while (true){
                line = reader.readLine();
                if (line.contains(logText)){
                    Thread.sleep(5000);
                    log.new success(line);
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
