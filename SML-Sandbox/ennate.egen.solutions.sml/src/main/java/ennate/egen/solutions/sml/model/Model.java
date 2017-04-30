package ennate.egen.solutions.sml.model;


import ennate.egen.solutions.sml.utils.Utils;

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
            System.out.println("model is empty");
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
        mean.setFields(Utils.computMean(data, numberOfFields));

        // set standard deviation
        standardDeviation.setFields(Utils.computeStandardDeviation(data, numberOfFields, mean.getFields()));

        // compute distance
        Double[] distance = Utils.computeDistance(data, numberOfFields, mean.getFields());

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

    public String displayMEanAndStandardDeviation() {
        return " Number of Fields = " + numberOfFields + " Mean Vector => " + mean.toString() + " StdDev Vector => "
                + standardDeviation.toString();
    }
}
