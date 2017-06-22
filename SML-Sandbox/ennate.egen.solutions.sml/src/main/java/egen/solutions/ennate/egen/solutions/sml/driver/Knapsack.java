package egen.solutions.ennate.egen.solutions.sml.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jmalakalapalli on 5/6/17.
 */
public class Knapsack {
    private static int[] weights = new int[] {5,7,1,2,4,4,1};
    private static int[] values = new int[] {10,15,4,6,8,8,4};
    private static int MAX_WEIGHT = 8;
    private static int[][] m = new int[weights.length + 1][MAX_WEIGHT+1];
    public static void main(String[] args) {

        //initialize the knapsack matrix
        for (int j = 0; j < MAX_WEIGHT + 1; j++) {
            m[0][j] = 0;
        }

        computeKnapsackTable();
        Arrays.stream(m).
                forEach(row -> {
                    Arrays.stream(row)
                            .forEach(i -> System.out.print(i + "\t"));
                    System.out.println();
                });

        System.out.println("Items to be added into bag are");
        List<Integer> itemsToBeAdded= computeElmentsInsideKnapsack();
        itemsToBeAdded.stream()
                .forEach(i -> System.out.print(i + " "));
    }

    public static void computeKnapsackTable() {
        for (int i = 1; i < weights.length + 1; i++) {
            for (int j = 0; j < MAX_WEIGHT + 1; j++) {
                if (weights[i-1] > j) {
                    m[i][j] = m[i-1][j];
                } else {
                    m[i][j] = Integer.max(m[i-1][j], m[i-1][j - weights[i-1]] + values[i-1]);
                }
            }
        }
    }

    public static List<Integer> computeElmentsInsideKnapsack() {
        int j = MAX_WEIGHT;
        int i = weights.length;
        List<Integer> itemsToBeAdded = new ArrayList<>();
        while (i > 0) {
            if (m[i][j] > m[i-1][j]) {
                itemsToBeAdded.add(weights[i-1]);
                j = j - weights[i-1];
            }
            i--;
        }

        return itemsToBeAdded;
    }
}
