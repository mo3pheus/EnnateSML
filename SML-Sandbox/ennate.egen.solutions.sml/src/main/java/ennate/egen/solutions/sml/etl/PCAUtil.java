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

	public static double[][] convertDataSetToMatrixMean(List<Data> dataSet, Double[] mean) {
		if (dataSet.isEmpty()) {
			System.out.println("Dataset is empty!");
			return null;
		}

		int numFields = dataSet.get(0).getFields().length;
		double[][] matrix = new double[dataSet.size()][numFields];

		for (int i = 0; i <= dataSet.size()/2; i++) {
			Data temp1 = dataSet.get(i);
			Data temp2 = dataSet.get(dataSet.size() - 1 - i);
			for (int j = 0; j <= numFields/2; j++) {
				matrix[i][j] = temp1.getFields()[j] - mean[j];
				matrix[i][numFields - 1 - j] = temp1.getFields()[numFields - 1 - j] - mean[numFields - 1 - j];
				matrix[dataSet.size() - 1 - i][j] = temp2.getFields()[j] - mean[j];
				matrix[dataSet.size() - 1 - i][numFields - 1 - j] = temp2.getFields()[numFields - 1 - j] - mean[numFields - 1 - j];
			}
		}
		return matrix;
	}

	public static double[][] buildCovarianceMatrix(double[][] dataMeanMatrix, Double[] stdDev) {
		int numFields = dataMeanMatrix[0].length;
		double[][] covMatrix = new double[numFields][numFields];
		for (int i = 0; i < numFields; i++) {
			covMatrix[i][i] = Math.pow(stdDev[i], 2);
			for (int j = i+1; j < numFields; j++) {
				covMatrix[i][j] = 0;
				for (int k = 0; k < dataMeanMatrix.length; k++) {
					covMatrix[i][j] += dataMeanMatrix[k][i] * dataMeanMatrix[k][j];
				}
				covMatrix[i][j] /= (dataMeanMatrix.length - 1);
				covMatrix[j][i] = covMatrix[i][j];
			}
		}

		return covMatrix;
	}

	public static void printMatrix(double[][] matrix) {
		System.out.println("\nCovariance Matrix: ");
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[0].length; j++) {
				System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("");
	}
}
