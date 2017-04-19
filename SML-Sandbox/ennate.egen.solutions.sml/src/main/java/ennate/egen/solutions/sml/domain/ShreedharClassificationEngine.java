package ennate.egen.solutions.sml.domain;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.*;

public class ShreedharClassificationEngine extends ClassificationEngine {

    private final Map<String, ShreedharDataModel> DATA_MODELS = new HashMap<>();

    @Override
    public void buildModels(ArrayList<Data> trainingData, int numberOfFields) {
        Map<String, List<Data>> classMap = getClassMap(trainingData);

        classMap
                .entrySet()
                .forEach(entry -> {
                    if (Objects.nonNull(entry.getValue())) {
                        DATA_MODELS.putIfAbsent(entry.getKey(), new ShreedharDataModel(entry.getValue(), numberOfFields, entry.getKey()));
                    } else {
                        throw new RuntimeException(String.format("ClassId: [%s] does not have any value: [%s]", entry.getKey(), entry.getValue()));
                    }
                });
    }

    @Override
    public Result classify(Data sample) {
        Result result = new Result();

        ShreedharDataModel finalEntry = null;
        String finalClassId = "empty";
        Double confidence = Double.MIN_VALUE;

        for (Entry<String, ShreedharDataModel> entry : DATA_MODELS.entrySet()) {
            Double[] meanFields = entry.getValue().getMean().getFields();
            Double[] stdDevFields = entry.getValue().getStandardDeviation().getFields();
            Double[] sampleFields = sample.getFields();

            Double pdfScore = 0.0d;
            for (int i = 0; i < sampleFields.length; i++) {
                pdfScore += calculateDistancePDF(sampleFields[i], meanFields[i], stdDevFields[i]);
            }

            if (pdfScore > confidence) {
                confidence = pdfScore;
                finalClassId = entry.getKey();
                finalEntry = entry.getValue();
            }
        }

        printLine("Sample = " + sample.toString());
        printLine("Model = " + (finalEntry != null ? finalEntry.toString() : null));
        printLine("PDF Score = " + confidence);
        printLine("------------------------------------------------------");

        result.setClassId(finalClassId);
        result.setConfidence(confidence);
        return result;
    }

    private Double calculateDistancePDF(Double value, Double mean, Double stdDev) {
        Double numerator = -1.0d * Math.pow((value - mean), 2);
        Double valueToExp = numerator / (2.0d * (Math.pow(stdDev, 2)));
        Double exp = Math.exp(valueToExp);
        Double divisor =  Math.sqrt(2.0d * Math.PI * (Math.pow(stdDev, 2)));
        Double distance = exp / divisor;

        return distance;
    }

    private Map<String, List<Data>> getClassMap(List<Data> traingingData) {
        Map<String, List<Data>> classIdMap = new HashMap<>();

        traingingData.forEach(data -> {
           List<Data> dataList;

           if(Objects.nonNull(dataList = classIdMap.get(data.getClassId()))) {
               dataList.add(data);
           } else {
               classIdMap.putIfAbsent(data.getClassId(), new ArrayList<>());
           }
        });

        return classIdMap;
    }

    @Override
    public String toString() {
        return "{ DATA_MODELS = [" + printModels() + "] }";
    }

    private String printModels() {
        StringBuilder builder = new StringBuilder();

         this.DATA_MODELS.entrySet().forEach(entry -> {
            builder.append(String.format("ClassId: [%s] ", entry.getKey()))
                    .append(String.format("Data: [%s] ", entry.getValue()))
                    .append("\n");
        });

       return builder.toString();
    }
}
