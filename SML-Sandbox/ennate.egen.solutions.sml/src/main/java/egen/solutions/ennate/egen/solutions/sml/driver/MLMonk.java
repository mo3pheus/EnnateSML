package egen.solutions.ennate.egen.solutions.sml.driver;

import ennate.egen.solutions.sml.domain.ClassificationEngine;
import ennate.egen.solutions.sml.domain.ClusteringEngine;
import ennate.egen.solutions.sml.domain.ClusteringEngine.ClusteredPoints;
import ennate.egen.solutions.sml.domain.Data;
import ennate.egen.solutions.sml.domain.ShreedharPrincipleComponentAnalysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MLMonk {

    private final static int numberOfFields = 2;
    private final static int numClusters = 3;
    private final static String fileName = "beijing_00_1";
    private final static String fileExtension = ".txt";
    private final static String resourcesPath = "/Users/schampakaram/IdeaProjects/DataAnalytics/EnnateSML/SML-Sandbox/ennate.egen.solutions.sml/src/main/resources/";

    public static void main(String[] args) {
        /*
		 * Instantiate the Machine Learning framework
		 */
        SanketML beijingInput = new SanketML();
        try {
            beijingInput.loadData(fileName + fileExtension, ",", numberOfFields);
//			irisProblem.loadData("iris-principle-component.csv", ",", numberOfFields);

        } catch (IOException e) {
            e.printStackTrace();
        }
        beijingInput.populateTrainTestSets(90);

        System.out.println(" Number of training samples = " + beijingInput.getTrainingData().size());
        System.out.println(" Number of testing samples = " + beijingInput.getTestingData().size());

        Double distance = beijingInput.calculateGeodesicDistance();
        Double numberOfClusters = Math.floor(distance / 0.125);
        System.out.println("Number of clusters calculated is: " + numberOfClusters.intValue());

        Map<Data, ClusteredPoints> result = performClustering(beijingInput, numberOfClusters.intValue());

        List<String> contentList = writeClusterOutput(result);

        SanketML beijingOutput = new SanketML();
        try {
            Thread.sleep(1000*10);
            beijingOutput.loadData( contentList, ",", numberOfFields);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        beijingOutput.populateTrainTestSets(90);

        System.out.println(" Number of training samples = " + beijingOutput.getTrainingData().size());
        System.out.println(" Number of testing samples = " + beijingOutput.getTestingData().size());

		performClassification(beijingOutput);
//		performPrincipleComponentAnalysis(irisProblem);

        //Sentence Completion
//		ShreedharSentenceCompletion.compare();
    }


    private static void performPrincipleComponentAnalysis(final SanketML irisProblem) {
        System.out.println("=================================================================================");

        irisProblem.populateTrainTestSets(100);
        System.out.println(" Number of samples for Principle component Analysis = " + irisProblem.getTrainingData().size());

        ShreedharPrincipleComponentAnalysis componentAnalysis = new ShreedharPrincipleComponentAnalysis(irisProblem.getTestingData(), numberOfFields);
        componentAnalysis.computePrincipleComponents();
    }

    /**
     * This part should be done by the students. Implement your own version
     * of ClassificationEngine and compare accuracy you get.
     * <p>
     * 1. Instantiate the classificationEngine, 2. Report accuracy.
     */
    private static void performClassification(final SanketML irisProblem) {
//		ShreedharClassificationEngine classificationEngine = new ShreedharClassificationEngine();
//		classificationEngine.buildModels(irisProblem.getTrainingData(), numberOfFields);

        ClassificationEngine classificationEngine = new ClassificationEngine();
        classificationEngine.buildModels(irisProblem.getTrainingData(), numberOfFields);

        ClassificationEngine.setDebugMode(true);
        irisProblem.setClassificationEngine(classificationEngine);
        System.out.println("Accuracy Percentage = " + irisProblem.getAccuracy() + " % ");
    }

    /**
     * Implements k-means clustering algorithm.
     */
    private static Map<Data, ClusteredPoints> performClustering(final SanketML irisProblem, final Integer numClusters) {

        System.out.println("\n\n\n ############################  CLUSTERING  ###################################\n\n ");
//		ShreedharClusteringEngine clusterer = new ShreedharClusteringEngine();

        Map<Data, ClusteredPoints> result = new HashMap<>();
        ClusteringEngine clusterer = new ClusteringEngine();
        try {
            result = clusterer.clusterData(irisProblem.getTrainingData(), numClusters);
            irisProblem.setClusterer(clusterer);

            for (Data centroid : result.keySet()) {
                ClusteredPoints points = result.get(centroid);
                System.out.println(" Centroid = " + centroid.toString() + " memberSize = " + points.getPoints().size());
            }

            System.out.println(
                    " For number of clusters = " + numClusters + " Cost = " + irisProblem.getCostFunction(result));



        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in clustering data!");
        }
        return result;
    }


    private static List<String> writeClusterOutput(Map<Data, ClusteredPoints> result) {
        List<String> contentList = new ArrayList<>();
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(resourcesPath + fileName + "_output" + fileExtension))) {
            StringBuilder builder = new StringBuilder();
            for (Map.Entry<Data, ClusteredPoints> entry : result.entrySet()) {

                List<Data> points = entry.getValue().getPoints();
                System.out.println(String.format("Writing to file: [%s] classId: [%s]", fileName + "_output", points.get(0).getClassId()));

                for (Data point : points) {
                    builder.setLength(0);
                    Double[] data = point.getFields();
                    String classId = point.getClassId();

                    builder.append(data[0]);
                    builder.append(",");
                    builder.append(data[1]);
                    builder.append(",");
                    builder.append(classId);
                    builder.append("\n");

                    String content = builder.toString();
                    contentList.add(content);

                    bw.write(content);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    return contentList;
    }
}



/*
 * public static void main1(String[] args) { SanketML bloodProblem = new
 * SanketML();
 * 
 * // Recency (months),Frequency (times),Monetary (c.c. blood),Time //
 * //(months),"whether he/she donated blood in March 2007"
 * 
 * bloodProblem.loadData("transfusion.data.txt", ",",4);
 * bloodProblem.populateTrainTestSets(92);
 * 
 * System.out.println(" Number of training samples = " +
 * bloodProblem.getTrainingData().size());
 * System.out.println(" Number of testing samples = " +
 * bloodProblem.getTestingData().size());
 * 
 * 
 * System.out.println(bloodProblem.getGd().getModels().size());
 * System.out.println(bloodProblem.getAccuracy()); }
 */

/*
 * 
 * public static void main3(String[] args) { DataOperations glassProblem = new
 * DataOperations();
 * 
 * // Recency (months),Frequency (times),Monetary (c.c. blood),Time //
 * (months),"whether he/she donated blood in March 2007"
 * 
 * glassProblem.loadArrayData("glass.data.csv", 9);
 * glassProblem.populateTrainTestSets(90);
 * 
 * System.out.println(" Number of training samples = " +
 * glassProblem.getTrainingData().size());
 * System.out.println(" Number of testing samples = " +
 * glassProblem.getTestingData().size());
 * 
 * glassProblem.buildGaussianModel();
 * System.out.println(glassProblem.getGd().getModels().size());
 * System.out.println(glassProblem.getAccuracy()); }
 */
