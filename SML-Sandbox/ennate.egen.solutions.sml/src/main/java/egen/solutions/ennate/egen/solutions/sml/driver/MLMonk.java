package egen.solutions.ennate.egen.solutions.sml.driver;

import ennate.egen.solutions.sml.domain.ClassificationEngine;
import ennate.egen.solutions.sml.domain.ClusteringEngine.ClusteredPoints;
import ennate.egen.solutions.sml.domain.Data;
import ennate.egen.solutions.sml.domain.ShreedharClassificationEngine;
import ennate.egen.solutions.sml.domain.ShreedharClusteringEngine;
import ennate.egen.solutions.sml.domain.ShreedharPrincipleComponentAnalysis;
import ennate.egen.solutions.sml.domain.ShreedharSentenceCompletion;

import java.io.IOException;
import java.util.Map;

public class MLMonk {

	private final static int numberOfFields = 4;
	private final static int numClusters = 3;

	public static void main(String[] args) {
		/*
		 * Instantiate the Machine Learning framework
		 */
		SanketML irisProblem = new SanketML();
		try {
			irisProblem.loadData("iris.data.txt", ",", numberOfFields);
//			irisProblem.loadData("iris-principle-component.csv", ",", numberOfFields);

		} catch (IOException e) {
			e.printStackTrace();
		}
		irisProblem.populateTrainTestSets(80);

		System.out.println(" Number of training samples = " + irisProblem.getTrainingData().size());
		System.out.println(" Number of testing samples = " + irisProblem.getTestingData().size());

		performClustering(irisProblem);
		performClassification(irisProblem);
		performPrincipleComponentAnalysis(irisProblem);

		//Sentence Completion
		ShreedharSentenceCompletion.compare();
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
		 *
		 * 1. Instantiate the classificationEngine, 2. Report accuracy.
		 *
		 */
	private static void performClassification(final SanketML irisProblem) {
		ShreedharClassificationEngine classificationEngine = new ShreedharClassificationEngine();
		classificationEngine.buildModels(irisProblem.getTrainingData(), numberOfFields);

//		ClassificationEngine classificationEngine = new ClassificationEngine();
//		classificationEngine.buildModels(irisProblem.getTrainingData(), 4);

		ClassificationEngine.setDebugMode(true);
		irisProblem.setClassificationEngine(classificationEngine);
		System.out.println("Accuracy Percentage = " + irisProblem.getAccuracy() + " % ");
	}

	   /**
		 * Implements k-means clustering algorithm.
		 */
	private static void performClustering(final SanketML irisProblem) {

		System.out.println("\n\n\n ############################  CLUSTERING  ###################################\n\n ");
		ShreedharClusteringEngine clusterer = new ShreedharClusteringEngine();

//		ClusteringEngine clusterer = new ClusteringEngine();
		try {
			Map<Data, ClusteredPoints> result = clusterer.clusterData(irisProblem.getTrainingData(), numClusters);
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
