package utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NumericUtilities {

    public int randomNumber(int min, int max){ //Generates random number in the given margins
        int range = max - min + 1;
        return (int)(Math.random() * range) + min;
    }

    public List<Integer> listSorter(List<Integer> list, boolean larger2smaller){
        List<Integer> orderedList = new ArrayList<>();
        int lnIndex;
        int ln;
        Map<String , Integer> map;
        while (list.size() > 0){
            if (larger2smaller)
                map = getLargestInList(list);

            else
                map = getSmallestInList(list);

            ln = map.get("number");
            lnIndex = map.get("index");
            list.remove(lnIndex);
            orderedList.add(ln);
        }
        return orderedList;
    }

    public Map<String, Integer> getLargestInList(List<Integer> list){
        Map<String, Integer> largestNumberMap = new HashMap<>();
        int largestNumber = 0;
        int largestNumberIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            if (largestNumber<list.get(i)){
                largestNumber = list.get(i);
                largestNumberIndex = i;
            }
        }
        largestNumberMap.put("number", largestNumber);
        largestNumberMap.put("index", largestNumberIndex);
        return largestNumberMap;
    }

    public Map<String, Integer> getSmallestInList(List<Integer> list){
        Map<String, Integer> largestNumberMap = new HashMap<>();
        int smallestNumber = 999999999;
        int smallestNumberIndex = 0;
        for (int i = 0; i < list.size(); i++) {
            if (smallestNumber>list.get(i)){
                smallestNumber = list.get(i);
                smallestNumberIndex = i;
            }
        }
        largestNumberMap.put("number", smallestNumber);
        largestNumberMap.put("index", smallestNumberIndex);
        return largestNumberMap;
    }

    public Double shortenDouble(Double number){
        DecimalFormat formatter = new DecimalFormat("#.##");
        return Double.parseDouble(formatter.format(number));
    }
}
