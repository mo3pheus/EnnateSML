package ennate.egen.solutions.sml.domain;

import ennate.egen.solutions.sml.model.Data;
import ennate.egen.solutions.sml.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Cluster {
    private static final double ERR_MARGIN = 0.0001d;
    private static final int MAX_ITERATIONS = 1000;

    public static Double getCost(HashMap<Data, ArrayList<Data>> centroidMap, int noOfFields) {
        Double cost = 0.0d;

        for(Data centroid: centroidMap.keySet()) {
            ArrayList<Data> members = centroidMap.get(centroid);
            for(int i=0; i< members.size(); i++) {
                cost += Utils.getDistance(members.get(i).getFields(), centroid.getFields(), noOfFields);
            }
        }

        return cost;
    }

    public static HashMap<Data, ArrayList<Data>> clusterData(ArrayList<Data> data, int numberOfClusters) {
        ArrayList<Data> centroids;
        HashMap<Data, ArrayList<Data>> centroidMap;
        ArrayList<Data> newCentroids = getInitialCentroids(data, numberOfClusters);
        int iteration = 0;
        do {
            centroids = newCentroids;
            centroidMap = clusterData(data, centroids, 4);
            newCentroids = getNewCentroids(centroidMap, 4);
            iteration++;
        }while (shouldContinueClustering(centroids, newCentroids, 4) && iteration < MAX_ITERATIONS);

        return centroidMap;
    }

    private static boolean  shouldContinueClustering(ArrayList<Data> oldCentroids, ArrayList<Data> newCentroids, int noOfFields) {
        boolean shouldContinue = false;
        for(int i=0; i<newCentroids.size(); i++) {
            if(Utils.getDistance(newCentroids.get(i).getFields(), getCorrespondingOldCentroid(newCentroids.get(i), oldCentroids).getFields(), noOfFields) > ERR_MARGIN) {
                shouldContinue = true;
                break;
            }
        }

        return shouldContinue;
    }

    private static Data getCorrespondingOldCentroid(Data newCentroid, ArrayList<Data> oldCentroids) {
        Data correspondingOldCentroid = new Data(newCentroid.getFields().length);
        for(Data centroid : oldCentroids) {
            if(centroid.getClassId().equalsIgnoreCase(newCentroid.getClassId())) {
                correspondingOldCentroid = centroid;
                break;
            }
        }
        return correspondingOldCentroid;
    }

    private static ArrayList<Data> getInitialCentroids(ArrayList<Data> data, int numberOfCentroids) {
        int[] centroidIndices = new int[numberOfCentroids];
        ArrayList<Data> centroids = new ArrayList<Data>();

        int numFilled = 0;
        while (numFilled != numberOfCentroids) {
            int randomNum = ThreadLocalRandom.current().nextInt(0, data.size() + 1);
            if (!alreadyPresent(randomNum, centroidIndices)) {
                centroidIndices[numFilled] = randomNum;
                numFilled++;
            }
        }
        int count = 0;
        for( int index : centroidIndices) {
            Data centroid = new Data(4);
            centroid.setClassId(String.valueOf(count));
            centroid.setFields(data.get(index).getFields());
            centroids.add(centroid);
            count++;
        }
        return centroids;
    }

    private static boolean alreadyPresent(int target, int[] host) {
        boolean present = false;
        for (int i = 0; i < host.length; i++) {
            if (host[i] == target) {
                present = true;
                break;
            }
        }
        return present;
    }

    private static HashMap<Data, ArrayList<Data>> clusterData(ArrayList<Data> data, ArrayList<Data> centroids, int noOfFields) {
        HashMap<Data, ArrayList<Data>> centroidMap = new HashMap<Data, ArrayList<Data>>();
        for(Data centroid: centroids) {
            centroidMap.put(centroid, new ArrayList<Data>());
        }

        for(int i=0; i<data.size(); i++) {
            int centroidNumber = 0;
            double minDistance = Utils.getDistance(data.get(i).getFields(), centroids.get(0).getFields(), noOfFields);

            for(int j=1; j<centroids.size(); j++) {
                Double distance = Utils.getDistance(data.get(i).getFields(), centroids.get(j).getFields(), noOfFields);
                if(minDistance > distance) {
                    centroidNumber = j;
                    minDistance = distance;
                }
            }
            ArrayList<Data> dataMap = centroidMap.get(centroids.get(centroidNumber));
            dataMap.add(data.get(i));
            centroidMap.put(centroids.get(centroidNumber), dataMap);
        }

        return centroidMap;
    }

    private static ArrayList<Data> getNewCentroids(HashMap<Data, ArrayList<Data>> centroidMap, int noOfFields) {
        ArrayList<Data> centroids = new ArrayList<Data>();
        int index = 0;

        for (Data key : centroidMap.keySet()) {
            Data centroid = new Data(noOfFields);
            centroid.setClassId(String.valueOf(index));
            centroid.setFields(Utils.computMean( centroidMap.get(key), noOfFields));
            centroids.add(centroid);
            index++;
        }
        return centroids;
    }
}
