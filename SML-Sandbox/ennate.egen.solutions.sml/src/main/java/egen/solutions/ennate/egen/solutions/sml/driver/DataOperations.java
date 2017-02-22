package egen.solutions.ennate.egen.solutions.sml.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import ennate.egen.solutions.sml.domain.Classifier;
import ennate.egen.solutions.sml.domain.Data;
import ennate.egen.solutions.sml.domain.Result;

public class DataOperations<T extends Classifier> {

	private ArrayList<Data> trainingSet;
	private ArrayList<Data> testingSet;
	private ArrayList<Data> totalDataset;
	private T classificationEngine;
	
	public DataOperations() {
		trainingSet = new ArrayList<Data>();
		testingSet = new ArrayList<Data>();
		totalDataset = new ArrayList<Data>();
	}
	
	public void setClassificationEngine(T classificationEngine){
		this.classificationEngine = classificationEngine;
	}
	
	public T getClassificationEngine(){
		return classificationEngine;
	}

	public void loadData(String fileLocation, String delimiter, int numberOfFields) throws IOException {
		/*
		 * Load the file
		 */
		//System.out.println(" Path  = " + this.getClass().getClassLoader().getResource(fileLocation).getPath());
		File file = new File(this.getClass().getClassLoader().getResource(fileLocation).getPath());
		totalDataset = new ArrayList<Data>();
		//this.numberOfFields = numberOfFields;

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			try {
				Data temp = new Data(line, delimiter, numberOfFields);
				totalDataset.add(temp);
			} catch (Exception e) {

			}
		}

		br.close();
	}

	/**
	 * This function splits the complete dataset into training and testing data
	 * sets with the given percentage.
	 * 
	 * @param trainPercent
	 */
	public void populateTrainTestSets(int trainPercent) {
		if (trainPercent >= 100) {
			System.out.println("Using entire data for training is stupid!");
		}

		int trainSamplesCount = (int) (totalDataset.size() * trainPercent / 100.0d);
		int testSamplesCount = totalDataset.size() - trainSamplesCount;
		int[] testIndices = new int[testSamplesCount];

		// generate the indices for random test samples
		int numFilled = 0;
		while (numFilled != testSamplesCount) {
			int randomNum = ThreadLocalRandom.current().nextInt(0, testSamplesCount + 1);
			if (!alreadyPresent(randomNum, testIndices)) {
				testIndices[numFilled] = randomNum;
				numFilled++;
			}
		}

		// pull the elements and populate the arrays
		for (int i = 0; i < totalDataset.size(); i++) {
			Data temp = totalDataset.get(i);
			if (alreadyPresent(i, testIndices)) {
				testingSet.add(temp);
			} else {
				trainingSet.add(temp);
			}
		}
	}
	
	public double getAccuracy() {
		int totalTest = testingSet.size();
		int accurate = 0;
		for (int i = 0; i < testingSet.size(); i++) {
			Data testPoint = testingSet.get(i);
			Result result = classificationEngine.classify(testPoint);
			if (testPoint.getClassId().equals(result.getClassId())) {
				accurate++;
			}
		}

		return (double) (accurate / (double) totalTest) * 100.0d;
	}

	public ArrayList<Data> getTrainingData() {
		return trainingSet;
	}

	public ArrayList<Data> getTestingData() {
		return testingSet;
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
