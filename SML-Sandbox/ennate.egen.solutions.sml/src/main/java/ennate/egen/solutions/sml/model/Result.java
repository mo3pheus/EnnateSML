package ennate.egen.solutions.sml.model;

public class Result {
	private double confidence;
	private String classId;
	private Data sample;

	public Data getSample() {
		return sample;
	}

	public void setSample(Data sample) {
		this.sample = sample;
	}

	public double getConfidence() {
		return confidence;
	}
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
}
