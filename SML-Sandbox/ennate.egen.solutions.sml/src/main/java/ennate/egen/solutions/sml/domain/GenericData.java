package ennate.egen.solutions.sml.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenericData {
	private int numberOfFields;
	private ArrayList<Data> data;
	private ArrayList<DataModel> models;

	public int getNumberOfFields() {
		return numberOfFields;
	}

	public void setNumberOfFields(int numberOfFields) {
		this.numberOfFields = numberOfFields;
	}

	public ArrayList<DataModel> getModels() {
		return models;
	}

	public void setModels(ArrayList<DataModel> models) {
		this.models = models;
	}

	public void setData(ArrayList<Data> data) {
		this.data = data;
	}

	public GenericData(ArrayList<Data> data, int numberOfFields) {
		this.data = data;
		this.numberOfFields = numberOfFields;
	}

	public GenericData(File file, String delimiter, int numberOfFields) throws IOException {
		if (file == null) {
			System.out.println(" File invalid! ");
			return;
		}

		this.numberOfFields = numberOfFields;
		this.data = new ArrayList<Data>();

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = null;
		while ((line = br.readLine()) != null) {
			try {
				data.add(new Data(line, delimiter, numberOfFields));
			} catch (Exception e) {

			}
		}

		br.close();
	}

	public Result classify(Data sample) {
		Result id = new Result();
		double maxProb = Double.MIN_VALUE;
		String classId = "";

		for (int i = 0; i < models.size(); i++) {
			double dist = getDistance(sample, models.get(i));

			if (dist > maxProb) {
				maxProb = dist;
				classId = models.get(i).getClassId();
			}
		}

		id.setClassId(classId);
		id.setConfidence(maxProb);

		return id;
	}

	private Map<String, Integer> getClassMap() {
		Map<String, Integer> classMap = new HashMap<String, Integer>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getClass() != null) {
				classMap.put(data.get(i).getClassId(), 1);
			}
		}

		return classMap;
	}

	private double getDistance(Data sample, DataModel model) {
		double distance = -1.0d;

		for (int i = 0; i < sample.getFields().length; i++) {
			distance += (((sample.getFields()[i] - model.getMean().getFields()[i]) / model.getStdDev().getFields()[i])
					* ((sample.getFields()[i] - model.getMean().getFields()[i]) / model.getStdDev().getFields()[i]));
		}
		distance = Math.sqrt(distance);

		return Math.pow(Math.E, (-1.0d * distance));
	}
	

	/**
	 * Figure out if this is used
	 * 
	 * @param sample1
	 * @param sample2
	 * @return
	 * @throws Exception
	 */
	public static double getDistance(Data sample1, Data sample2) throws Exception {
		double distance = 0.0d;

		if (sample1.fields.length != sample2.fields.length) {
			throw new Exception("Two samples must be of equal dimensions!");
		}

		for (int i = 0; i < sample1.fields.length; i++) {
			distance += ((sample1.fields[i] - sample2.fields[i]) * (sample1.fields[i] - sample2.fields[i]));
		}
		return Math.sqrt(distance);
	}

	public ArrayList<DataModel> buildModels() {
		Map<String, Integer> classMap = getClassMap();
		Object[] classIds = classMap.keySet().toArray();

		ArrayList<DataModel> modelsTemp = new ArrayList<DataModel>();

		for (int i = 0; i < classIds.length; i++) {
			ArrayList<Data> temp = new ArrayList<Data>();
			for (int j = 0; j < data.size(); j++) {
				if ((data.get(j) != null) && (data.get(j).classId != null)
						&& (data.get(j).classId.equals((String) classIds[i]))) {
					temp.add(data.get(j));
				}
			}

			modelsTemp.add(new DataModel(temp));
		}
		return modelsTemp;
	}

	public ArrayList<Data> getData() {
		return data;
	}

}
