package ennate.egen.solutions.sml.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class ClusteringEngine implements IClusterStuff {

	public static final double ERR_MARGIN = 0.0001d;
	public static final int MAX_ITERATIONS = 1000;

	/**
	 * Class to hold clustered set of points. Contains a list of points and the
	 * classId they were classified to.
	 * 
	 * @author sanket korgaonkar
	 *
	 */
	public class ClusteredPoints {
		List<Data> points;
		String classId;

		public ClusteredPoints() {
			points = new ArrayList<Data>();
			classId = "";
		}

		public void setClassId(String id) {
			classId = id;
		}

		public void addToPoints(Data sample) {
			if (sample != null) {
				points.add(sample);
			}
		}

		public List<Data> getPoints() {
			return points;
		}
	}

	/**
	 * This function takes in a list of points and the number of clusters and
	 * applies k-means clustering algorithm to find n-clusters, it returns the
	 * result as a map of the <centroid, clusteredPoints>
	 * 
	 */
	public Map<Data, ClusteredPoints> clusterData(List<Data> data, int numClusters) throws Exception {
		/*
		 * Sanity Check
		 */
		if (data.size() <= numClusters) {
			throw new Exception("Number of clusters is too high. " + numClusters);
		}
		int numFields = data.get(0).getFields().length;

		/*
		 * Local variable
		 */
		ClusteredPoints[] clusteredPoints = new ClusteredPoints[numClusters];

		/*
		 * Pick initial clusterIds
		 */
		int[] seedIds = new int[numClusters];
		initializeArray(seedIds);
		int i = 0;
		while (i < numClusters) {
			int randomNum = ThreadLocalRandom.current().nextInt(0, data.size());
			if (!isPresent(seedIds, randomNum)) {
				seedIds[i] = randomNum;
				i++;
			}
		}

		/*
		 * Populate centroids
		 */
		ArrayList<Data> centroids = new ArrayList<Data>();
		for (i = 0; i < numClusters; i++) {
			Data temp = data.get(seedIds[i]);
			temp.setClassId(Integer.toString(i));
			centroids.add(temp);
		}

		/*
		 * Iterate over the data and cluster points
		 */
		double error = Double.MAX_VALUE;
		int j = 0;
		while ((error > ERR_MARGIN) && (j <= MAX_ITERATIONS)) {
			/* reset clustered points */
			resetClusteredPoints(clusteredPoints);

			/* classify all points */
			for (i = 0; i < data.size(); i++) {
				Data sample = data.get(i);
				if (sample == null || sample.getFields() == null || sample.getClassId() == null) {
					continue;
				}

				try {
					int id = classifySample(sample, centroids);
					Data tmpSample = sample;
					tmpSample.setClassId(Integer.toString(id));
					clusteredPoints[id].addToPoints(tmpSample);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			/*
			 * Save off old centroids and new centroids to compute movement
			 */
			List<Data> oldCentroids = new ArrayList<Data>();
			copyPoints(centroids, oldCentroids);

			/* Recompute new centroids */
			computeCentroids(clusteredPoints, centroids, numFields);

			/* update error and increment counter */
			error = computeMovement(oldCentroids, centroids);
			j++;
		}

		/*
		 * Compose the result
		 */
		Map<Data, ClusteredPoints> result = new HashMap<Data, ClusteredPoints>();
		for (int k = 0; k < clusteredPoints.length; k++) {
			result.put(centroids.get(k), clusteredPoints[k]);
		}

		return result;
	}

	/**
	 * Computes the distance between two points of type Data.
	 * 
	 * @param a
	 * @param b
	 * @return
	 * @throws Exception
	 */
	public static double getDistance(Data a, Data b) throws Exception {
		Double[] fields1 = a.getFields();
		Double[] fields2 = b.getFields();

		if (fields1 == null || fields2 == null) {
			int j = 0;
			j = j + 10;
		}

		if (fields1.length != fields2.length) {
			throw new Exception(
					" Fields for two samples are not consistent " + " A => " + a.toString() + " B=> " + b.toString());
		}

		double distance = 0.0d;
		for (int i = 0; i < fields1.length; i++) {
			distance = Math.pow((fields1[i] - fields2[i]), 2.0d);
		}
		distance = Math.sqrt(distance);

		return distance;
	}

	private void resetClusteredPoints(ClusteredPoints[] clusteredPoints) {
		for (int i = 0; i < clusteredPoints.length; i++) {
			clusteredPoints[i] = new ClusteredPoints();
			clusteredPoints[i].setClassId(Integer.toString(i));
		}
	}

	private double computeMovement(List<Data> oldCentroids, List<Data> newCentroids) throws Exception {
		double distance = 0.0d;

		for (int i = 0; i < oldCentroids.size(); i++) {
			distance += getDistance(oldCentroids.get(i), newCentroids.get(i));
		}

		return distance;
	}

	private void copyPoints(List<Data> a, List<Data> b) {
		b.clear();
		for (int i = 0; i < a.size(); i++) {
			Data sample = a.get(i);
			b.add(sample);
		}
	}

	private void computeCentroids(ClusteredPoints[] clusteredPoints, List<Data> centroids, int numFields) {
		clearCentroids(centroids);
		for (int i = 0; i < clusteredPoints.length; i++) {
			Data centroidPoint = new Data(numFields);
			List<Data> points = clusteredPoints[i].getPoints();
			for (int j = 0; j < points.size(); j++) {
				Data sample = points.get(j);
				addData(sample, centroidPoint);
			}
			averageData(centroidPoint, points.size());
			centroids.add(centroidPoint);
		}
	}

	private void averageData(Data data, int num) {
		Double[] fields = data.getFields();
		for (int i = 0; i < fields.length; i++) {
			fields[i] /= (double) num;
		}
		data.setFields(fields);
	}

	private void addData(Data sample, Data target) {
		Double[] sampleFields = sample.getFields();
		Double[] targetFields = target.getFields();

		if (targetFields.length != sampleFields.length) {
			return;
		}

		for (int i = 0; i < sampleFields.length; i++) {
			targetFields[i] += sampleFields[i];
		}
		target.setFields(targetFields);
	}

	private void clearCentroids(List<Data> centroids) {
		if (centroids.isEmpty()) {
			return;
		}

		centroids.clear();
	}

	private int classifySample(Data sample, List<Data> centroids) throws Exception {
		int classId = 0;
		double minDist = Double.MAX_VALUE;
		for (int i = 0; i < centroids.size(); i++) {
			double distance = getDistance(sample, centroids.get(i));
			if (distance < minDist) {
				minDist = distance;
				classId = i;
			}
		}

		return classId;
	}

	private void initializeArray(int[] x) {
		for (int i = 0; i < x.length; i++) {
			x[i] = 0;
		}
	}

	private boolean isPresent(int[] list, int a) {
		for (int i = 0; i < list.length; i++) {
			if (list[i] == a) {
				return true;
			}
		}
		return false;
	}
}
