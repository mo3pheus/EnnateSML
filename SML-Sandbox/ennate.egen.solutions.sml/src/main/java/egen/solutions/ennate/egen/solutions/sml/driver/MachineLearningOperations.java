package egen.solutions.ennate.egen.solutions.sml.driver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import ennate.egen.solutions.sml.domain.Classifier;
import ennate.egen.solutions.sml.domain.ClusteringEngine;
import ennate.egen.solutions.sml.domain.ClusteringEngine.ClusteredPoints;
import ennate.egen.solutions.sml.domain.Data;
import ennate.egen.solutions.sml.domain.Result;

public abstract class MachineLearningOperations<T extends Classifier> {
	private ArrayList<Data>		trainingSet;
	private ArrayList<Data>		testingSet;
	private ArrayList<Data>		totalDataset;
	private ArrayList<Data>		reducedDataset;
	protected T					classificationEngine;
	protected ClusteringEngine	clusteringEngine;
	private HashMap<String, ArrayList<String>> trainingGenreToMovie;
	private HashMap<String, ArrayList<String>> testingGenreToMovie;
	private HashMap<String, String> movieDataset;

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
	 * Loads the data from the given fileLocation. Data needs to be delimiter
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

	public void loadMoviesData(String fileLocation, String delimiter) throws IOException {
		File file = new File(this.getClass().getClassLoader().getResource(fileLocation).getPath());

		movieDataset = new HashMap<>();

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		br.readLine();
		while ((line = br.readLine()) != null) {
			try {
				String[] content = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
				/*
		        * Sanity check
		        */
				if (content.length != 3) {
					System.out.println(
							"Corrupted string passed in" + content + " expected fields length is 3 ");
					return;
				}
				if (!content[2].contains("|") && content[2].matches("Action|Comedy|Romance|Drama|Horror")) {
					movieDataset.put(content[1], content[2]);
				}

			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

	}

	public void loadReducedData(String fileLocation, String delimiter, int[] dimensions) throws IOException {
		/*
		 * Load the file
		 */
		File file = new File(this.getClass().getClassLoader().getResource(fileLocation).getPath());
		reducedDataset = new ArrayList<Data>();

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			try {
				Data temp = new Data(line, delimiter, dimensions);
				reducedDataset.add(temp);
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

	public void populateTrainTestMovieSets(int trainPercent) {
		trainingGenreToMovie = new HashMap<>();
		testingGenreToMovie = new HashMap<>();
		if (trainPercent >= 100) {
			System.out.println("Using entire data for training causes no learning!");
		}

		int trainSamplesCount = (int) (movieDataset.size() * trainPercent / 100.0d);
		int testSamplesCount = movieDataset.size() - trainSamplesCount;
		int[] testIndices = new int[testSamplesCount];

		int numFilled = 0;
		List<String> movies = new ArrayList<>(movieDataset.keySet());
		while (numFilled != testSamplesCount) {
			int randomNum = ThreadLocalRandom.current().nextInt(0, movies.size() + 1);
			if (!alreadyPresent(randomNum, testIndices)) {
				testIndices[numFilled] = randomNum;
				numFilled++;
			}
		}

		for (int i = 0; i < movies.size(); i++) {
			String movie = movies.get(i);
			String genre= movieDataset.get(movie);
			if (alreadyPresent(i, testIndices)) {
				if (testingGenreToMovie.containsKey(genre)) {
					testingGenreToMovie.get(genre).add(movie);
				} else {
					ArrayList<String> list = new ArrayList<>();
					list.add(movie);
					testingGenreToMovie.put(genre, list);
				}

			} else {
				if (trainingGenreToMovie.containsKey(genre)) {
					trainingGenreToMovie.get(genre).add(movie);
				} else {
					ArrayList<String> list = new ArrayList<>();
					list.add(movie);
					trainingGenreToMovie.put(genre, list);
				}
			}
		}
	}

	/**
	 * This function gets the accuracy score by evaluating learnt models on
	 * testSet.
	 *
	 * @return
	 */
	public double getAccuracy() {
		int totalTest = testingSet.size();
		int accurate = 0;
		for (int i = 0; i < testingSet.size(); i++) {
			Data testPoint = testingSet.get(i);
			System.out.println("\n\n=============================== " + i + " =====================================");
			Result result = classificationEngine.classify(testPoint);
			if (testPoint.getClassId().equals(result.getClassId())) {
				accurate++;
			}
			System.out.println("========================================================================");
		}

		return (double) (accurate / (double) totalTest) * 100.0d;
	}

	/**
	 * Returns List of training data points
	 *
	 * @return
	 */
	public ArrayList<Data> getTotalDataset() {
		return totalDataset;
	}

	public HashMap<String, ArrayList<String>> getTrainingGenreToMovie() {
		return trainingGenreToMovie;
	}

	public HashMap<String, ArrayList<String>> getTestingGenreToMovie() {
		return testingGenreToMovie;
	}

	public HashMap<String, String> getMovieDataset() {
		return movieDataset;
	}

	public ArrayList<Data> getTotalReducedDataset() {
		return reducedDataset;
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
	 * Returns List of all data points
	 *
	 * @return
	 */
	public ArrayList<Data> getTrainingData() {
		return trainingSet;
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
