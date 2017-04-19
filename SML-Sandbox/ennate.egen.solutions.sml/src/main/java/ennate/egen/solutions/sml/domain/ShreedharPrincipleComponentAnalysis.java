package ennate.egen.solutions.sml.domain;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import java.util.ArrayList;
import java.util.Arrays;

public class ShreedharPrincipleComponentAnalysis {

    private final double[][] covarMatrix;
    private final int numberOfFields;
    private final ArrayList<Data> data;

    public ShreedharPrincipleComponentAnalysis(final ArrayList<Data> data, final int numberOfFields) {
        this.numberOfFields = numberOfFields;
        this.data = data;
        covarMatrix = new double[numberOfFields][numberOfFields];
        initializeCovarianceMatrix(covarMatrix, this.numberOfFields);
    }


    public void computePrincipleComponents() {
        double[] eigenValues = computeEigenValues();

        System.out.print("Eigen values: ");
        Arrays.stream(eigenValues)
                .forEach(e -> System.out.print(e +" "));

         double firstPrincipleComponent = Arrays.stream(eigenValues)
                .max()
                .orElseThrow(() ->  new RuntimeException("Something went seriously wrong !!! "));

         double secondPrincipleComponent
                 = Arrays.stream(eigenValues)
                 .filter(e -> e != firstPrincipleComponent)
                 .max()
                 .orElseThrow(() ->  new RuntimeException("Something went seriously wrong AGAIN !!! "));


        System.out.println(String.format("FirstPrincipleComponent: [%.2f] " +
                "and SecondPrincipleComponent: [%.2f]", firstPrincipleComponent, secondPrincipleComponent));
    }

    private double[] computeEigenValues() {
        populateCovarianceMatrix();
        printCovarianceMatrix();
        Matrix matrix = new Matrix(this.covarMatrix);
        EigenvalueDecomposition decomposition = matrix.eig();
        return decomposition.getRealEigenvalues();
    }

    private void populateCovarianceMatrix() {
        Double[] mean = calculateMean();

        for (int i = 0; i < numberOfFields ; i++) {
            for (int j = 0; j < numberOfFields; j++) {
                covarMatrix[i][j] = computeMatrixValue(i, j, mean);
            }
        }
    }

    private Double computeMatrixValue(int dimension1, int dimension2, Double[] mean) {
        Double summation = 0.0d;
        Double metricValue;

        for(Data dataPoint : data) {
            Double term1 = (dataPoint.getFields()[dimension1] - mean[dimension1]);
            Double term2 = (dataPoint.getFields()[dimension2] - mean[dimension2]);

            summation += (term1 * term2);
        }

        metricValue = summation / data.size();

        return metricValue;
    }

    private Double[] calculateMean() {
        Double[] meanFields = Utils.initializeArray(new Double[numberOfFields]);

        data.forEach(dataPoint -> {
            Double[] dataFields = dataPoint.getFields();

            if(dataFields.length != numberOfFields) {
                throw new RuntimeException(String.format("Data point fields length: [%d] " +
                        "is not equal to number of fields: [%d] ", dataFields.length, numberOfFields));
            }

            for (int i = 0; i < dataFields.length; i++) {
                meanFields[i] += dataFields[i];
            }
        });

        for (int i = 0; i < meanFields.length ; i++) {
            meanFields[i] /= data.size();
        }

        return meanFields;
    }

    private void printCovarianceMatrix() {
        System.out.println("==================================");
        System.out.println("Coviance Matrix: ");
        for (int i = 0; i < numberOfFields; i++) {
            for (int j = 0; j < numberOfFields; j++) {
                System.out.print( String.format("%.2f",covarMatrix[i][j]));
                System.out.print("   ");
            }
            System.out.println();
        }
        System.out.println("==================================");
    }

    private void initializeCovarianceMatrix(double[][] covarMatrix, int numberOfFields) {
        for (int i = 0; i < numberOfFields; i++) {
            for (int j = 0; j < numberOfFields; j++) {
                covarMatrix[i][j] = 0.0d;
            }
        }
    }

}
