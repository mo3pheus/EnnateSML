package ennate.egen.solutions.sml.domain;

import java.util.List;
import java.util.Map;

import ennate.egen.solutions.sml.domain.ClusteringEngine.ClusteredPoints;
import ennate.egen.solutions.sml.model.Data;

public interface IClusterStuff {
	public Map<Data,ClusteredPoints> clusterData(List<Data> data, int numClusters) throws Exception;
}
