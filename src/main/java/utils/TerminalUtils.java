package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TerminalUtils {
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
                    System.out.println(line);
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
