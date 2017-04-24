package ennate.egen.solutions.sml.domain;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ShreedharSentenceCompletion
{

    public static void compare() {

        long start = System.currentTimeMillis();

        final String first = "mary had a little lamb";
        final String second = "bob is not so little, bob ate the little lamb";

        List<String> firstList = Arrays.asList(first.split(" "))
                .stream()
                .map(e -> e.endsWith(",") ? e.substring(0, e.lastIndexOf(",")) : e)
                .collect(Collectors.toList());

        List<String> secondList = Arrays.asList(second.split(" "))
                .stream()
                .map(e -> e.endsWith(",") ? e.substring(0, e.lastIndexOf(",")) : e)
                .collect(Collectors.toList());

        Set<String> union = new HashSet<String>() {{
            addAll(firstList);
            addAll(secondList);
        }};

        HashMap<String, Integer> oneVector = initialize(union);
        HashMap<String, Integer> twoVector = initialize(union);


        for (int i = 0; i < firstList.size() ; i++) {
            if( oneVector.get(firstList.get(i)) != null) {
                Integer count = oneVector.get(firstList.get(i));
                oneVector.put(firstList.get(i), ++count);
            }
        }

        for (int i = 0; i < secondList.size() ; i++) {
            if( twoVector.get(secondList.get(i)) != null) {
                Integer count = twoVector.get(secondList.get(i));
                twoVector.put(secondList.get(i), ++count);
            }
        }

        Double oneMagnitude = getMagnitude(oneVector);
        Double twoMagnitude = getMagnitude(twoVector);

        Double product = getProduct(oneVector, twoVector, union);
        Double cosine = product / (oneMagnitude * twoMagnitude);
        Double ans = Math.acos(cosine);
        long end = System.currentTimeMillis();

        System.out.println();
        System.out.println("Degree of similarity between the two sentences is: "+  Math.toDegrees(ans));
        System.out.println("Time taken: "+ (end - start));

    }

    private static Double getMagnitude(HashMap<String, Integer> vector) {

        Double summation = 0.0d;
        for (Map.Entry<String, Integer> entry : vector.entrySet()) {
            Integer count = entry.getValue();
            summation += Math.pow(count, 2);
        }

        Double magnitude = Math.sqrt(summation);
        return magnitude;
    }

    private static Double getProduct (final HashMap<String, Integer> vector1, final HashMap<String, Integer> vector2, final Set<String> union) {

        List<Integer> vector1Count = new ArrayList<Integer>(){{ addAll(vector1.values()); }};
        List<Integer> vector2Count = new ArrayList<Integer>(){{ addAll(vector2.values()); }};

        Double product = 0.0d;
        for (int i = 0; i < union.size() ; i++) {
            product += (vector1Count.get(i) * vector2Count.get(i));
        }

        return product;
    }

    private static HashMap<String, Integer> initialize(Set<String> array) {
        HashMap<String, Integer> toReturn = new HashMap<String, Integer>();
        for (String s : array) {
            toReturn.put(s, 0);
        }

        return toReturn;
    }
}
