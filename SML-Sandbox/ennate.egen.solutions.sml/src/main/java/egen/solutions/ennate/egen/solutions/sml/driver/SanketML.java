package egen.solutions.ennate.egen.solutions.sml.driver;

import ennate.egen.solutions.sml.domain.ClassificationEngine;
import ennate.egen.solutions.sml.domain.ClusteringEngine;

public class SanketML extends MachineLearningOperations<ClassificationEngine>{

	@Override
	public ClassificationEngine getClassificationEngine() {
		return classificationEngine;
	}

	@Override
	public void setClassificationEngine(ClassificationEngine classificationEngine) {
		this.classificationEngine = classificationEngine;
	}

	@Override
	public ClusteringEngine getClusterer() {
		return clusteringEngine;
	}

	@Override
	public void setClusterer(ClusteringEngine clusteringEngine) {
		this.clusteringEngine = clusteringEngine;
	}
}
