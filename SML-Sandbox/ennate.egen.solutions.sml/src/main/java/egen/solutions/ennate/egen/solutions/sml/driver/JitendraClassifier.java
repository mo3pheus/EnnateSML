package egen.solutions.ennate.egen.solutions.sml.driver;

import ennate.egen.solutions.sml.domain.ClassificationEngine;
import ennate.egen.solutions.sml.domain.Data;
import ennate.egen.solutions.sml.domain.JitendraDataModel;

import java.io.IOException;
import java.util.List;

/**
 * Created by jmalakalapalli on 4/11/17.
 */
public class JitendraClassifier {

    private static List<Data> data;
    private static List<JitendraDataModel> dataModels;

    public static void main(String[] args) {
        SanketML irisProblem = new SanketML();
        try {
            irisProblem.loadData("iris.data.txt", ",", 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = irisProblem.getTotalDataset();

        ClassificationEngine classificationEngine = new ClassificationEngine();
        classificationEngine.buildModels(irisProblem.getTotalDataset(), 4);
        dataModels = classificationEngine.getModles();

        data.stream()
                .map(d -> {
                    System.out.print("The distance of " + d.toString());
                    return dataModels.stream()
                            .map(dataModel -> calculateDistance(d, dataModel))
                            .reduce(Double::min);
                })
                .forEach(distance -> System.out.print(" The closer distance is " + distance.get() + "\n"));

    }

    public static double calculateDistance(Data data, JitendraDataModel dataModel) {
        Double[] mean = dataModel.getMean().getFields();
        double distance = 0.0d;
        Double[] fields = data.getFields();
        for (int j = 0; j < fields.length; j++) {
            distance += (fields[j] - mean[j]) * (fields[j] - mean[j]);
            distance = Math.sqrt(distance);
        }
        System.out.print(" from " + dataModel.getClassId() + " dataModel is " + distance + ";");

        return distance;
    }
}
