/**
 * 
 */
package egen.solutions.ennate.egen.solutions.sml.driver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ennate.egen.solutions.sml.domain.ActivationFunctions;
import ennate.egen.solutions.sml.domain.NeuralNetworkEngine;
import ennate.egen.solutions.sml.etl.CommonUtil;

/**
 * @author ktalabattula
 *
 */
public class NeuralNetworkDriver {

	/**
	 * @param args
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		
		try {
			Class[] parameterTypes = new Class[1];
	        parameterTypes[0] = double.class;
			Method sigmoidFunction = ActivationFunctions.class.getMethod("evaluateSigmoid", parameterTypes);
			NeuralNetworkEngine nnEngine = new NeuralNetworkEngine(2, 2, 2, sigmoidFunction);
			double[] inputValues = new double[]{0.05, 0.10};
			double[][] inputToHiddenWeights = new double[][]{{0.15, 0.20}, {0.25, 0.30}};
			double[][] hiddenToOutputWeights = new double[][]{{0.4, 0.45}, {0.5, 0.55}};
			double[] biases = new double[]{0.35, 0.60};
			nnEngine.setInputNodes(inputValues);
			nnEngine.setBiases(biases);
			nnEngine.setInputToHiddenWeights(inputToHiddenWeights);
			nnEngine.setHiddenToOutputWeights(hiddenToOutputWeights);
			
			System.out.println("*******************************************************************************************");
			System.out.println("**************************Evaluating Neural Network engine*********************************");
			double[] outputValues = nnEngine.forwardPass(inputValues);
			CommonUtil.printArray(outputValues);
			System.out.println("*******************************************************************************************");
			
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

}
