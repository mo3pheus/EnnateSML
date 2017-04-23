package egen.solutions.ennate.egen.solutions.sml.driver;


import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by jmalakalapalli on 4/21/17.
 */
public class CosineSImilarity {

    public static void main(String[] args) {
        String str1 = "Mary had a little lamb Mary had a little lamb";
        String str2 = "Bob little is actually not so little and eats lamb";

        computCosineSimilarity(str1, str2);
    }

    private static void computCosineSimilarity(String str1, String str2) {
        String[] tokens1 = str1.split("\\s");
        String[] tokens2 = str2.split("\\s");
        Map<String, Integer> tokens1Map = new HashMap<>();
        Map<String, Integer> tokens2Map = new HashMap<>();
        Set<String> globalDIctionary = new HashSet<>();
        for (int i = 0; i < tokens1.length; i++) {
            String str = tokens1[i];
            globalDIctionary.add(str);
            if (tokens1Map.containsKey(str)) {
                tokens1Map.put(str, tokens1Map.get(str)+1);
            } else {
                tokens1Map.put(str, 1);
            }
        }
        for (int i = 0; i < tokens2.length; i++) {
            String str = tokens2[i];
            globalDIctionary.add(str);
            if (tokens2Map.containsKey(str)) {
                tokens2Map.put(str, tokens2Map.get(str)+1);
            } else {
                tokens2Map.put(str, 1);
            }
        }

        int sumOfA = tokens1Map.values().stream()
                .mapToInt(i -> i*i)
                .sum();
        double magnitudeOfA = Math.sqrt(sumOfA);

        int sumOfB = tokens2Map.values().stream()
                .mapToInt(i -> i*i)
                .sum();
        double magnitudeOfB = Math.sqrt(sumOfB);
        int sum = dotProduct(tokens1Map, tokens2Map, globalDIctionary);
        double cos = (double)sum / (magnitudeOfA * magnitudeOfB);
        System.out.print(Math.acos(cos) * (180/Math.PI));

    }

    private static int dotProduct(Map map1, Map map2, Set<String> set) {
        int sum = 0;
        for (String str: set) {
            int i = 0, j = 0;
            if (map1.containsKey(str)) {
                i = (int)map1.get(str);
            }
            if (map2.containsKey(str)) {
                j = (int)map2.get(str);
            }
            sum += i*j;
        }
        return sum;


    }
}
