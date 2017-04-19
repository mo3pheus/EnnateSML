/**
 * 
 */
package ennate.egen.solutions.sml.etl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import egen.solutions.ennate.egen.solutions.sml.driver.SanketML;
import ennate.egen.solutions.sml.domain.ClassificationEngine;
import ennate.egen.solutions.sml.domain.Data;

/**
 * @author ktalabattula
 *
 */
public class KesavaUtil {
	
	public static final double TOLERABLE_LOW_VALUE = 0.1d;
	
	public static void evaluatePDF(int noOfFields, SanketML preVersion) {
		System.out.println("*******************************************************************************************");
		System.out.println("**************Classify Dataset using Probability Distribution Function*********************");
		classifyDataset(noOfFields, preVersion);
		System.out.println("*******************************************************************************************");
	}

	private static void classifyDataset(int noOfFields, SanketML preVersion) {
		preVersion.populateTrainTestSets(80);
		
		System.out.println(" Number of training samples = " + preVersion.getTrainingData().size());
		System.out.println(" Number of testing samples = " + preVersion.getTestingData().size());
		
		ClassificationEngine classificationEngine = new ClassificationEngine();
		classificationEngine.buildModelsV2(preVersion.getTrainingData(), noOfFields);
		
		ClassificationEngine.setDebugMode(true);
		preVersion.setClassificationEngine(classificationEngine);
		System.out.println("Accuracy Percentage = " + preVersion.getAccuracyV2() + " % ");
		System.out.println("*******************************************************************************************");
	}

	public static Map<Data, List<Data>> evaluateKCluster(int noOfFields, SanketML machineLearning, Random random) {
		int noOfClusters = 0;
		List<Double>clusterDistance = new ArrayList<Double>();
		double previousDistance = 0.0d;
		double currentDistance = 0.0d;
		double percentChange = 0.0d;
		ArrayList<Data> totalDataset = machineLearning.getTotalData();
		Map<Data, List<Data>> clusterMap;
		
		do {
			noOfClusters++;
			System.out.println("*******************************************************************************************");
			System.out.println("***************************K-Mean Clustering with clusters: "+ noOfClusters + "****************************");
			System.out.println("*******************************************************************************************");
			clusterMap = mapCentroids(totalDataset, noOfFields, noOfClusters, random);
			currentDistance = evaluateClusterDistance(clusterMap);
			percentChange = (currentDistance - previousDistance)/currentDistance;
			percentChange = percentChange < 0 ? (0 - percentChange) : percentChange;
			clusterDistance.add(currentDistance);
			previousDistance = currentDistance;
		} while (percentChange > TOLERABLE_LOW_VALUE);
		
		System.out.println("*******************************************************************************************");
		System.out.println("******************************K-Mean Clustering completed *********************************");
		System.out.println("*******************************************************************************************");
		
		return clusterMap;
	}
	
	public static Map<Data, List<Data>> evaluateKClusterWithNClusters(int noOfFields, SanketML machineLearning, Random random, int noOfClusters) {
		List<Double>clusterDistance = new ArrayList<Double>();
		double previousDistance = 0.0d;
		double currentDistance = 0.0d;
		double percentChange = 0.0d;
		ArrayList<Data> totalDataset = machineLearning.getTotalData();
		Map<Data, List<Data>> clusterMap;
		
		System.out.println("*******************************************************************************************");
		System.out.println("*************************** K-Mean Clustering with clusters: "+ noOfClusters + "****************************");
		System.out.println("*******************************************************************************************");
		
		do {
			clusterMap = mapCentroids(totalDataset, noOfFields, noOfClusters, random);
			currentDistance = evaluateClusterDistance(clusterMap);
			percentChange = (currentDistance - previousDistance)/currentDistance;
			percentChange = percentChange < 0 ? (0 - percentChange) : percentChange;
			clusterDistance.add(currentDistance);
			previousDistance = currentDistance;
		} while (percentChange > TOLERABLE_LOW_VALUE);
		
		System.out.println("*******************************************************************************************");
		System.out.println("****************************** K-Mean Clustering completed ********************************");
		System.out.println("*******************************************************************************************");
		
		return clusterMap;
	}

