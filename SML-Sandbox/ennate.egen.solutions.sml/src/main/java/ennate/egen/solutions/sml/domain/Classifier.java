package ennate.egen.solutions.sml.domain;

import ennate.egen.solutions.sml.model.Data;
import ennate.egen.solutions.sml.model.Result;

import java.util.ArrayList;

public interface Classifier {
	public void buildModels(ArrayList<Data> trainingData, int numberOfFields);
	public Result classify(Data sample);
}
