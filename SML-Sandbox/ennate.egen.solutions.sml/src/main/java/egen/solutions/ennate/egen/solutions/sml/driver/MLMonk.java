package egen.solutions.ennate.egen.solutions.sml.driver;

import java.io.IOException;
import java.util.Map;

import ennate.egen.solutions.sml.model.Data;
import ennate.egen.solutions.sml.utils.Utils;
import ennate.egen.solutions.sml.domain.*;
import ennate.egen.solutions.sml.domain.ClusteringEngine.ClusteredPoints;

public class MLMonk {

	public static void main(String[] args) {
		SanketML irisProblem = new SanketML();
		try {
			irisProblem.loadData("iris.data.txt", ",", 4);
		} catch (IOException e) {
			e.printStackTrace();
		}
		irisProblem.populateTrainTestSets(80);

		System.out.println(" Number of training samples = " + irisProblem.getTrainingData().size());
		System.out.println(" Number of testing samples = " + irisProblem.getTestingData().size());

		/*
		 *  Classification
		 */
		Double classificationAccuracy = Utils.classify(irisProblem.getTrainingData(), irisProblem.getTestingData(), 4);

		System.out.println("Classification Accuracy ==> " + classificationAccuracy);

		System.out.println("\n\n\n ############################  CLUSTERING  ###################################\n\n ");
		int numClusters = 3;
		ClusteringEngine clusterer = new ClusteringEngine();
		try {
			Map<Data, ClusteredPoints> result = clusterer.clusterData(irisProblem.getTotalDataset(), numClusters);
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
