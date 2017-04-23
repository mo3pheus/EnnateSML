package ennate.egen.solutions.sml.domain;


import ennate.egen.solutions.sml.domain.Data;

import java.util.ArrayList;
import java.util.Arrays;

public class Model {
    private int numberOfFields;
    private Data mean;
    private Data standardDeviation;
    private String classId;

    public Model(ArrayList<Data> data, int noOfFields) {
        System.out.println("Models");

        if(data.isEmpty()) {
            System.out.println("Data is empty");
            return;
        }
        classId = data.get(0).getClassId();
        numberOfFields = noOfFields;

        System.out.println("classId  ==> " + classId);
        mean = new Data(numberOfFields);
        standardDeviation = new Data(numberOfFields);

        //set classId
        mean.setClassId(classId);
        standardDeviation.setClassId(classId);

        // set mean
        mean.setFields(computMean(data, numberOfFields));

        // set standard deviation
        standardDeviation.setFields(computeStandardDeviation(data, numberOfFields));

        // compute distance
        Double[] distance = computeDistance(data, numberOfFields, mean.getFields());

        System.out.println("distance ==> " + Arrays.toString(distance));

        displayMEanAndStandardDeviation();
        System.out.println("Number of Fields => " + numberOfFields);
        System.out.println("Mean Vector => " + mean.toString());
        System.out.println("StdDev Vector => " + standardDeviation.toString());
        System.out.println("=============================================================================================================================================================================================");
    }

    public int getNumberOfFields() {
        return numberOfFields;
    }

    public void setNumberOfFields(int numberOfFields) {
        this.numberOfFields = numberOfFields;
    }

    public Data getMean() {
        return mean;
    }

    public void setMean(Data mean) {
        this.mean = mean;
    }

    public Data getStandardDeviation() {
        return standardDeviation;
    }

    public void setStandardDeviation(Data standardDeviation) {
        this.standardDeviation = standardDeviation;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Double[] computMean(ArrayList<Data> data, int noOfFields) {
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

    public Double[] computeStandardDeviation(ArrayList<Data> data, int noOfFields) {
        Double[] intermediate = new Double[noOfFields];
        Arrays.fill(intermediate, 0.0);

        for(int i=0; i<data.size(); i++) {
            for(int j=0; j<noOfFields; j++)  {
                intermediate[j] += Math.pow(data.get(i).getFields()[j]-mean.getFields()[j], 2);
            }
        }

        for(int k=0; k<noOfFields; k++) {
            intermediate[k] = Math.sqrt(intermediate[k]/(data.size()-1));
        }

        return intermediate;
    }

    public Double[] computeDistance(ArrayList<Data> data, int noOfFields, Double[] meanValues) {
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

    public String displayMEanAndStandardDeviation() {
        return " Number of Fields = " + numberOfFields + " Mean Vector => " + mean.toString() + " StdDev Vector => "
                + standardDeviation.toString();
    }
}
