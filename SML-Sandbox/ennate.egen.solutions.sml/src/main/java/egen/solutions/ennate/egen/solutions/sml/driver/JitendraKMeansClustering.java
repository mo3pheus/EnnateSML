package egen.solutions.ennate.egen.solutions.sml.driver;

import com.fasterxml.jackson.databind.ObjectMapper;
import ennate.egen.solutions.sml.domain.ClassificationEngine;
import ennate.egen.solutions.sml.domain.Data;
import ennate.egen.solutions.sml.domain.JitendraDataModel;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by jmalakalapalli on 4/23/17.
 */
public class JitendraKMeansClustering {
    private static ArrayList<Data> data;
    private static LinkedHashMap<Data, ArrayList<Data>> oldCentroids;
    private static LinkedHashMap<Data, ArrayList<Data>> newCentroids;
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static int K = 3;
    private static int NUMBER_OF_FIELDS = 4;
    public static final double ERR_MARGIN = 0.0001d;
    public static final int MAX_ITERATIONS = 1000;
    public static void main(String[] args) {

        SanketML irisProblem = new SanketML();
        try {
            irisProblem.loadData("iris.data.txt", ",", NUMBER_OF_FIELDS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = irisProblem.getTotalDataset();
        computeCostFunction(5);



    }

    private static int[] getKRandomInitialDataPoints() {
        Random random = new Random();
        int[] randomNumbers = new int[K];
        int i = 0;
        while (i < K) {
            int k = random.nextInt(data.size());
            randomNumbers[i] = k;
            i++;
        }

        return randomNumbers;
    }

    private static double calculateStraightLineDistance(Data data, Data centroid) {
        double distance = 0.0d;
        Double[] fields = data.getFields();
        for (int j = 0; j < fields.length; j++) {
            distance += (fields[j] - centroid.getFields()[j]) * (fields[j] - centroid.getFields()[j]);
            distance = Math.sqrt(distance);
        }

        return distance;
    }

    private static double computeDistanceFromOldCentroidToNewCentroid(LinkedHashMap<Data, ArrayList<Data>> oldCentroids, LinkedHashMap<Data, ArrayList<Data>> newCentroids) {
        Object[] c1 = oldCentroids.keySet().toArray();
        Object[] c2 = newCentroids.keySet().toArray();
        return IntStream.range(0, K)
                .mapToDouble(i -> calculateStraightLineDistance(objectMapper.convertValue(c1[i], Data.class), objectMapper.convertValue(c2[i], Data.class)))
                .sum();
    }

    private static void computeCostFunction(int N) {
        IntStream.range(1, N+1)
                .forEach(i -> {
                    K = i;
                    computeKMeans();
                    Iterator it = oldCentroids.entrySet().iterator();
                    double sum = 0.0d;
                    while(it.hasNext()) {
                        Map.Entry pair = (Map.Entry)it.next();
                        ArrayList<Data> group = (ArrayList<Data>) pair.getValue();
                        sum += group.stream()
                                .mapToDouble(d -> calculateStraightLineDistance(d, objectMapper.convertValue(pair.getKey(),Data.class)))
                                .sum();
                    }
                    System.out.println(String.format("Cost function for K = %1$1d is %2$2f", K, sum));
                });
    }

    private static void computeKMeans() {
        int[] randomNumbers = getKRandomInitialDataPoints();

        ClassificationEngine classificationEngine1 = new ClassificationEngine();
        classificationEngine1.buildModels(data, NUMBER_OF_FIELDS);
        List<JitendraDataModel> dataModelsList = classificationEngine1.getModles();

        // initialize the initial centroids with the randomly selected data points
        oldCentroids = new LinkedHashMap<>();
        Arrays.stream(randomNumbers)
                .forEach(random -> oldCentroids.put(data.get(random), new ArrayList<>()));

        int iteration = 0;
        while (true) {
            iteration++;
            for (int i = 0; i < data.size(); i++) {
                Data leastDistanceCentroid = null;
                double min =  Double.POSITIVE_INFINITY;
                Iterator it = oldCentroids.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    double distance = calculateStraightLineDistance(data.get(i), objectMapper.convertValue(pair.getKey(),Data.class));
                    if (distance < min) {
                        min = distance;
                        leastDistanceCentroid = objectMapper.convertValue(pair.getKey(), Data.class);
                    }
                }
                oldCentroids.get(leastDistanceCentroid).add(data.get(i));
            }

            // for each group find mean. This corresponds to new centroid

            newCentroids = new LinkedHashMap<>();
            oldCentroids.values().stream()
                    .forEach(group -> {
                        ClassificationEngine classificationEngine = new ClassificationEngine();
                        classificationEngine.buildOverallModal(group, NUMBER_OF_FIELDS);
                        JitendraDataModel dataModel = classificationEngine.getOverallDataModel();
                        newCentroids.put(dataModel.getMean(), group);
                    });

            double distance_between_centroids = computeDistanceFromOldCentroidToNewCentroid(oldCentroids, newCentroids);
            if (distance_between_centroids > ERR_MARGIN && iteration < MAX_ITERATIONS) {
                oldCentroids = newCentroids;
                oldCentroids.keySet().stream()
                        .forEach(centroid -> oldCentroids.get(centroid).clear());
            } else {
                break;
            }

        }
    }
}
