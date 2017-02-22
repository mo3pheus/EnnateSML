package ennate.egen.solutions.sml.domain;

public interface Classifier {
	public void buildModels();
	public Result classify(Data sample);
}
