package egen.solutions.ennate.egen.solutions.sml.driver;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import ennate.egen.solutions.sml.domain.ClassificationEngine;
import ennate.egen.solutions.sml.domain.Data;
import ennate.egen.solutions.sml.domain.JitendraDataModel;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Created by jmalakalapalli on 4/11/17.
 */
public class JitendraClassifier {

    private static List<Data> data;
    private static List<Data> reducedData;
    private static List<JitendraDataModel> dataModels;
    private static List<JitendraDataModel> reducedDataModels;
    private static JitendraDataModel covarianceDataModel;
    private static double[][] covarianceMatrix;
    private static List<String> result;
    private static List<String> PCAresult;
    private static int TOP_N = 2;

    public static void main(String[] args) {
        SanketML irisProblem = new SanketML();
        result = new ArrayList<>();
        PCAresult = new ArrayList<>();
        try {
            irisProblem.loadData("iris.data.txt", ",", 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = irisProblem.getTotalDataset();
        covarianceMatrix = new double[4][4];
        ClassificationEngine classificationEngine = new ClassificationEngine();
        ClassificationEngine classificationEngine2 = new ClassificationEngine();
        classificationEngine.buildModels(irisProblem.getTotalDataset(), 4);
        dataModels = classificationEngine.getModles();
        classificationEngine.buildOverallModal(irisProblem.getTotalDataset(), 4);
        covarianceDataModel = classificationEngine.getOverallDataModel();


        System.out.println("---------------- PDF and accuracy for original data------------");
        // calculate pdf for raw data
        // printClosestDistance(data, dataModels);
        printAccuracy(data, dataModels);  // print accuracy



        // calculate covariance matrix
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                double sum = 0.0d;
                for (Data d: data) {
                    sum += calculateCovarianceBetweenFields(d, i, j);
                }
                sum /= data.size();
                covarianceMatrix[i][j] = sum;
            }
        }

        System.out.println("The covariance matrix is ");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(covarianceMatrix[i][j] + " ");
            }
            System.out.println();
        }
        Matrix matrix = new Matrix(covarianceMatrix);

        // Find eigen values
        EigenvalueDecomposition ed = new EigenvalueDecomposition(matrix);
        System.out.println("The eigen values are");
        double[] eigenValues = ed.getRealEigenvalues();
        System.out.println(Arrays.toString(eigenValues));

        // Get the top two PCA components
        Object[] topEigenValues = getSortedTree(eigenValues).values().toArray();

        System.out.println(String.format("top %d components are", TOP_N));
        int[] topNEigenValues = IntStream.range(0, TOP_N)
                .mapToObj(i -> topEigenValues[i])
                .mapToInt(i -> (int)i)
                .toArray();

        Arrays.sort(topNEigenValues);
        Arrays.stream(topNEigenValues)
                .forEach(System.out::println);

        try {
            irisProblem.loadReducedData("iris.data.txt", ",", topNEigenValues);
        } catch (IOException e) {
            e.printStackTrace();
        }
        reducedData = irisProblem.getTotalReducedDataset();

        classificationEngine2.buildModels(irisProblem.getTotalReducedDataset(), topNEigenValues.length);
        reducedDataModels = classificationEngine2.getModles();



        System.out.println("---------------- PDF and accuracy for reduced data");
        // calculate pdf for raw data
        // printClosestDistance(reducedData, reducedDataModels);
        printAccuracy(reducedData, reducedDataModels);  // print accuracy

    }

    public static double calculateDistance(Data data, JitendraDataModel dataModel) {
        Double[] mean = dataModel.getMean().getFields();
        double distance = 0.0d;
        Double[] fields = data.getFields();
        for (int j = 0; j < fields.length; j++) {
            distance += (fields[j] - mean[j]) * (fields[j] - mean[j]);
            distance = Math.sqrt(distance);
        }
        System.out.print(" from " + dataModel.getClassId() + " dataModel is " + distance + ";");

        return distance;
    }

    public static double calculateCovarianceBetweenFields(Data d, int i, int j) {
        return (d.getFields()[i] - covarianceDataModel.getMean().getFields()[i]) *
                (d.getFields()[j] - covarianceDataModel.getMean().getFields()[j]);

    }

    public static double getAccuracy(List<Data> testData) {
        int accurate = 0;
        double accuracyPercentage = 0.0d;
        for (int i = 0; i < data.size(); i++) {
            if (testData.get(i).getClassId().equals(result.get(i))) {
                accurate++;
            }
        }

        accuracyPercentage = ((double) accurate / data.size()) * 100d;

        return accuracyPercentage;

    }

    public static double calculateProbabilityDensityFunction(Data data, JitendraDataModel dataModel) {
        Double[] mean = dataModel.getMean().getFields();
        Double[] stDev = dataModel.getStdDev().getFields();
        double distance = 0.0d;
        Double[] fields = data.getFields();
        for (int j = 0; j < fields.length; j++) {
            double a = (double) 1 / Math.sqrt(2 * 3.14 * stDev[j] * stDev[j]);
            double b = ((fields[j] - mean[j]) * (fields[j] - mean[j])) / (2 * stDev[j] * stDev[j]);
            double c = Math.exp(-1 * b);
            distance += a * c;
        }
        // System.out.print(" from " + dataModel.getClassId() + " dataModel is " + distance + ";");


        return distance;
    }

    private static void printClosestDistance(List<Data> data, List<JitendraDataModel> dataModels) {
        data.stream()
                .map(d -> {
                    System.out.print("The distance of " + d.toString());
                    return dataModels.stream()
                            .map(dataModel -> calculateProbabilityDensityFunction(d, dataModel))
                            .reduce(Double::min);
                })
                .forEach(distance -> System.out.print(" The closer distance is " + distance.get() + "\n"));

    }

    private static void printAccuracy(List<Data> data, List<JitendraDataModel> dataModels) {
        result.clear();
        for (int i = 0; i < data.size(); i++) {
            double max = 0.0d;
            JitendraDataModel closest = null;
            for (int j = 0; j < dataModels.size(); j++) {
                double distance = calculateProbabilityDensityFunction(data.get(i), dataModels.get(j));
                if (max < distance) {
                    max = distance;
                    closest = dataModels.get(j);
                }
            }
            result.add(closest.getClassId());
        }

        System.out.println("accuracy " + getAccuracy(data));
    }

    private static Map<Double, Integer> getSortedTree(double[] eigenValues) {
        Map<Double, Integer> treeMap = new TreeMap<>((Double d1, Double d2) -> {
            if (d2 > d1) return 1;
            if (d2 < d1) return -1;
            return 0;
        });
        IntStream.range(0, eigenValues.length)
                .forEach(i -> treeMap.put(eigenValues[i], i));

        return treeMap;

    }
}
