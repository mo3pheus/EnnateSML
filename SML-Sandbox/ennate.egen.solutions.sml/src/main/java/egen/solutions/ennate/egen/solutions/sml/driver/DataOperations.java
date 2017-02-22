package egen.solutions.ennate.egen.solutions.sml.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import ennate.egen.solutions.sml.domain.Data;
import ennate.egen.solutions.sml.domain.DataModel;
import ennate.egen.solutions.sml.domain.GenericData;
import ennate.egen.solutions.sml.domain.Result;

public class DataOperations {

	private ArrayList<Data> trainingSet;
	private ArrayList<Data> testingSet;
	private ArrayList<Data> totalDataset;
	private GenericData gd;
	private int numberOfFields;

	public DataOperations() {
		trainingSet = new ArrayList<Data>();
		testingSet = new ArrayList<Data>();
		totalDataset = new ArrayList<Data>();
	}
	
	public ArrayList<Data> loadData(File file, String delimiter, int numberOfFields) throws IOException {
		ArrayList<Data> data = new ArrayList<Data>();
		if (file == null) {
			System.out.println(" File invalid! ");
			return null;
		}

		this.numberOfFields = numberOfFields;

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			try {
				Data temp = new Data(line, delimiter, numberOfFields);
				data.add(temp);
			} catch (Exception e) {

			}
		}

		br.close();
		return data;
	}

	public void loadArrayData(String location, int numFields) {
		/**
		 * Lets confirm we can get to the database
		 */

		try {
			this.numberOfFields = numFields;
			URL fileLocation = this.getClass().getClassLoader().getResource(location);
			if (fileLocation == null) {
				System.out.println("URL is null");
			}
			System.out.println(fileLocation.getPath().toString());

			GenericData temp = new GenericData(new File(fileLocation.getPath().toString()), ",", numFields);

			totalDataset = temp.getData();
			for (int i = 0; i < totalDataset.size(); i++) {
				System.out.println(totalDataset.get(i).toString());
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public GenericData getGd() {
		return gd;
	}

	public void setGd(GenericData gd) {
		this.gd = gd;
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

	public ArrayList<Data> getTrainingData() {
		return trainingSet;
	}

	public ArrayList<Data> getTestingData() {
		return testingSet;
	}

	public static int findNumClasses(ArrayList<Data> dataSet) {
		Map<String, Integer> classIdMap = new HashMap<String, Integer>();
		for (int i = 0; i < dataSet.size(); i++) {
			classIdMap.put(dataSet.get(i).getClassId(), 1);
		}

		return classIdMap.size();
	}

	public void buildGaussianModel() {
		System.out.println(" Data loaded total row count = " + totalDataset.size());
		gd = new GenericData(trainingSet, numberOfFields);
		gd.buildModels();

		System.out.println("Model for class = " + gd.getModels().get(0).getClassId());
		System.out.println("Mean values = ");
		System.out.println(gd.getModels().get(0).getMean().toString());
		System.out.println("StdDeviation values = ");
		System.out.println(gd.getModels().get(0).getStdDev().toString());
	}

	public void setGaussianModel(ArrayList<DataModel> models) {
		System.out.println(" Data loaded total row count = " + totalDataset.size());
		gd = new GenericData(trainingSet, numberOfFields);
		gd.setModels(models);
	}

	public double getAccuracy() {
		int totalTest = testingSet.size();
		int accurate = 0;
		for (int i = 0; i < testingSet.size(); i++) {
			Data testPoint = testingSet.get(i);
			Result result = gd.classify(testPoint);
			if (testPoint.getClassId().equals(result.getClassId())) {
				accurate++;
			}
		}

		return (double) (accurate / (double) totalTest) * 100.0d;
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
