package ennate.egen.solutions.sml.domain;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GenericData {
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

	private int numberOfFields;

	public class Data {
		private Double[] fields;
		private String classId;

		public Data(String content, String delimiter) throws NumberFormatException {
			String[] parts = content.split(delimiter);

			/*
			 * Sanity check
			 */
			if (parts.length != numberOfFields + 1) {
				System.out.println(
						"Corrupted string passed in" + content + " expected fields length = " + numberOfFields);
				return;
			}

			fields = new Double[numberOfFields];
			int i;
			for (i = 0; i < numberOfFields; i++) {
				fields[i] = Double.parseDouble(parts[i]);
			}
			classId = parts[i];
		}

		public Data() {
			fields = new Double[numberOfFields];

			/*
			 * Initialize the array
			 */
			for (int i = 0; i < numberOfFields; i++) {
				fields[i] = new Double(0.0d);
			}

			classId = "";
		}

		public String toString() {
			StringBuilder dataBuilder = new StringBuilder();
			try {
				for (int i = 0; i < numberOfFields; i++) {
					dataBuilder.append(fields[i]);
					dataBuilder.append(" ");
				}
				dataBuilder.append(classId);
			} catch (Exception e) {

			}
			return dataBuilder.toString();
		}

		public Double[] getFields() {
			return fields;
		}

		public void setFields(Double[] fields) {
			this.fields = fields;
		}

		public String getClassId() {
			return classId;
		}

		public void setClassId(String classId) {
			this.classId = classId;
		}
	}

	private ArrayList<Data> data;
	private ArrayList<DataModel> models;

	public class DataModel {
		private Data mean;
		private Data stdDev;
		private String classId;

		public Data getMean() {
			return mean;
		}

		public void setMean(Data mean) {
			this.mean = mean;
		}

		public Data getStdDev() {
			return stdDev;
		}

		public void setStdDev(Data stdDev) {
			this.stdDev = stdDev;
		}

		public String getClassId() {
			return classId;
		}

		public void setClassId(String classId) {
			this.classId = classId;
		}

		public DataModel() {
			mean = new Data();
			stdDev = new Data();
			classId = "";
		}

		/**
		 * This constructor takes a list of samples as data points and builds a
		 * statistical model of the data - with mean and standard deviation.
		 * 
		 * @param data
		 */
		public DataModel(ArrayList<Data> data) {
			/*
			 * Get class id and sanity check
			 */
			if (data.isEmpty()) {
				System.out.println("Data is empty.");
				return;
			} else {
				classId = data.get(0).classId;
			}

			/*
			 * Compute the mean and stdDev
			 */
			mean = new Data();
			stdDev = new Data();

			// compute the aggregate
			for (int i = 0; i < data.size(); i++) {
				Data sample = data.get(i);
				for (int j = 0; j < sample.fields.length; j++) {
					if (sample != null && sample.fields != null && sample.fields[j] != null)
						mean.fields[j] += sample.fields[j];
				}
			}

			// compute the mean
			for (int j = 0; j < mean.fields.length; j++) {
				mean.fields[j] /= ((double) data.size());
			}

			// compute aggregate difference from mean squared
			for (int i = 0; i < data.size(); i++) {
				Data sample = data.get(i);
				// for every field of the sample
				for (int j = 0; j < sample.fields.length; j++) {
					stdDev.fields[j] += (sample.fields[j] - mean.fields[j]) * (sample.fields[j] - mean.fields[j]);
				}
			}

			// compute the std dev
			for (int j = 0; j < stdDev.fields.length; j++) {
				stdDev.fields[j] /= (double) data.size();
				stdDev.fields[j] = Math.sqrt(stdDev.fields[j]);
			}
		}
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
				data.add(new Data(line, delimiter));
			} catch (Exception e) {

			}
		}

		br.close();
	}

	public GenericData(ArrayList<Data> data, int numberOfFields) {
		this.data = data;
		this.numberOfFields = numberOfFields;
	}

	public Result classify(Data sample) {
		Result id = new Result();
		double maxProb = Double.MIN_VALUE;
		String classId = "";

		for (int i = 0; i < models.size(); i++) {
			double dist = getDistance(sample, models.get(i));

			if (dist > maxProb) {
				maxProb = dist;
				classId = models.get(i).classId;
			}
		}

		id.setClassId(classId);
		id.setConfidence(maxProb);

		return id;
	}

	private double getDistance(Data sample, DataModel model) {
		double distance = -1.0d;

		for (int i = 0; i < sample.fields.length; i++) {
			distance += (((sample.fields[i] - model.mean.fields[i]) / model.getStdDev().fields[i])
					* ((sample.fields[i] - model.mean.fields[i]) / model.getStdDev().fields[i]));
		}
		distance = Math.sqrt(distance);

		return Math.pow(Math.E, (-1.0d * distance));
	}

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

	public void buildModels() {
		Map<String, Integer> classMap = getClassMap();
		Object[] classIds = classMap.keySet().toArray();

		models = new ArrayList<DataModel>();

		for (int i = 0; i < classIds.length; i++) {
			ArrayList<Data> temp = new ArrayList<Data>();
			for (int j = 0; j < data.size(); j++) {
				if ((data.get(j) != null) && (data.get(j).classId != null)
						&& (data.get(j).classId.equals((String) classIds[i]))) {
					temp.add(data.get(j));
				}
			}

			models.add(new DataModel(temp));
		}
	}

	public ArrayList<Data> getData() {
		return data;
	}

	private Map<String, Integer> getClassMap() {
		Map<String, Integer> classMap = new HashMap<String, Integer>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).classId != null) {
				classMap.put(data.get(i).classId, 1);
			}
		}

		return classMap;
	}
}
