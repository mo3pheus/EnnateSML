package egen.solutions.ennate.egen.solutions.sml.driver;

import ennate.egen.solutions.sml.domain.ClassificationEngine;
import ennate.egen.solutions.sml.domain.Clusterer;

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
	public Clusterer getClusterer() {
		return clusteringEngine;
	}

	@Override
	public void setClusterer(Clusterer clusteringEngine) {
		this.clusteringEngine = clusteringEngine;
	}
}
