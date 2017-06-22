/**
 * 
 */
package ennate.egen.solutions.sml.domain;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

/**
 * @author ktalabattula
 *
 */
public class NeuralNetworkEngine {
	
	private double[] inputNodes;
	private double[] hiddenNodes;
	private double[] hiddenNodesDerivative;
	private double[] outputNodes;
	private double[] outputNodesDerivative;
	private double[] errorVector;
	private double errorMagnitude;
	private double[] biases;
	private double[][] inputToHiddenWeights;
	private double[][] hiddenToOutputWeights;
	private Random random;
	private Method activationFunction;
	private Method activationFunctionDerivate;
	private double[] expectedOutput;

	public NeuralNetworkEngine(int noOfInputNodes, int noOfHiddenNodes, int noOfOutputNodes, Method activationFunction, Method activationFunctionDerivate) {
		inputNodes = new double[noOfInputNodes];
		hiddenNodes = new double[noOfHiddenNodes];
		hiddenNodesDerivative = new double[noOfHiddenNodes];
		outputNodes = new double[noOfOutputNodes];
		outputNodesDerivative = new double[noOfOutputNodes];
		biases = new double[2];
		inputToHiddenWeights = new double[noOfInputNodes][noOfHiddenNodes];
		hiddenToOutputWeights = new double[noOfHiddenNodes][noOfOutputNodes];
		random = new Random();
		this.activationFunction = activationFunction;
		this.activationFunctionDerivate = activationFunctionDerivate;
		
		initialize();
	}
	
	
	/**
	 * Initializes all weights and biases to some random values between 0, 1 (both exclusive)
	 */
	private void initialize() {
		for (int i = 0; i < inputNodes.length; i++) {
			for (int j = 0; j < hiddenNodes.length; j++) {
				inputToHiddenWeights[i][j] = random.nextDouble();
			}
		}
		
		for (int i = 0; i < hiddenNodes.length; i++) {
			for (int j = 0; j < outputNodes.length; j++) {
				hiddenToOutputWeights[i][j] = random.nextDouble();
			}
		}
		
		biases[0] = random.nextDouble();
		biases[1] = random.nextDouble();
	}


	/**
	 * Executes one run for the given input values by computing the inputs to hidden nodes
	 * and evaluating the output of hidden nodes using the provided activation function and computes 
	 * the values of the output nodes
	 * @param values of inputNodes
	 * @return values of outputNodes
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public double[] forwardPass(double[] inputNodeValues) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Object[] parameters = new Object[1];
		errorMagnitude = 0;
		
		for (int i = 0; i < hiddenNodes.length; i++) {
			double value = 0.0d;
			for (int j = 0; j < inputNodes.length; j++) {
				value += inputNodes[j] * inputToHiddenWeights[j][i];
			}
			value += biases[0];
			parameters[0] = value;
			hiddenNodes[i] = (Double) activationFunction.invoke(null, parameters);
			hiddenNodesDerivative[i] = (Double) activationFunctionDerivate.invoke(null, parameters);
		}
		
		for (int i = 0; i < outputNodes.length; i++) {
			double value = 0.0d;
			for (int j = 0; j < hiddenNodes.length; j++) {
				value += hiddenNodes[j] * hiddenToOutputWeights[j][i];
			}
			value += biases[1];
			parameters[0] = value;
			outputNodes[i] = (Double) activationFunction.invoke(null, parameters);
			outputNodesDerivative[i] = (Double) activationFunctionDerivate.invoke(null, parameters);
			errorVector[i] = expectedOutput[i] - outputNodes[i];
			errorMagnitude += 0.5*errorVector[i]*errorVector[i]; 
		}
		
		return outputNodes;
	}
	
	public void adjustWeights() {
		for (int j = 0; j < hiddenNodes.length; j++) {
			int i = 0;
			double value = hiddenNodes[j] * hiddenToOutputWeights[j][i];
		}
	}
	
	/**
	 * @param index
	 * @return array of values of weights for paths from input node to all of the hidden nodes 
	 */
	public double[] getWeightsOfPathsFromInputNode(int index) {
		return inputToHiddenWeights[index];
	}
	
	/**
	 * 
	 * @param index
	 * @return array of values of weights for paths from hidden node to all of the output nodes 
	 */
	public double[] getWeightsOfPathsFromHiddenNode(int index) {
		return hiddenToOutputWeights[index];
	}
	
	/**
	 * @return values of output from the output nodes from previous pass 
	 */
	public double[] getOutputNodes() {
		return outputNodes;
	}

	/**
	 * @return value of biases for hidden and output layers
	 */
	public double[] getBiases() {
		return biases;
	}

	/**
	 * @param inputNodes : values of input nodes as an array
	 */
	public void setInputNodes(double[] inputNodes) {
		this.inputNodes = inputNodes;
	}
	
	//TODO : remove below. 
	/**
	 * In true NN, biases and weights are random and weights are updated based on each run
	 */
	

	public void setBiases(double[] biases) {
		this.biases = biases;
	}


	public void setInputToHiddenWeights(double[][] inputToHiddenWeights) {
		this.inputToHiddenWeights = inputToHiddenWeights;
	}


	public void setHiddenToOutputWeights(double[][] hiddenToOutputWeights) {
		this.hiddenToOutputWeights = hiddenToOutputWeights;
	}
}
