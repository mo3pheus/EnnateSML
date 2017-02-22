package ennate.egen.solutions.sml.domain;

import java.util.ArrayList;

public class DataModel {
	private int numberOfFields;
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

	public DataModel(int numberOfFields) {
		mean = new Data(numberOfFields);
		stdDev = new Data(numberOfFields);
		classId = "";
	}
	
	public String toString(){
		return " Number of Fields = " + numberOfFields 
				+ " Mean Vector => " + mean.toString()
				+ " StdDev Vector => " + stdDev.toString();
	}

	/**
	 * This constructor takes a list of samples as data points and builds a
	 * statistical model of the data - with mean and standard deviation.
	 * 
	 * @param data
	 */
	public DataModel(ArrayList<Data> data, int numberOfFields) {
		this.numberOfFields = numberOfFields;
		
		/*
		 * Get class id and sanity check
		 */
		if (data.isEmpty()) {
			System.out.println("Data is empty.");
			return;
		} else {
			classId = data.get(0).getClassId();
		}

		/*
		 * Compute the mean and stdDev
		 */
		mean = new Data(numberOfFields);
		stdDev = new Data(numberOfFields);

		// compute the aggregate
		Double[] mFields = new Double[numberOfFields];
		for(int j = 0; j < numberOfFields; j++){
			mFields[j] = 0.0d;
		}
		
		for (int i = 0; i < data.size(); i++) {
			Data sample = data.get(i);
			Double[] fields = sample.getFields();
			for (int j = 0; j < fields.length; j++) {
				mFields[j] += fields[j];
			}
		}
		
		// compute the mean
		for (int j = 0; j < numberOfFields; j++) {
			mFields[j] /= ((double) data.size());
		}
		mean.setFields(mFields);

		// compute aggregate difference from mean squared
		Double[] stdDevFields = new Double[this.numberOfFields];
		for(int j = 0; j < numberOfFields; j++){
			stdDevFields[j] = 0.0d;
		}
		
		for (int i = 0; i < data.size(); i++) {
			Data sample = data.get(i);
			Double[] fields = sample.getFields();
			// for every field of the sample
			for (int j = 0; j < fields.length; j++) {
				stdDevFields[j] += (fields[j] - mean.getFields()[j]) * (fields[j] - mean.getFields()[j]);
			}
		}

		// compute the std dev
		for (int j = 0; j < this.numberOfFields; j++) {
			stdDevFields[j] /= (double) data.size();
			stdDevFields[j] = Math.sqrt(stdDevFields[j]);
		}
		
		stdDev.setFields(stdDevFields);
	}
}

