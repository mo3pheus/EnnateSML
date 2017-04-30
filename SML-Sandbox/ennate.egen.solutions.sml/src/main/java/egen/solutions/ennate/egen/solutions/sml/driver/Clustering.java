package egen.solutions.ennate.egen.solutions.sml.driver;

import ennate.egen.solutions.sml.domain.Cluster;
import ennate.egen.solutions.sml.model.Data;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Clustering {
    public static void main(String[] args) {
        SanketML machineLearning = new SanketML();
        try {
            machineLearning.loadData("iris.data.txt", ",", 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HashMap<Data, ArrayList<Data>> centroidMap = Cluster.clusterData(machineLearning.getTotalDataset(), 3);

        for(Data centroid : centroidMap.keySet()) {
            System.out.println("centroid ==> " + centroid.toString());
            System.out.println("classId ==> " + centroid.getClassId());
            System.out.println("member size ==> " + centroidMap.get(centroid).size());
            for(Data member :  centroidMap.get(centroid)) {
                System.out.println(member.toString());
            }
            System.out.println("======================================================================================");
        }

        System.out.println("Cost ==> " + Cluster.getCost(centroidMap, 4));
    }
}
