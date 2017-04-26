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
	 * evaluates Sigmoid function for the given input and returns the processed output
	 * 
	 * Sigmoid function f(x) = 1/(1 + e^(-x))
	 * @param input
	 * @return processed output after evaluating sigmoid function
	 * @throws DivideByZeroException 
	 */
	public static double evaluateSigmoid(double input) throws IllegalArgumentException {
		double value = 1.0d + 1.0d/Math.exp(input);
		
		if (value != 0) {
			return 1.0d/value;
		} else {
			throw new IllegalArgumentException("Divide by zero exception occurred");
		}
		
	}
}
