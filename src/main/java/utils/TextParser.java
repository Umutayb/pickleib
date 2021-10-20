package utils;

import java.util.Scanner;

public class TextParser {

    public static void main(String[] args) {//Sample execution
        Printer log = new Printer(TextParser.class);
        TextParser textParser = new TextParser();
        Scanner scanner = new Scanner(System.in);
        log.new important("Enter the input");
        String input = scanner.nextLine(); // "ajsKAagq5J3w._CoolButton-sg-j3yaG3 a3TGb"
        log.new important("Enter the first keyword"); //._
        String initialKeyword = scanner.nextLine();
        log.new important("Enter the final keyword"); // -
        String finalKeyword = scanner.nextLine();
        scanner.close();
        log.new important(textParser.parse(initialKeyword,finalKeyword,input));
    }

    public String parse(String initialKeyword, String finalKeyword, String input){
        int firstIndex = 0;

        Scanner scanner = new Scanner(input);

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();

            if (initialKeyword != null)
                firstIndex = line.indexOf(initialKeyword) + initialKeyword.length();

            if (initialKeyword != null && finalKeyword != null){
                //This is the case where the string will be cut from both sides
                if (line.contains(initialKeyword) && line.contains(finalKeyword)){
                    final int lastIndex = line.indexOf(finalKeyword);
                    scanner.close();
                    return line.substring(firstIndex, lastIndex);

                }
            }
            else if (initialKeyword != null){
                //This is the case where only a single side of the string will be cut (left side)
                if (line.contains(initialKeyword)){
                    scanner.close();
                    return line.substring(firstIndex);

                }
            }
            else if (finalKeyword != null){
                //This is the case where only a single side of the string will be cut (right side)
                if (line.contains(finalKeyword)){
                    final int lastIndex = line.indexOf(finalKeyword);
                    scanner.close();
                    return line.substring(0, lastIndex);

                }
            }
        }
        scanner.close();
        return null;
    }
}
