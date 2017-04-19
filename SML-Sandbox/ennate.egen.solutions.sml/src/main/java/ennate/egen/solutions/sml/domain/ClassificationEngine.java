package ennate.egen.solutions.sml.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassificationEngine implements Classifier {

	private ArrayList<DataModel>	models;
	private static boolean			debug;

	/**
	 * Set debugMode
	 * 
	 * @param debugFlag
	 */
	public static void setDebugMode(boolean debugFlag) {
		debug = debugFlag;
	}

	/**
	 * This function builds models based on the trainingData and the number of
	 * fields.
	 */
	public void buildModels(ArrayList<Data> trainingData, int numberOfFields) {
		models = new ArrayList<DataModel>();
		Map<String, Integer> classMap = getClassMap(trainingData);
		Object[] classIds = classMap.keySet().toArray();

		for (int i = 0; i < classIds.length; i++) {
			ArrayList<Data> temp = new ArrayList<Data>();
			for (int j = 0; j < trainingData.size(); j++) {
				if ((trainingData.get(j) != null) && (trainingData.get(j).getClassId() != null)
						&& (trainingData.get(j).getClassId().equals((String) classIds[i]))) {
					temp.add(trainingData.get(j));
				}
			}
			models.add(new DataModel(temp, numberOfFields));
		}
	}

	/**
	 * This function returns the built models. If buildModels() is not called.
	 * This will return null.
	 * 
	 * @return
	 */
	public ArrayList<DataModel> getModles() {
		return models;
	}

	/**
	 * This function lets you classify the given sample.
	 */
	public Result classify(Data sample) {
		Result id = new Result();
		double maxPDF = Double.MIN_VALUE;
		String classId = "";
		for (int i = 0; i < models.size(); i++) {
			double dist = ClassificationEngine.getPDFVal(sample, models.get(i));

			if (dist > maxPDF) {
				maxPDF = dist;
				classId = models.get(i).getClassId();
			}
		}

		id.setClassId(classId);
		id.setConfidence(maxPDF);

		printLine("Predicted classId = " + classId);
		return id;
	}

	/**
	 * This function gets the Probability Density Function value of a sample
	 * with respect to the given model.
	 * 
	 * @param sample
	 * @param model
	 * @return
	 */
	public static double getPDFVal(Data sample, DataModel model) {
		double distance = 0.0d;
		Double[] stdDev = model.getStdDev().getFields();
		Double[] mean = model.getMean().getFields();
		Double[] sFields = sample.getFields();

		for (int i = 0; i < stdDev.length; i++) {
			double constant = 1.0d / Math.sqrt(2.0d * Math.PI * stdDev[i] * stdDev[i]);
			double term = (-1.0d) * (sFields[i] - mean[i]) * (sFields[i] - mean[i]);
			term /= (2.0d * stdDev[i] * stdDev[i]);
			distance += constant * Math.exp(term);
		}

		printLine("Sample = " + sample.toString());
		printLine("Model = " + model.toString());
		printLine("PDF Score = " + distance);
		printLine("------------------------------------------------------");

		return distance;
	}

	/**
	 * This function returns a map of the data given.
	 * 
	 * @param data
	 * @return
	 */
	private Map<String, Integer> getClassMap(ArrayList<Data> data) {
		Map<String, Integer> classMap = new HashMap<String, Integer>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getClassId() != null) {
				classMap.put(data.get(i).getClassId(), 1);
			}
		}

		return classMap;
	}

	protected static void printLine(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
}
