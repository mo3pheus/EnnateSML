/**
 * 
 */
package ennate.egen.solutions.sml.etl;

/**
 * @author ktalabattula
 *
 */
public class CommonUtil {

	/**
	 * prints elements in a one dimensional array on a line delimited by " , "
	 * @param double array
	 */
	public static void printArray(double[] arrayValues) {
		if (arrayValues != null) {
			System.out.println();
			System.out.print("[ ");
			for (int i = 0; i < arrayValues.length; i++) {
				if (i == (arrayValues.length - 1)) {
					System.out.print(arrayValues[i]);
				} else {
					System.out.print(arrayValues[i] + " , ");
				}
			}
			System.out.print(" ]");
			System.out.println();
		} else {
			throw new NullPointerException();
		}
	}
}
