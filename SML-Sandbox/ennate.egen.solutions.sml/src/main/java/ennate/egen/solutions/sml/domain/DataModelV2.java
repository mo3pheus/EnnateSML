package ennate.egen.solutions.sml.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataModelV2 {
	private String classId;
	private List<Data> data;
	private final int noOfFields;
	private Data standardDeviation;
	private Data mean;

	public DataModelV2() {
		this.data = new ArrayList<Data>();
		this.noOfFields = 0;
		this.classId = "";
	}

	public DataModelV2(List<Data>data, int numberOfFields) {
		this.data = Optional.ofNullable(data).orElse(new ArrayList<Data>());
		this.noOfFields = numberOfFields;
		this.classId = Optional.ofNullable(data.get(0).getClassId()).orElse("");
		calculateMean();
		calculateStandardDeviation();
	}

	private void calculateMean() {
		this.mean = new Data(this.noOfFields);
		Double[] meanOfFields = new Double[noOfFields];

		/**
		 * Initialize values
		 */
		for(int i = 0; i < noOfFields; i++ ) {
			meanOfFields[i] = 0.0d;
		}


		/**
		 * Summation
		 */
		for(int j = 0; j < data.size(); j++) {
			Double[] dataFields = data.get(j).getFields();

			for(int i = 0; i < noOfFields; i++ ) {
				meanOfFields[i] += dataFields[i];
			}
		}

		/**
		 * Mean calculation
		 */
		for(int i = 0; i < noOfFields; i++ ) {
			meanOfFields[i] = meanOfFields[i]/(data.size() - 1);
		}

		this.mean.setFields(meanOfFields);
		this.mean.setClassId(classId);
	}

	private void calculateStandardDeviation() {
		this.standardDeviation = new Data(this.noOfFields);
		Double[] standardDeviationFields = new Double[noOfFields];

		/**
		 * Initialization
		 */
		for(int i = 0; i < noOfFields; i++ ) {
			standardDeviationFields[i] = 0.0d;
		}


		/**
		 * evaluate Summation of (X-mean)(X-mean)
		 */
		for(int j = 0; j < data.size(); j++) {
			Double[] dataFields = data.get(j).getFields();

			for(int i = 0; i < noOfFields; i++ ) {
				standardDeviationFields[i] += (dataFields[i] - mean.getFields()[i]) * (dataFields[i] - mean.getFields()[i]);
			}
		}

		for(int i = 0; i < noOfFields; i++ ) {
			standardDeviationFields[i] = standardDeviationFields[i]/(data.size() - 1);
			standardDeviationFields[i] = Math.sqrt(standardDeviationFields[i]);
		}

		this.standardDeviation.setFields(standardDeviationFields);
		this.standardDeviation.setClassId(classId);
	}

	public Data getStandardDeviation() {
		return standardDeviation;
	}

	public void setStandardDeviation(Data standardDeviation) {
		this.standardDeviation = standardDeviation;
	}

	public Data getMean() {
		return mean;
	}

	public void setMean(Data mean) {
		this.mean = mean;
	}

	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}

	public String toString() {
		return " Number of Fields = " + noOfFields + " Mean Vector => " + mean.toString() + " StdDev Vector => "
				+ standardDeviation.toString();
	}
}
