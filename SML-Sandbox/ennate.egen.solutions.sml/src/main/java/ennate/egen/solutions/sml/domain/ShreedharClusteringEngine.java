package ennate.egen.solutions.sml.domain;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static java.util.Map.Entry;

public class ShreedharClusteringEngine extends ClusteringEngine {

    public static final double ERR_MARGIN = 0.0001d;
    public static final int MAX_ITERATIONS = 1000;

    @Override
    public Map<Data, ClusteredPoints> clusterData(List<Data> data, int numClusters) throws Exception {

        Random random = new Random();
        Map<Integer, List<Data>> clusterBucket = new HashMap<Integer, List<Data>>();
        List<Data> clusterMean = new ArrayList<Data>();

        /**
         * Pick three random dataPoints from data
         */
        for(int i=0; i < numClusters; i++) {
            int index = random.nextInt((data.size() - 1));
            Data dataPoint = data.get(index);

            dataPoint.setClassNumber(i);
            clusterMean.add(dataPoint);
            clusterBucket.put(i, new ArrayList<>());
        }

        Double error;
        int i = 0;
        List<Data> oldMean = new ArrayList<>();
        do {
            i++;
            reset(clusterBucket);

            for(Data dataPoint : data) {
                Data closestMean = calculateClosestMean(dataPoint, clusterMean);
                clusterBucket.get(closestMean.getClassNumber()).add(dataPoint);
            }

            oldMean.clear();
            for(Data mean : clusterMean) {
                //Clone the old mean objects to avoid being overridden later
                oldMean.add(mean.clone());
            }

            //Calculate new Mean for all clusters
            clusterMean.clear();
            Iterator<Entry<Integer, List<Data>>> iterator = clusterBucket.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<Integer, List<Data>> entry = iterator.next();
                Data newMean = computeMeanForTheCluster(entry.getKey(), entry.getValue());
                clusterMean.add(newMean);
            }

            error = calculateMovement(oldMean, clusterMean);

        } while (error > ERR_MARGIN && i <= MAX_ITERATIONS);


        /*
		 * Compose the result
		 */
        Map<Data, ClusteredPoints> result = new HashMap<Data, ClusteredPoints>();
        Iterator<Entry<Integer, List<Data>>> iterator = clusterBucket.entrySet().iterator();
        int j = 0;
        while (iterator.hasNext()) {
            Entry<Integer, List<Data>> entry = iterator.next();

            ClusteredPoints clusteredPoints = new ClusteredPoints();
            clusteredPoints.setClassId(String.valueOf(entry.getKey()));
            clusteredPoints.setPoints(entry.getValue());
            result.put(clusterMean.get(j), clusteredPoints);
            j++;
        }

        return result;
    }

    private Data calculateClosestMean(final Data datapoint, List<Data> clusterMean) {
        Double min = Double.MAX_VALUE;
        Data closestMean = null;

        if(datapoint == null || clusterMean == null || clusterMean.size() < 1) {
            throw new RuntimeException("Something wrong in the data point -" +datapoint+ " and clusterMean - "+ clusterMean);
        }

        for(Data mean : clusterMean) {
            Double distance = calculateDistance(datapoint, mean);
            if(distance < min) {
                min = distance;
                closestMean = mean;
            }
        }

        return closestMean;
    }

    private Double calculateDistance(Data source, Data target) {
        Double summation = 0.0d;

        if(source.getFields().length != target.getFields().length) {
            throw new RuntimeException("number of fields mismatch between" + source + " and " + target);
        }

        for(int i=0; i<source.getFields().length; i++) {
            Double difference = source.getFields()[i] - target.getFields()[i];
            summation += Math.pow(difference, 2.0d);
        }

        Double distance = Math.sqrt(summation);

        return distance;
    }

    private Data computeMeanForTheCluster(Integer classNumber, List<Data> cluster) {
        int length = cluster.get(0).getFields().length;
        Double[] fields = initiate(new Double[length]);

        for(Data dataPoint : cluster) {
            for(int i=0; i<dataPoint.getFields().length; i++) {
                Double[] dataPointFields = dataPoint.getFields();
                fields[i] += dataPointFields[i];
            }
        }

        for(int j=0; j<fields.length; j++) {
            fields[j] /= cluster.size();
        }

        Data newMean = new Data(fields.length);
        newMean.setFields(fields);
        newMean.setClassNumber(classNumber);

        return newMean;
    }

    private Double calculateMovement(List<Data> oldMean, List<Data> clusterMean) {
        Double distance = 0.0d;

        for(int i=0; i<oldMean.size(); i++) {
            distance += calculateDistance(oldMean.get(i), clusterMean.get(i));
        }

        return distance;
    }

    private Double[] initiate(Double[] fields) {
        for(int i=0; i<fields.length; i++) {
            fields[i] = 0.0d;
        }
        return fields;
    }

    private void reset(Map<Integer, List<Data>> clusterBucket) {
        for (Entry<Integer, List<Data>> entry : clusterBucket.entrySet()) {
            entry.getValue().clear();
        }
    }

}
