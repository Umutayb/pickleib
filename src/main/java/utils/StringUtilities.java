package utils;

import org.apache.commons.lang3.RandomStringUtils;
import java.text.Normalizer;
import java.util.*;

import static resources.Colors.*;

public class StringUtilities {   //Utility methods

    Printer log = new Printer(StringUtilities.class);

    public String reverse(String input){
        StringBuilder reversed = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {reversed.append(input.charAt(input.length() - i - 1));}
        return reversed.toString();
    }

    public String firstLetterCapped(String inputString){ //Capitalizes the first letter of the input string

        if (inputString!=null){
            String firstLetter = inputString.substring(0, 1);
            String remainingLetters = inputString.substring(1);
            firstLetter = firstLetter.toUpperCase();

            return firstLetter + remainingLetters;
        }
        else
            return null;
    }

    public String cleanText(String inputString){ //Cleans the input string of spaces, numbers etc.

        inputString = inputString
                .replaceAll("\\s", "")                    //Cleans spaces
                .replaceAll("[0-9]", "")                  //Cleans numbers
                .replaceAll("[-+^.,'&%/()=\"?!:;_*]*", "") //Cleans special characters
                .replaceAll("[^\\x00-\\x7F]", "");        //Cleans non english characters

        if (inputString.isEmpty())
            inputString = generateRandomString("element",4, true, false);

        return inputString;
    }

    public String normalize(String inputString){ //Replaces non english characters in input string

        return Normalizer
                .normalize(inputString, Normalizer.Form.NFD)
                .replaceAll("ç", "c")
                .replaceAll("ğ", "g")
                .replaceAll("ü", "u")
                .replaceAll("ş", "s")
                .replaceAll("ı", "i")
                .replaceAll("[^\\p{ASCII}]", "");
    }

    public String shorten(String inputString, int length) { //Shortens string to the given length

        return inputString.substring(0, Math.min(inputString.length(), length));
    }

    //Generates random string according to the input rules
    public String generateRandomString(String keyword, int length, boolean useLetters, boolean useNumbers) {
        return keyword + RandomStringUtils.random(length, useLetters, useNumbers);
    }

    public int measureDistanceBetween(String input, String firstKeyword, String lastKeyword){

        // Remove any special chars from string
        final String strOnlyWords = input.replace(",", "").replace(".", "");

        final List<String> words = Arrays.asList(strOnlyWords.split(" "));
        final int index1 = words.indexOf(firstKeyword);
        final int index2 = words.indexOf(lastKeyword);
        int distance = -1;

        // Check index of two words
        if (index1 != -1 && index2 != - 1) {
            distance = index2 - index1;
        }

        return distance;
    }

    public Map<String, String> str2Map(String inputString){

        Map<String, String> outputMap = new HashMap<>();

        inputString = inputString.replaceAll("[{}]*", "");

        String[] pairs = inputString.split(",");

        for(String pair: pairs) {

            String[] keyValue = pair.split("=");

            try{
                if (keyValue[0] != null && keyValue[1] != null)
                    outputMap.put(keyValue[0].trim(), keyValue[1].trim());

                else if (keyValue[0] == null)
                    throw new Exception( "First value of this pair was found to be null");

                else throw new Exception( "Second value of this pair was found to be null");

            }catch (Exception gamma){
                log.new error(GRAY+gamma+RESET);

            }

        }
        return outputMap;
    }

}