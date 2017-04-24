package ennate.egen.solutions.sml.domain;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.util.*;

public class ClassificationEngine implements Classifier {

	private ArrayList<MachineLearningModel> models;

	/**
	 * This function builds models based on the trainingData and the number of
	 * fields.
	 */
	public void buildModels(ArrayList<Model> trainingData, int numberOfFields) {
		models = new ArrayList<MachineLearningModel>();
		Map<String, Integer> classMap = getClassMap(trainingData);
		Object[] classIds = classMap.keySet().toArray();

		for (int i = 0; i < classIds.length; i++) {
			ArrayList<Model> temp = new ArrayList<Model>();
			for (int j = 0; j < trainingData.size(); j++) {
				if ((trainingData.get(j) != null) && (trainingData.get(j).getClassifierName() != null)
						&& (trainingData.get(j).getClassifierName().equals(classIds[i]))) {
					temp.add(trainingData.get(j));
				}
			}
			models.add(new MachineLearningModel(temp, numberOfFields));
		}
	}

	/**
	 * This function returns the built models. If buildModels() is not called.
	 * This will return null.
	 * 
	 * @return
	 */
	public ArrayList<MachineLearningModel> getModles() {
		return models;
	}

	/**
	 * This function lets you classify the given sample.
	 */
	public Result classify(Model sample) {
		Result id = new Result();
		double maxProb = Double.MIN_VALUE;
		String classId = "";
		for (int i = 0; i < models.size(); i++) {
			double dist = ClassificationEngine.getDistance(sample, models.get(i));

			if (dist > maxProb) {
				maxProb = dist;
				classId = models.get(i).getModelTrainingData().get(0).getClassifierName();
			}
		}

        System.out.println("Model predicted it as : " + classId + " and it was: " + sample.getClassifierName());

		id.setClassId(classId);
		id.setConfidence(maxProb);

		return id;
	}

    public void performPrincipalComponentAnalysis(ArrayList<Model> data, int numberOfFields) {
        double[][] covarianceMatrx = new double[numberOfFields][numberOfFields];
        Double[] meanDataForEachColumn = new Double[numberOfFields];

        for (int columnIndex = 0; columnIndex < numberOfFields; columnIndex++) {
            int n = data.size();
            Double sumofValues = 0D;

            for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
                sumofValues += data.get(rowIndex).getMeasurements()[columnIndex];
            }

            final Double meanValueForColumnIndex = sumofValues/(n);
            meanDataForEachColumn[columnIndex] = meanValueForColumnIndex;
        }


        for (int matrixColumnIndex = 0; matrixColumnIndex < numberOfFields; matrixColumnIndex++) {
            for (int matrixRowIndex = 0; matrixRowIndex < numberOfFields; matrixRowIndex++) {
                Double matrixValue = getCovarianceForTwoColumns(data, meanDataForEachColumn, matrixColumnIndex, matrixRowIndex);
                covarianceMatrx[matrixColumnIndex][matrixRowIndex] = matrixValue;
            }
        }

        Matrix matrix = new Matrix(covarianceMatrx);
        EigenvalueDecomposition decomposition = matrix.eig();
        double[] eigenValues = decomposition.getRealEigenvalues();

        for (double eigenValue : eigenValues) {
            System.out.println("Eigen values: " + eigenValue);
        }

        double firstValue = Arrays.stream(eigenValues).max().orElseThrow(() ->  new RuntimeException("That's it, you have failed in life"));
        double secondValue = Arrays.stream(eigenValues).filter(e -> e != firstValue).max().orElseThrow(() ->  new RuntimeException("That's it, you have failed in life"));

        System.out.println("First PCA value: " + firstValue);
        System.out.println("Second PCA value: " + secondValue);

    }

    private Double getCovarianceForTwoColumns(ArrayList<Model> data, Double[] meanData, int columnOne, int columnTwo) {
        Double numerator = 0D;
        for (Model dataRow : data) {
            Double x = dataRow.getMeasurements()[columnOne];
            Double y = dataRow.getMeasurements()[columnTwo];
            numerator += ((x - meanData[columnOne]) * (y - meanData[columnTwo]));
        }

        return numerator/data.size();
    }

    /**
	 * This function gets a distance measure from a sample to the given model.
	 *
	 * @param sample
	 * @param model
	 * @return
	 */
	public static double getDistance(Model sample, MachineLearningModel model) {
		Double distance = 0D;
		for (int i = 0; i < 4; i++) {
			Double sampleMeasurement = sample.getMeasurements()[i];
			Double meanValue = model.getMeanData()[i];
			Double sampleMinusMean = sampleMeasurement - meanValue;
			Double stdDeviation = model.getStdDeviationData()[i];

			Double constant = 1/Math.sqrt(2 * Math.PI * stdDeviation * stdDeviation);
			Double term = (-1 * (sampleMinusMean * sampleMinusMean))/(2 * stdDeviation * stdDeviation);

            distance += constant * Math.exp(term);
		}

		return distance;
	}

	/**
	 * This function returns a map of the data given.
	 *
	 * @param data
	 * @return
	 */
	private Map<String, Integer> getClassMap(ArrayList<Model> data) {
		Map<String, Integer> classMap = new HashMap<String, Integer>();
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).getClassifierName() != null) {
				classMap.put(data.get(i).getClassifierName(), 1);
			}
		}

		return classMap;
	}
}
