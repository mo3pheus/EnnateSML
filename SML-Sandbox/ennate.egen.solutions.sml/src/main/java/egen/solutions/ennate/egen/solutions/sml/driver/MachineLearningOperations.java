package egen.solutions.ennate.egen.solutions.sml.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import ennate.egen.solutions.sml.domain.Classifier;
import ennate.egen.solutions.sml.domain.ClusteringEngine;
import ennate.egen.solutions.sml.domain.ClusteringEngine.ClusteredPoints;
import ennate.egen.solutions.sml.model.Data;

public abstract class MachineLearningOperations<T extends Classifier> {
	private ArrayList<Data>		trainingSet;
	private ArrayList<Data>		testingSet;

	public ArrayList<Data> getTotalDataset() {
		return totalDataset;
	}

	private ArrayList<Data>		totalDataset;
	protected T					classificationEngine;
	protected ClusteringEngine	clusteringEngine;

	/**
	 * Instantiates the class.
	 */
	public MachineLearningOperations() {
		trainingSet = new ArrayList<Data>();
		testingSet = new ArrayList<Data>();
		totalDataset = new ArrayList<Data>();
	}

	/**
	 * Sets the classification engine
	 * 
	 * @param classificationEngine
	 */
	public abstract void setClassificationEngine(T classificationEngine);

	/**
	 * Returns the classification Engine
	 * 
	 * @return
	 */
	public abstract T getClassificationEngine();

	/**
	 * Returns clusteringEngine
	 * 
	 * @return
	 */
	public abstract ClusteringEngine getClusterer();

	/**
	 * Sets the clusteringEngine
	 * 
	 * @param clusteringEngine
	 */
	public abstract void setClusterer(ClusteringEngine clusteringEngine);

	/**
	 * Loads the data from the given fileLocation. model needs to be delimiter
	 * separated with the last column showing the classId.
	 * 
	 * @param fileLocation
	 * @param delimiter
	 * @param numberOfFields
	 * @throws IOException
	 * 
	 */
	public void loadData(String fileLocation, String delimiter, int numberOfFields) throws IOException {
		/*
		 * Load the file
		 */
		File file = new File(this.getClass().getClassLoader().getResource(fileLocation).getPath());
		totalDataset = new ArrayList<Data>();

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			try {
				Data temp = new Data(line, delimiter, numberOfFields);
				totalDataset.add(temp);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		br.close();
	}

	/**
	 * This function splits the complete data set into training and testing data
	 * sets with the given percentage.
	 * 
	 * @param trainPercent
	 */
	public void populateTrainTestSets(int trainPercent) {
		if (trainPercent >= 100) {
			System.out.println("Using entire data for training causes no learning!");
		}

		int trainSamplesCount = (int) (totalDataset.size() * trainPercent / 100.0d);
		int testSamplesCount = totalDataset.size() - trainSamplesCount;
		int[] testIndices = new int[testSamplesCount];

		int numFilled = 0;
		while (numFilled != testSamplesCount) {
			int randomNum = ThreadLocalRandom.current().nextInt(0, totalDataset.size() + 1);
			if (!alreadyPresent(randomNum, testIndices)) {
				testIndices[numFilled] = randomNum;
				numFilled++;
			}
		}

		for (int i = 0; i < totalDataset.size(); i++) {
			Data temp = totalDataset.get(i);
			if (alreadyPresent(i, testIndices)) {
				testingSet.add(temp);
			} else {
				trainingSet.add(temp);
			}
		}
	}

	/**
	 * Returns List of training data points
	 * 
	 * @return
	 */
	public ArrayList<Data> getTrainingData() {
		return trainingSet;
	}

	/**
	 * Returns List of testing data points
	 * 
	 * @return
	 */
	public ArrayList<Data> getTestingData() {
		return testingSet;
	}

	/**
	 * This function computes the total cost of the clustered points.
	 * 
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public double getCostFunction(Map<Data, ClusteredPoints> map) throws Exception {
		double cost = 0.0d;

		for (Data centroid : map.keySet()) {
			ClusteredPoints points = map.get(centroid);
			for (int i = 0; i < points.getPoints().size(); i++) {
				cost += ClusteringEngine.getDistance(centroid, points.getPoints().get(i));
			}
		}

		return cost;
	}

	private boolean alreadyPresent(int target, int[] host) {
		boolean present = false;
		for (int i = 0; i < host.length; i++) {
			if (host[i] == target) {
				present = true;
				break;
			}
		}
		return present;
	}
}
