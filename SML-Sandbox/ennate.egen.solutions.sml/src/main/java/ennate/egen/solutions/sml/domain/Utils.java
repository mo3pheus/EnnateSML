package ennate.egen.solutions.sml.domain;

import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
    public static Double[] computMean(ArrayList<Data> data, int noOfFields) {
        Double[] intermediate = new Double[noOfFields];
        Arrays.fill(intermediate, 0.0);

        // compute sum per dimension

        for(int i=0; i<data.size(); i++) {
            for(int j=0; j<noOfFields; j++)  {
                intermediate[j] += data.get(i).getFields()[j];
            }
        }

        // compute mean per dimension

        for(int k=0; k<noOfFields; k++) {
            intermediate[k] = intermediate[k]/(data.size()-1);
        }

        return intermediate;
    }

    public static Double[] computeStandardDeviation(ArrayList<Data> data, int noOfFields, Double[] mean) {
        Double[] intermediate = new Double[noOfFields];
        Arrays.fill(intermediate, 0.0);

        for(int i=0; i<data.size(); i++) {
            for(int j=0; j<noOfFields; j++)  {
                intermediate[j] += Math.pow(data.get(i).getFields()[j]-mean[j], 2);
            }
        }

        for(int k=0; k<noOfFields; k++) {
            intermediate[k] = Math.sqrt(intermediate[k]/(data.size()-1));
        }

        return intermediate;
    }

    public static Double[] computeDistance(ArrayList<Data> data, int noOfFields, Double[] meanValues) {
        Double[] distance = new Double[data.size()];
        Arrays.fill(distance, 0.0);

        for(int i=0; i<data.size(); i++) {
            for (int j=0; j<noOfFields; j++) {
                distance[i] += Math.pow(meanValues[j] - data.get(i).getFields()[j], 2);
            }
            distance[i] = Math.sqrt(distance[i]);
        }

        return distance;
    }

    public static double[][] computeCovarianceMatrix(ArrayList<Data> data, int noOfFields) {
        Double[] mean = computMean(data, noOfFields);
        double[][] covarianceMatrix = new double[noOfFields][noOfFields];

        for(int i=0; i<noOfFields; i++) {
            for(int j=0; j<noOfFields; j++) {
                covarianceMatrix[i][j] = 0.0d;
                for(Data sample: data) {
                    covarianceMatrix[i][j] += (sample.getFields()[i] - mean[i]) * (sample.getFields()[j] - mean[j]);
                }
                covarianceMatrix[i][j]/=(data.size()-1);
            }
        }
        return covarianceMatrix;
    }

    public static double getClassificationAccuracy(ArrayList<Result> classifiedData) {
        double accuracy = 0.0;

        for(Result sample : classifiedData) {
            if(sample.getSample().getClassId().equalsIgnoreCase(sample.getClassId())) {
                accuracy++;
            }
        }

        return accuracy * 100 / classifiedData.size();
    }

    public static Double classify(ArrayList<Data> trainingData, ArrayList<Data> testData, int numberOfDimensions) {
        ClassificationEngine classificationEngine = new ClassificationEngine();
        classificationEngine.buildModels(trainingData, numberOfDimensions);

        ArrayList<Result> classifiedData = classificationEngine.classifyData(testData);

        DisplayUtils.displayClassifiedSamples(classifiedData);
        Double accuracy = getClassificationAccuracy(classifiedData);

        System.out.println("Post PCA Classification Accuracy ==> " + accuracy);
        System.out.println();

        return accuracy;
    }
}
