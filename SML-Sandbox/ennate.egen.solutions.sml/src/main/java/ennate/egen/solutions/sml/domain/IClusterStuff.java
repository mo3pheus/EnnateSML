package ennate.egen.solutions.sml.domain;

import java.util.List;
import java.util.Map;

import ennate.egen.solutions.sml.domain.Clusterer.ClusteredPoints;

public interface IClusterStuff {
	public Map<Data,ClusteredPoints> clusterData(List<Data> data, int numClusters) throws Exception;
}
