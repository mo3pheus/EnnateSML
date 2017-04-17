/**
 * 
 */
package egen.solutions.ennate.egen.solutions.sml.driver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import ennate.egen.solutions.sml.domain.ClassificationEngine;
import ennate.egen.solutions.sml.domain.Data;

/**
 * @author ktalabattula
 * Multivariate Guassian Distribution helper
 */
public class MGD {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String fileName;
		String delimiter;
		int noOfFields;
		
		/*
		 * Instantiate the Machine Learning framework
		 */
		
		if (args.length != 3) {
			try{
				fileName = args[0];
				delimiter = args[1];
				noOfFields = Integer.parseInt(args[2]);
			}
			catch (Exception e) {
				System.out.println("Wrong parameters, executing with default parameters");
				fileName = "iris.data.txt";
				delimiter = ",";
				noOfFields = 4;
			}
		} else {
			fileName = "iris.data.txt";
			delimiter = ",";
			noOfFields = 4;
		}
		
		evaluateMGD(fileName, delimiter, noOfFields);
	}

	private static void evaluateMGD(String fileName, String delimiter, int noOfFields) {
		/**
		 * classify the dataset
		 */
		SanketML preVersion = new SanketML();
		
		System.out.println("Initial Classification");
		classifyDataset(fileName, delimiter, noOfFields, preVersion);
		
		ArrayList<Data> totalDataset = preVersion.getTotalData();
		
		double[][] covariance = calculateCovariance(delimiter, totalDataset, noOfFields);
		
		System.out.println("*******************************************************************************************");
		System.out.println("Covariance Matrix");
		printArray(covariance, noOfFields, noOfFields);
		Matrix covarianceMatrix = new Matrix(covariance);
		EigenvalueDecomposition ed = new EigenvalueDecomposition(covarianceMatrix);
		double[] ev = ed.getRealEigenvalues();
		System.out.println("*******************************************************************************************");
		System.out.println("Eigen Values");
		System.out.println();
		System.out.println(Arrays.toString(ev));
		System.out.println("*******************************************************************************************");
		
		int[] dimensionsToRetain = evaluateDimensionsToRetail(ev);
		System.out.println("Dimensions picked");
		System.out.println();
		System.out.println(Arrays.toString(dimensionsToRetain));
		
		System.out.println("*******************************************************************************************");
		System.out.println("Selecting only relevant fields");
	
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String path = new MGD().getClass().getClassLoader().getResource(fileName).getPath();
		System.out.println(path);
		String newFileName = dateFormat.format(new Date());
		System.out.println(newFileName);
		createNewDataFile(path.substring(0, path.length() - fileName.length()) + newFileName, totalDataset, dimensionsToRetain);
		
		System.out.println("*******************************************************************************************");
		System.out.println("Secondary Classification");
		SanketML version2 = new SanketML();
		classifyDataset(newFileName, delimiter, 2, version2);
		System.out.println("*******************************************************************************************");
	}

	private static String createNewDataFile(String fileName, ArrayList<Data> totalDataset, int[] dimensionsToRetain) {
		BufferedWriter bw = null;
		FileWriter fw = null;

		try {
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			
			for(Data data: totalDataset) {
				Double[] fields = data.getFields();
				bw.write(fields[dimensionsToRetain[0]] + "," + fields[dimensionsToRetain[1]] + "," + data.getClassId() + "\n");
			}


		} catch (Exception e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}
		
		return null;
	}

	private static int[] evaluateDimensionsToRetail(double[] ev) {
		int size = ev.length;
		int[] result = new int[2];
		result[0] = 0;
		result[1] = 1;
		for(int j = 2; j < size; j++) {
			if(ev[result[0]] < ev[j]) {
				if(ev[result[1]] < ev[result[0]]) {
					result[1] = result[0];
				}
				result[0] = j;
			} else if(ev[result[1]] < ev[j]) {
				result[1] = j;
			}
		}
		
		return result;
	}

	private static void printArray(double[][] covariance, int xFields, int yFields) {
		System.out.println();
		for(int i = 0; i < xFields; i++) {
			for(int j = 0; j < xFields; j++) {
				System.out.print(covariance[i][j]);
				System.out.print("    ");
			}
			System.out.println();
		}
	}

	private static double[][] calculateCovariance(String delimiter, ArrayList<Data> totalDataset, int noOfFields) {
		Double[] mean = new Double[noOfFields];
		Double[] variance = new Double[noOfFields];
		double[][] covariance = new double[noOfFields][noOfFields];
		
		for(int i = 0; i < noOfFields; i++) {
			mean[i] = 0.0d;
			variance[i] = 0.0d;
			for(int j = 0; j < noOfFields; j++) {
				covariance[i][j] = 0.0d;
			}
		}
		
		for (int i = 0; i < totalDataset.size(); i++) {
			Double[] fields = totalDataset.get(i).getFields();
			for(int j = 0; j < noOfFields; j++) {
				mean[j] += (fields[j] / totalDataset.size());
			}
		}
		
		for(int i = 0; i < noOfFields; i++ ) {
			for(int j = 0; j < noOfFields; j++ ) {
				if (i > j) {
					covariance[i][j] = covariance[j][i];
				} else {
					for(int k = 0; k < totalDataset.size(); k++) {
						Double[] dataFields = totalDataset.get(j).getFields();
						covariance[i][j] += ((dataFields[i] - mean[i]) * (dataFields[j] - mean[j]) / totalDataset.size());
					}
				}
			}
		}
		
		return covariance;
	}

	private static void classifyDataset(String fileName, String delimiter, int noOfFields, SanketML preVersion) {
		try {
			preVersion.loadData(fileName, delimiter, noOfFields);
		} catch (IOException e) {
			e.printStackTrace();
		}
		preVersion.populateTrainTestSets(80);
		
		System.out.println(" Number of training samples = " + preVersion.getTrainingData().size());
		System.out.println(" Number of testing samples = " + preVersion.getTestingData().size());
		
		ClassificationEngine classificationEngine = new ClassificationEngine();
		classificationEngine.buildModelsV2(preVersion.getTrainingData(), noOfFields);
		
		ClassificationEngine.setDebugMode(true);
		preVersion.setClassificationEngine(classificationEngine);
		System.out.println("Accuracy Percentage = " + preVersion.getAccuracyV2() + " % ");
	}

}
