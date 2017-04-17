package ennate.egen.solutions.sml.domain;

import java.util.ArrayList;

public interface Classifier {
	void buildModels(ArrayList<Model> trainingData, int numberOfFields);
	Result classify(Model sample);
}
