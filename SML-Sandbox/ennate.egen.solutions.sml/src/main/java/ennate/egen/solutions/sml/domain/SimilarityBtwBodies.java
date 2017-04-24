package ennate.egen.solutions.sml.domain;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimilarityBtwBodies {

    public double getTextBodies(String text1, String text2) {
        List<String> textList1 = Arrays.asList(text1.replace(".", "").replace(",", "").replace("?", "").replace("!", "").split(" "));
        List<String> textList2 = Arrays.asList(text2.replace(".", "").replace(",", "").replace("?", "").replace("!", "").split(" "));

        List<String> unionSet = findUnionSet(textList1, textList2);

        Map<String, Integer> textMap1 = new LinkedHashMap<String, Integer>();
        Map<String, Integer> textMap2 = new LinkedHashMap<String, Integer>();

        for (String word : textList1) {
            if (!textMap1.containsKey(word)) {
                textMap1.put(word, 1);
            } else {
                textMap1.put(word, textMap1.get(word) + 1);
            }
        }

        for (String word : textList2) {
            if (!textMap2.containsKey(word)) {
                textMap2.put(word, 1);
            } else {
                textMap2.put(word, textMap2.get(word) + 1);
            }
        }

        int col = unionSet.size();
        int[][] setA = new int[1][col];
        int[][] setB = new int[1][col];
        int i = 0;


        for (String word : unionSet) {
            setA[0][i] = textMap1.containsKey(word) ? textMap1.get(word) : 0;
            setB[0][i] = textMap2.containsKey(word) ? textMap2.get(word) : 0;
            i++;
        }

        double dotProduct = dotProduct(setA, setB);
        double distance = getDistance(setA, setB);

       return (Math.acos(dotProduct / distance) * 180/Math.PI);

    }

    private double getDistance(int[][] setA, int[][] setB) {
        double tempA = 0.0d;
        double tempB = 0.0d;

        for (int i = 0; i < setA[0].length; i++) {
            tempA += setA[0][i] * setA[0][i];
            tempB += setB[0][i] * setB[0][i];
        }

        return Math.sqrt(tempA) * Math.sqrt(tempB);
    }

    private double dotProduct(int[][] setA, int[][] setB) {
        double result = 0.0d;
        for (int i = 0; i < setA[0].length; i++) {
            result += setA[0][i] * setB[0][i];
        }

        return result;
    }

    private List<String> findUnionSet(List<String> textList1, List<String> textList2) {
        Set<String> union = new LinkedHashSet<String>();

        union.addAll(textList1);
        union.addAll(textList2);

        return new ArrayList<String>(union);
    }

    public static void main(String[] args) {
        String str1 = "Mary had a little lamb. Mary had a little lamb.";
        String str2 = "Bob little is actually not so little and eats lamb.";

        SimilarityBtwBodies s = new SimilarityBtwBodies();
        System.out.print(s.getTextBodies(str1, str2));
    }

}
