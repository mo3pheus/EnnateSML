package ennate.egen.solutions.sml.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassificationEngine implements Classifier {

	private ArrayList<DataModel> models;
	private ArrayList<DataModelV2> modelsV2;

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

	public void buildModelsV2(ArrayList<Data> trainingData, int numberOfFields) {
		modelsV2 = new ArrayList<DataModelV2>();
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
			modelsV2.add(new DataModelV2(temp, numberOfFields));
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

	public ArrayList<DataModelV2> getModelsV2() {
		return modelsV2;
	}

	/**
	 * This function lets you classify the given sample.
	 */
	public Result classify(Data sample) {
		Result id = new Result();
		double maxProb = Double.MIN_VALUE;
		String classId = "";
		for (int i = 0; i < models.size(); i++) {
			double dist = ClassificationEngine.getDistance(sample, models.get(i));

			if (dist > maxProb) {
				maxProb = dist;
				classId = models.get(i).getClassId();
			}
		}

		id.setClassId(classId);
		id.setConfidence(maxProb);

		return id;
	}

	public Result classifyV2(Data sample) {
		Result id = new Result();
		double maxProb = Double.MIN_VALUE;
		String classId = "";
		for (int i = 0; i < modelsV2.size(); i++) {
			double dist = ClassificationEngine.getDistanceV2(sample, modelsV2.get(i));

			if (dist > maxProb) {
				maxProb = dist;
				classId = modelsV2.get(i).getClassId();
			}
		}

		id.setClassId(classId);
		id.setConfidence(maxProb);

		return id;
	}

	/**
	 * This function gets a distance measure from a sample to the given model.
	 * 
	 * @param sample
	 * @param model
	 * @return
	 */
	public static double getDistance(Data sample, DataModel model) {
		double distance = 0.0d;
		Double[] stdDev = model.getStdDev().getFields();
		Double[] mean = model.getMean().getFields();
		Double[] sFields = sample.getFields();

		for (int i = 0; i < stdDev.length; i++) {
			double constant = 1.0d / (stdDev[i] * Math.sqrt(2.0d * Math.PI));
			double term = (sFields[i] - mean[i]) / stdDev[i];
			term *= term;
			term *= -0.5d;
			distance += (Math.pow(Math.E, term) * constant);
		}

		return distance;
	}

	public static double getDistanceV2(Data sample, DataModelV2 model) {
		double distance = 0.0d;
		Double[] stdDev = model.getStandardDeviation().getFields();
		Double[] mean = model.getMean().getFields();
		Double[] sFields = sample.getFields();

		for (int i = 0; i < stdDev.length; i++) {
			double constant = 1.0d / (stdDev[i] * Math.sqrt(2.0d * Math.PI));
			double term = (sFields[i] - mean[i]) / stdDev[i];
			term *= term;
			term *= -0.5d;
			distance += (Math.pow(Math.E, term) * constant);
		}

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
}
