package ennate.egen.solutions.sml.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.TreeMap;

public class LexicalAnalyzerUtils {

    public static double compareSentences(String sentence1, String sentence2) {
        ArrayList<String> strings = new ArrayList<String>();
        strings.add(sentence1);
        strings.add(sentence2);
        ArrayList<String[]> lowerCaseStringArrays = getSentenceArrayInLowerCase(strings);
        HashSet<String> uniqueWordsInStrings = getUniqueWordsInStrings(lowerCaseStringArrays);
        TreeMap<String, Integer> frequencyMap1 = getFrequencyOfWords(uniqueWordsInStrings, lowerCaseStringArrays.get(0));
        TreeMap<String, Integer> frequencyMap2 = getFrequencyOfWords(uniqueWordsInStrings, lowerCaseStringArrays.get(1));
        int[] frequencyVector1 = getFrequencyVector(frequencyMap1);
        int[] frequencyVector2 = getFrequencyVector(frequencyMap2);
        double magnitude1 = getMagnitude(frequencyVector1);
        double magnitude2 = getMagnitude(frequencyVector2);
        double dotProduct = getDotProductOfArrays(frequencyVector1, frequencyVector2);
        double angle = computeAngle(magnitude1, magnitude2, dotProduct);
        System.out.println(uniqueWordsInStrings);
        System.out.println("Frequency Vector 1==> " + Arrays.toString(frequencyVector1));
        System.out.println("Frequency Vector 2 ==> " + Arrays.toString(frequencyVector2));

        return angle;
    }

    public static ArrayList<String[]> getSentenceArrayInLowerCase(ArrayList<String> strings) {
        ArrayList<String[]> lowerCaseStringArrays = new ArrayList<String[]>();
        for(String sentence: strings) {
            String[] lowerCaseStringArray = sentence
                    .toLowerCase()
                    .replaceAll(",", "")
                    .replaceAll("\\.", "")
                    .split(" ");
            lowerCaseStringArrays.add(lowerCaseStringArray);
        }

        return lowerCaseStringArrays;
    }

    public static HashSet<String> getUniqueWordsInStrings(ArrayList<String[]> strings) {
        HashSet<String> uniqueStringArray = new HashSet<String>();
        for(String[] string: strings) {
            for (String word: string) {
                uniqueStringArray.add(word);
            }
        }
        return uniqueStringArray;
    }

    public static TreeMap<String, Integer> getFrequencyOfWords(HashSet<String> words, String[] sentence) {
        TreeMap<String, Integer> frequency = new TreeMap<String, Integer>();

        for(String word: words) {
            frequency.put(word, 0);
        }

        for(String word: sentence) {
            frequency.put(word, frequency.get(word)+1);
        }

        return frequency;
    }

    public static int[] getFrequencyVector(TreeMap<String, Integer> frequencyMap) {
        int[] values = new int[frequencyMap.size()];
        int index = 0;

        for(String word : frequencyMap.keySet()) {
            values[index] = frequencyMap.get(word);
            index++;
        }

        return values;
    }


    public static double getMagnitude(int[] array) {
        Double magnitude = 0.0d;

        for(int value: array) {
            magnitude += Math.pow(value, 2);
        }

        return Math.sqrt(magnitude);
    }

    public static double getDotProductOfArrays(int[] array1, int[] array2) {
        double value = 0.0d;

        for(int i=0; i<array1.length; i++) {
            value += (array1[i] * array2[i]);
        }

        return value;
    }

    private static double computeAngle(double magnitude1, double magnitude2, double dotProduct) {
       return  Math.toDegrees(Math.acos(dotProduct/(magnitude1 * magnitude2)));
    }
}
