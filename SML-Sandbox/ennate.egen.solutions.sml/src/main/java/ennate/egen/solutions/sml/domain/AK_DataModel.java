package ennate.egen.solutions.sml.domain;

import lombok.ToString;

import java.util.List;

@ToString
public class AK_DataModel {

    int numberOfFields;
    Data mean;
    Data stdDev;
    private String classId;

    public AK_DataModel(List<Data> data, int numberOfFields) {
        this.numberOfFields = numberOfFields;
        this.mean = new Data(numberOfFields);
        this.stdDev = new Data(numberOfFields);
        Double[] meanFields = setMean(data, numberOfFields);
        setStdDev(data, numberOfFields, meanFields);
        this.classId = data.get(0).getClassId();

    }

    public Double[] setMean(List<Data> data, int numberOfFields) {
        Double[] meanFields = new Double[numberOfFields];
        Double[] dataFields = new Double[numberOfFields];

        for(int i =0; i < numberOfFields; i++){
        for(int j=0; j < data.size(); j++) {
                dataFields = data.get(j).getFields();
                meanFields[i] = meanFields[i] != null ? meanFields[i] + dataFields[i]/data.size(): 0.0d;
            }
        }
        mean.setFields(meanFields);
        return meanFields;
    }

    public  void setStdDev(List<Data> data, int numberOfFields, Double[] meanFields) {
        Double[] stdDevFields = new Double[numberOfFields];
        Double[] dataFields = new Double[numberOfFields];
        for(int i =0; i < numberOfFields; i++){
            for(int j=0; j < data.size(); j++) {
                dataFields = data.get(j).getFields();
                stdDevFields[i] = stdDevFields[i] != null ? stdDevFields[i] + Math.pow((dataFields[i] - meanFields[i]),2)/data.size(): 0.0d;
            }
        }

        for(int i = 0; i < numberOfFields; i++) {
            stdDevFields[i] = Math.sqrt(stdDevFields[i]);
        }

        stdDev.setFields(stdDevFields);
    }
    public Data getStdDev() {
        return stdDev;
    }

    public Data getMean() {
        return mean;
    }

    public String getClassId() {
        return classId;
    }
}
