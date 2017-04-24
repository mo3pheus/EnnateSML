package ennate.egen.solutions.sml.domain;

import java.util.ArrayList;

public class DataModel {
	private int		numberOfFields;
	private Data	mean;
	private Data	stdDev;
	private String	classId;

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

	public String toString() {
		return "ClassId = " + classId + "\nMean Vector => " + mean.toString() + "\nStdDev Vector => "
				+ stdDev.toString();
	}

	/**
	 * This constructor takes a list of samples as data points and builds a
	 * statistical model of the data - with mean and standard deviation.
	 * 
	 * @param data
	 */
	//My Code
	public DataModel(ArrayList<Data> data, int numberOfFields) {
		this.classId = data.get(0).getClassId();
		this.numberOfFields = numberOfFields;

		Double[] sumOfFields = new Double[numberOfFields];
		Double[] meanOfFields = new Double[numberOfFields];
		Double[] stdDeviationOfFields = new Double[numberOfFields];

		for(int i=0; i< sumOfFields.length ; i++) {
			sumOfFields[i] = 0.0d;
			meanOfFields[i] = 0.0d;
			stdDeviationOfFields[i] = 0.0d;
		}

		// compute mean
		for(int i = 0; i< data.size();i++) {
			for(int j=0;j<data.get(i).getFields().length;j++) {
				sumOfFields[j] += data.get(i).getFields()[j];
			}
		}
		for(int i=0; i< sumOfFields.length; i++) {
			meanOfFields[i] = sumOfFields[i] / (data.size());
		}

		mean = new Data(numberOfFields);
		mean.setFields(meanOfFields);
		mean.setClassId(data.get(0).getClassId());

		//compute standard deviation
		for(int i=0;i<data.size();i++) {
			for(int j=0;j<data.get(i).getFields().length;j++) {
				stdDeviationOfFields[j] += Math.pow(meanOfFields[j] - data.get(i).getFields()[j], 2);
			}
		}

		for(int i=0;i<stdDeviationOfFields.length;i++) {
			stdDeviationOfFields[i] = Math.sqrt(stdDeviationOfFields[i] / (data.size()-1));

		}

		stdDev = new Data(numberOfFields);
		stdDev.setFields(stdDeviationOfFields);
		stdDev.setClassId(data.get(0).getClassId());
	}

}
