/**
 * 
 */
package ennate.egen.solutions.sml.domain;

/**
 * @author ktalabattula
 *
 */
public class ActivationFunctions {
	
	/**
	 * evaluates Logistic Sigmoid function for the given input and returns the processed output
	 * 
	 * Logistic Sigmoid function f(x) = 1/(1 + e^(-x))
	 * @param input
	 * @return processed output after evaluating Logistic sigmoid function
	 * @throws DivideByZeroException 
	 */
	public static double evaluateLogisticSigmoid(double input) throws IllegalArgumentException {
		double value = 1.0d + 1.0d/Math.exp(input);
		
		if (value != 0) {
			return 1.0d/value;
		} else {
			throw new IllegalArgumentException("Divide by zero exception occurred");
		}
		
	}
	
	/**
	 * evaluates Derivative of Logistic Sigmoid function for the given input and returns the processed output
	 * 
	 * Logistic Sigmoid function f(x) = 1/(1 + e^(-x))
	 * derivative of f(x) = f(x) ( 1 - f(x))  (holds true when f(x) denotes logistic sigmoid function)
	 * @param input
	 * @return processed output after evaluating Derivative of Logistic Sigmoid function
	 * @throws DivideByZeroException 
	 */
	public static double evaluateLogisticSigmoidDerivative(double input) throws IllegalArgumentException {
		double logisticSigmoidvalue = evaluateLogisticSigmoid(input);
		
		return logisticSigmoidvalue*(1 - logisticSigmoidvalue);
	}
}
