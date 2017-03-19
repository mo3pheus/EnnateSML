package ennate.egen.solutions.sml.etl;

import java.util.List;

import ennate.egen.solutions.sml.domain.Data;

public class PCAUtil {

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
}
