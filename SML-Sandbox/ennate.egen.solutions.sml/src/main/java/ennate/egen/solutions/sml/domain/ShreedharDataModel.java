package ennate.egen.solutions.sml.domain;

import java.util.List;

public final class ShreedharDataModel {

    private final int numberOfFields;
    private final Data mean;
    private final Data standardDeviation;
    private String	classId;

    public ShreedharDataModel(List<Data> trainingData, int numberOfFields, String classId) {
        this.numberOfFields = numberOfFields;
        this.mean = new Data(numberOfFields);
        this. standardDeviation = new Data(numberOfFields);

        Double[] meanFields = Utils.initializeArray(new Double[numberOfFields]);
        Double[] stdDevFields = Utils.initializeArray(new Double[numberOfFields]);

        // Begin Calculating mean
        for (Data data : trainingData) {
            if(meanFields.length != data.getFields().length) {
                throw new RuntimeException("Data fields length is not equal to the mean fields");
            }

            Double[] dataFields = data.getFields();
            for(int i=0; i<dataFields.length; i++) {
                meanFields[i] += dataFields[i];
            }
        }

        for (int i=0; i<meanFields.length; i++) {
            meanFields[i] /= trainingData.size();
        }
        this.mean.setFields(meanFields);
        // End Calculating mean

        // Begin Calculating standard deviation
        for (Data data : trainingData) {
            if(stdDevFields.length != data.getFields().length) {
                throw new RuntimeException("Data fields length is not equal to the stdDevFields length");
            }

            Double[] dataFields = data.getFields();
            for(int i=0; i<dataFields.length; i++) {
                stdDevFields[i] += ((dataFields[i] - meanFields[i]) * (dataFields[i] - meanFields[i]));
            }
        }

        for(int i=0; i<stdDevFields.length; i++) {
            stdDevFields[i] =  stdDevFields[i] / trainingData.size();
            stdDevFields[i] = Math.sqrt(stdDevFields[i]);
        }

        this.standardDeviation.setFields(stdDevFields);
        // End Calculating standard deviation

        // Set classId
        this.classId = classId;
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public Data getMean() {
        return mean;
    }

    public Data getStandardDeviation() {
        return standardDeviation;
    }

    public String getClassId() {
        return classId;
    }

    @Override
    public String toString() {
        return "ShreedharDataModel{" +
                "numberOfFields=" + numberOfFields + "\n"+
                ", mean = [" + Utils.printArray(mean.getFields()) +"]"+ "\n" +
                ", stdDev = [" + Utils.printArray(standardDeviation.getFields()) +"]"+ "\n" +
                ", classId='" + classId + '\'' +
                '}' + "\n" ;
    }
}
