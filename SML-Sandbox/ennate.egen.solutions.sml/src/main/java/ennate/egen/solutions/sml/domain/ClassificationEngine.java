package ennate.egen.solutions.sml.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ClassificationEngine implements Classifier{
	
	private ArrayList<DataModel> models;

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
	
	public ArrayList<DataModel> getModles(){
		return models;
	}

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
	
	private Map<String, Integer> getClassMap(ArrayList<Data> data) {
		Map<String, Integer> classMap = new HashMap<String, Integer>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getClassId() != null) {
				classMap.put(data.get(i).getClassId(), 1);
			}
		}

		return classMap;
	}
	
	public static double getDistance(Data sample, DataModel model) {
		double distance = -1.0d;

		for (int i = 0; i < sample.getFields().length; i++) {
			distance += (((sample.getFields()[i] - model.getMean().getFields()[i]) / model.getStdDev().getFields()[i])
					* ((sample.getFields()[i] - model.getMean().getFields()[i]) / model.getStdDev().getFields()[i]));
		}
		distance = Math.sqrt(distance);

		return Math.pow(Math.E, (-1.0d * distance));
	}
}
