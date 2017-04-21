package egen.solutions.artificial.neural.networks;

import java.util.Random;

public class NeuralNetworkEngine {
	private int			numInputNeurons;
	private int			numHiddenNeurons;
	private int			numOutputNeurons;
	private double[][]	weightIHMatrix	= null;
	private double[][]	weightHOMatrix	= null;

	private double		learningRate;
	private double		momentum;

	public NeuralNetworkEngine(int numInputNeurons, int numHiddenNeurons, int numOutputNeurons) {
		this.numInputNeurons = numInputNeurons;
		this.numHiddenNeurons = numHiddenNeurons;
		this.numOutputNeurons = numOutputNeurons;

		this.weightIHMatrix = new double[numInputNeurons + 1][numHiddenNeurons];
		this.weightHOMatrix = new double[numHiddenNeurons + 1][numOutputNeurons];
	}

	public double[][] getWeightIHMatrix() {
		return weightIHMatrix;
	}

	public void setWeightIHMatrix(double[][] weightIHMatrix) {
		this.weightIHMatrix = weightIHMatrix;
	}

	public double[][] getWeightHOMatrix() {
		return weightHOMatrix;
	}

	public void setWeightHOMatrix(double[][] weightHOMatrix) {
		this.weightHOMatrix = weightHOMatrix;
	}

	public double getLearningRate() {
		return learningRate;
	}

	public void setLearningRate(double learningRate) {
		this.learningRate = learningRate;
	}

	public double getMomentum() {
		return momentum;
	}

	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}

	private void seedWeightMatrices() {
		Random random = new Random();
		double rangeMin = 0.0d;
		double rangeMax = 1.0d;

		for (int i = 0; i < numInputNeurons + 1; i++) {
			for (int j = 0; j < numHiddenNeurons + 1; j++) {
				double randomValue = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
				weightIHMatrix[i][j] = randomValue;
			}
		}

		for (int i = 0; i < numHiddenNeurons + 1; i++) {
			for (int j = 0; j < numOutputNeurons + 1; j++) {
				double randomValue = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
				weightHOMatrix[i][j] = randomValue;
			}
		}
	}
}
