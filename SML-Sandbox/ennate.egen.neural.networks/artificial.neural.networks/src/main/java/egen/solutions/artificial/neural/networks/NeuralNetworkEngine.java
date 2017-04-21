package egen.solutions.artificial.neural.networks;

import java.util.Properties;
import java.util.Random;

public class NeuralNetworkEngine {
	private static final int	BIAS				= 1;
	private Properties			networkConfig		= null;
	private double[]			inputVector			= null;
	private double[]			outputVector		= null;
	private double[]			hiddenOutputVector	= null;
	private double[][]			weightIHMatrix		= null;
	private double[][]			weightHOMatrix		= null;
	private int					numInputNeurons;
	private int					numHiddenNeurons;
	private int					numOutputNeurons;
	private double				learningRate;
	private double				momentum;
	private double				backPropError;

	public NeuralNetworkEngine(int numInputNeurons, int numHiddenNeurons, int numOutputNeurons) {
		this.numInputNeurons = numInputNeurons;
		this.numHiddenNeurons = numHiddenNeurons;
		this.numOutputNeurons = numOutputNeurons;

		this.weightIHMatrix = new double[numInputNeurons + BIAS][numHiddenNeurons];
		this.weightHOMatrix = new double[numHiddenNeurons + BIAS][numOutputNeurons];
		this.hiddenOutputVector = new double[numHiddenNeurons];
		this.inputVector = new double[numInputNeurons];
		this.outputVector = new double[numOutputNeurons];
		seedWeightMatrices();
	}

	public void calculateOutput(double[] input, double[] expectedOutput) {
		if (input.length != numInputNeurons || expectedOutput.length != numOutputNeurons) {
			System.out.println("Input and output vectors are incompatible with the network config. Please adhere to "
					+ numInputNeurons + " inputNeurons and " + numOutputNeurons + " outputNeurons.");
			return;
		}
		inputVector = input.clone();
		calculateHiddenLayerOp();
		calculateOutputLayerOp();
		backPropError = NeuralNetworkEngine.calculateMeanSquaredError(outputVector, expectedOutput);
	}

	private void calculateHiddenLayerOp() {
		double[] hidInputVector = new double[numHiddenNeurons];
		double[] weights = new double[numInputNeurons + BIAS];

		/* calculate input to hidden layer */
		for (int i = 0; i < numHiddenNeurons; i++) {

			for (int j = 0; j < weights.length; j++) {
				weights[j] = weightIHMatrix[j][i];
			}
			hidInputVector[i] = calcDotProduct(inputVector, weights);
			hidInputVector[i] += weights[numInputNeurons + BIAS];
		}

		/* calculate output of hidden layer */
		for (int i = 0; i < numHiddenNeurons; i++) {
			hiddenOutputVector[i] = calcSigmoidActivation(hidInputVector[i]);
		}
	}

	private void calculateOutputLayerOp() {
		double[] opInputVector = new double[numHiddenNeurons];
		double[] weights = new double[numHiddenNeurons + BIAS];

		/* calculate input to output layer */
		for (int i = 0; i < numOutputNeurons; i++) {
			for (int j = 0; j < weights.length; j++) {
				weights[j] = weightHOMatrix[j][i];
			}
			opInputVector[i] = calcDotProduct(hiddenOutputVector, weights);
			opInputVector[i] += weights[numHiddenNeurons + BIAS];
		}

		/* calculate output of output layer */
		for (int i = 0; i < numOutputNeurons; i++) {
			outputVector[i] = calcSigmoidActivation(opInputVector[i]);
		}
	}
	
	private void adjustWIH(){
		
	}
	
	private void adjustWHO(){
		
	}

	/**
	 * NOTE: vecA.vecB != vecB.vecA if the length of the two vectors is not the
	 * same. Well, this is actually supposed to blow up if the lengths are not
	 * the same.
	 * 
	 * @param vecA
	 * @param vecB
	 * @return
	 */
	public static double calcDotProduct(double[] vecA, double[] vecB) {
		double op = 0.0d;
		for (int i = 0; i < vecA.length; i++) {
			op += (vecA[i] * vecB[i]);
		}
		return op;
	}

	public static double calcSigmoidActivation(double x) {
		return 1.0d / (1.0d + Math.exp(-1.0d * x));
	}

	public static double calcSigmoidDifferntial(double x) {
		double fx = calcSigmoidActivation(x);
		return fx * (1.0d - fx);
	}

	public static double calculateMeanSquaredError(double[] x1, double[] x2) {
		double meanError = 0.0d;
		for (int i = 0; i < x1.length; i++) {
			meanError += Math.pow((x1[i] - x2[i]), 2.0d);
		}
		meanError = meanError / (double) x1.length;
		return meanError;
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

	public void configureNeuralNetwork(Properties configProperties) {
		this.networkConfig = configProperties;
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
