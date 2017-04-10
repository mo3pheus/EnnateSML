package ennate.egen.solutions.sml.etl;

import java.util.ArrayList;
import java.util.List;

import ennate.egen.solutions.sml.domain.Data;

public class PCAUtil {

	/**
	 * Retuns the dataSet in a 2D matrix
	 * 
	 * @param dataSet
	 * @return
	 */
	public static double[][] convertDataSetToMatrix(List<Data> dataSet) {
		if (dataSet.isEmpty()) {
			System.out.println("Dataset is empty!");
			return null;
		}

		int numFields = dataSet.get(0).getFields().length;
		double[][] matrix = new double[dataSet.size()][numFields];

		for (int i = 0; i < dataSet.size(); i++) {
			Data temp = dataSet.get(i);
			for (int j = 0; j < numFields; j++) {
				matrix[i][j] = temp.getFields()[j];
			}
		}
		return matrix;
	}

	/**
	 * This function returns the co-variance matrix for the dataset passed in.
	 * 
	 * @param dataSet
	 * @return
	 * @throws Exception
	 */
	public static double[][] getCovarianceMatrix(List<Data> dataSet) throws Exception {
		if (dataSet == null || dataSet.isEmpty()) {
			throw new Exception(" Dataset passed in was empty!");
		}

		int numFields = dataSet.get(0).getFields().length;
		Data mean = computeMean(dataSet);
		List<Data> adjustedDataSet = new ArrayList<Data>();

		// find (x-xI) for each element
		for (int i = 0; i < dataSet.size(); i++) {
			Double[] fields = dataSet.get(i).getFields();
			Double[] mFields = mean.getFields();
			for (int j = 0; j < numFields; j++) {
				fields[j] -= mFields[j];
			}
			Data temp = new Data(numFields);
			temp.setFields(fields);
			adjustedDataSet.add(temp);
		}

		// for covarMatrix[i][j] = covarMatrix[i][x] * covarMatrix[j][x]
		double[][] covarMatrix = new double[numFields][numFields];
		for (int i = 0; i < numFields; i++) {
			for (int j = 0; j < numFields; j++) {
				Data temp = null;
				for (int k = 0; k < adjustedDataSet.size(); k++) {
					temp = adjustedDataSet.get(k);
					covarMatrix[i][j] += temp.getFields()[i] * temp.getFields()[j];
				}
			}
		}

		// divide the sum by (n-1)
		for (int i = 0; i < numFields; i++) {
			for (int j = 0; j < numFields; j++) {
				covarMatrix[i][j] /= (adjustedDataSet.size() - 1);
			}
		}

		return covarMatrix;
	}

	/**
	 * Retuns the mean for each dimension of a dataset of type Data
	 * 
	 */
	public static Data computeMean(List<Data> dataSet) throws Exception {
		if (dataSet == null || dataSet.isEmpty()) {
			throw new Exception(" Dataset passed in was empty!");
		}

		int numFields = dataSet.get(0).getFields().length;

		Data mean = new Data(numFields);
		Double[] fields = new Double[numFields];
		for (int i = 0; i < numFields; i++) {
			fields[i] = new Double(0.0d);
			for (int j = 0; j < dataSet.size(); j++) {
				fields[i] += dataSet.get(j).getFields()[i];
			}
		}

		for (int i = 0; i < numFields; i++) {
			fields[i] /= dataSet.size();
		}

		mean.setFields(fields);
		return mean;
	}
}
