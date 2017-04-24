package egen.solutions.ennate.egen.solutions.sml;

import java.io.IOException;
import java.util.Arrays;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import egen.solutions.ennate.egen.solutions.sml.driver.SanketML;
import ennate.egen.solutions.sml.domain.Utils;

public class CovarianceTest {

	public static void main(String[] args) {
		try {
			SanketML mlMonk = new SanketML();
			mlMonk.loadData("iris.data.txt",",", 4);
			mlMonk.populateTrainTestSets(80);

			double[][] covarianceMatrix = Utils.computeCovarianceMatrix(mlMonk.getTotalDataset(), 4);
			System.out.println("=============================================================");
			System.out.println("Covariance Matrix:: ");
			System.out.println("=============================================================");
			printArrays(covarianceMatrix);

			Matrix matrix = new Matrix(covarianceMatrix);
			System.out.println("=============================================================");
			System.out.println("EigenValueDecomposition:: ");
			System.out.println("=============================================================");
			EigenvalueDecomposition ed = new EigenvalueDecomposition(matrix);
			System.out.println(Arrays.toString(ed.getRealEigenvalues()));

		} catch (IOException io) {
			System.out.println(io.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printArrays(double[][] arr) {
		for (int i = 0; i < arr.length; i++) {
			for (int j = 0; j < arr[i].length; j++) {
				System.out.print(arr[i][j] + " | ");
			}
			System.out.println();
		}
	}
}
