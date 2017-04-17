package ennate.egen.solutions.sml.domain;

import java.util.Arrays;

public class Model {
    private Double[] measurements;
    private String classifierName;

    public Model(Double[] measurements, String classifierName) {
        this.measurements = measurements;
        this.classifierName = classifierName;
    }

    public String getClassifierName() {
        return classifierName;
    }

    public Double[] getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Double[] measurements) {
        this.measurements = measurements;
    }

    public void setClassifierName(String classifierName) {
        this.classifierName = classifierName;
    }

    @Override
    public String toString() {
        return "Model{" +
                "measurements=" + Arrays.toString(measurements) +
                ", classifierName='" + classifierName + '\'' +
                '}';
    }
}
