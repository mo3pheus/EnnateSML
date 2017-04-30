package ennate.egen.solutions.sml.domain;

import ennate.egen.solutions.sml.model.Data;
import ennate.egen.solutions.sml.model.Model;
import ennate.egen.solutions.sml.model.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassificationEngine implements Classifier {

	private ArrayList<Model> models;
	private static boolean			debug;

	/**
	 * Set debugMode
	 *
	 * @param debug
	 */
	public static void setDebugMode(boolean debugFlag) {
		debug = debugFlag;
	}

	/**
	 * This function builds models based on the trainingData and the number of
	 * fields.
	 */
	public void buildModels(ArrayList<Data> trainingData, int numberOfFields) {
		models = new ArrayList<Model>();
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
			models.add(new Model(temp, numberOfFields));
		}
	}

	/**
	 * This function returns the built models. If buildModels() is not called.
	 * This will return null.
	 * 
	 * @return
	 */
	public ArrayList<Model> getModles() {
		return models;
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

	private static void printLine(String s) {
		if (debug) {
			System.out.println(s);
		}
	}

	public Result classify(Data sample) {
		Result result = new Result();

		result.setSample(sample);

		for(int j=0; j<models.size();j++) {
			Double pdf = computePDFValue(sample, models.get(j));

			if (result.getConfidence() < pdf) {
				result.setConfidence(pdf);
				result.setClassId(models.get(j).getClassId().toString());
			}
		}
		return result;
	}

	public ArrayList<Result> classifyData(ArrayList<Data> data) {
		ArrayList<Result> classifiedSamples = new ArrayList<Result>();

		for(Data sample : data) {
			classifiedSamples.add(classify(sample));
		}

		return classifiedSamples;
	}

	public Double computePDFValue(Data data, Model model) {
		Double value = 0.0;
		Double[] sample = data.getFields();
		Double[] mean = model.getMean().getFields();
		Double[] standardDeviation = model.getStandardDeviation().getFields();

		for(int i=0; i<data.getFields().length; i++) {
			Double variance = Math.pow(standardDeviation[i], 2);
			Double difference = sample[i]-mean[i];

			value += Math.exp(-Math.pow(difference, 2) /(2 * variance))/(Math.sqrt(2 * Math.PI) * standardDeviation[i]);
		}

		return value;
	}
}