	private static Double evaluateClusterDistance(Map<Data, List<Data>> clusterMap) {
		Double distance = 0.0d;
		for(Data centroid: clusterMap.keySet()) {
			for(Data data: clusterMap.get(centroid)) {
				distance += getEuclidianDistanceBetweenPoints(data, centroid);
			}
		}
		return distance;
	}

	private static Map<Data, List<Data>> mapCentroids(ArrayList<Data> totalDataset, int noOfFields, int noOfClusters, Random random) {
		double distanceFromPreviousCentroids = 0.0d;
		Map<Data, List<Data>> result = new HashMap<Data, List<Data>>();
		Data[] means = new Data[noOfClusters]; 
		int sizeOfDataset = totalDataset.size();
		
		for(int i = 0; i < noOfClusters; i++) {
			Data centroid = new Data(noOfFields);
			Data mean = new Data(noOfFields);
			centroid.setClassId("Class: " + i);
			mean.setClassId("Class: " + i);
			centroid.setFields(totalDataset.get(random.nextInt(sizeOfDataset)).getFields());
			mean.setFields(centroid.getFields());
			result.put(centroid, new ArrayList<Data>());
			means[i] = mean;
		}
		
		do {
			result = classifyClusters(result, totalDataset, noOfFields);
			int index = 0;
			distanceFromPreviousCentroids = 0.0d;
			for(Data centroid: result.keySet()) {
				means[index] = calculateMean(result.get(centroid), centroid);
				distanceFromPreviousCentroids += getEuclidianDistanceBetweenPoints(means[index], centroid);
				centroid.setFields(means[index].getFields());
				index++;
			}
		} while(distanceFromPreviousCentroids > TOLERABLE_LOW_VALUE);
			
		return result;
	}
	
	private static Data calculateMean(List<Data> datalist, Data centroid) {
		if (datalist.size() == 0) {
			return centroid;
		}
		int dimensions = datalist.get(0).getFields().length;
		Data mean = new Data(dimensions);
		mean.setClassId(datalist.get(0).getClassId());
		int size = datalist.size();
		Double[] fields = mean.getFields();
		for(int i = 0; i < dimensions; i++) {
			fields[i] = 0.0d;
		}
		
		for(Data data : datalist) {
			Double[] dataFields = data.getFields();
			for(int i = 0; i < dimensions; i++) {
				fields[i] += dataFields[i]/size;
			}
		}
		
		mean.setFields(fields);
		return mean;
	}

	private static Map<Data, List<Data>> classifyClusters(Map<Data, List<Data>> result, ArrayList<Data> totalDataset,
			int noOfFields) {
		Data[] centroids = result.keySet().toArray(new Data[result.keySet().size()]);
		int size = centroids.length;
		
		for(Data data: totalDataset) {
			double distance = getEuclidianDistanceBetweenPoints(centroids[0], data);
			int index = 0;
			for (int i = 1; i < size; i++) {
				double temp = getEuclidianDistanceBetweenPoints(centroids[i], data);
				if (distance > temp) {
					distance = temp;
					index = i;
				}
			}
			data.setClassId(centroids[index].getClassId());
			result.get(centroids[index]).add(data);
		}
		return result;
	}

	public static double getEuclidianDistanceBetweenPoints(Data pointA, Data pointB) {
		double distance = 0.0d;
		if (pointA == null || pointB == null) {
		return distance;
		}
		Double[] fieldsB = pointB.getFields();
		Double[] fieldsA = pointA.getFields();
		 
		for (int i = 0; i < fieldsB.length; i++) {
			distance += (fieldsB[i] - fieldsA[i]) * (fieldsB[i] - fieldsA[i]);
		}
		 
		return Math.sqrt(distance);
	}
}
