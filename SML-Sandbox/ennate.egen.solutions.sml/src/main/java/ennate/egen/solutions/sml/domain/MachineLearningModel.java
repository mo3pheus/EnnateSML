package ennate.egen.solutions.sml.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MachineLearningModel {
    private int numberOfFields;
    private List<Model> modelTrainingData;
    private Model model;

    private Double[] meanData;
    private Double[] stdDeviationData;

    public MachineLearningModel(ArrayList<Model> modelTrainingData, int numberOfFields) {
        this.numberOfFields = numberOfFields;
        this.modelTrainingData = modelTrainingData;
        this.meanData = new Double[numberOfFields];
        this.stdDeviationData = new Double[numberOfFields];

        constructMLModel(modelTrainingData, numberOfFields);
    }

    /**
     * This method takes takes the training data and construct the model by calculating mean and std deviation arrays
     * @param modelTrainingData
     * @param numberOfFields
     */
    private void constructMLModel(List<Model> modelTrainingData, int numberOfFields) {

        for (int columnIndex = 0; columnIndex < numberOfFields; columnIndex++) {
            int n = 0;
            Double sumofValues = 0D;

            for (int rowIndex = 0; rowIndex < modelTrainingData.size(); rowIndex++) {
                sumofValues += modelTrainingData.get(rowIndex).getMeasurements()[columnIndex];
                n++;
            }

            final Double meanValueForColumnIndex = sumofValues/(n - 1);
            meanData[columnIndex] = meanValueForColumnIndex;
        }

        for (int columnIndex = 0; columnIndex < numberOfFields; columnIndex++) {
            int n = 0;
            Double sumofStdDeviations = 0D;

            for (int rowIndex = 0; rowIndex < modelTrainingData.size(); rowIndex++) {
                Double x = modelTrainingData.get(rowIndex).getMeasurements()[columnIndex];
                Double stdDev = x - meanData[columnIndex];
                sumofStdDeviations += (stdDev * stdDev);

                n++;
            }

            final Double stdDeviationsForColumnIndex = sumofStdDeviations/(n - 1);
            stdDeviationData[columnIndex] = stdDeviationsForColumnIndex;
        }
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public List<Model> getModelTrainingData() {
        return modelTrainingData;
    }

    public Model getModel() {
        return model;
    }

    public Double[] getMeanData() {
        return meanData;
    }

    public Double[] getStdDeviationData() {
        return stdDeviationData;
    }

    @Override
    public String toString() {
        return "MachineLearningModel{" +
                "numberOfFields=" + numberOfFields +
                ", modelTrainingData=" + modelTrainingData +
                ", model=" + model +
                ", meanData=" + Arrays.toString(meanData) +
                ", stdDeviationData=" + Arrays.toString(stdDeviationData) +
                '}';
    }
}
