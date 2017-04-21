/**
 * 
 */
package egen.solutions.ennate.egen.solutions.sml.driver;

import java.util.Map;

import ennate.egen.solutions.sml.etl.KesavaUtil;

/**
 * @author ktalabattula
 *
 */
public class LexicalAnalysis {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String string1 = "Mary had a little lamb. Mary had a little lamb.";
		String string2 = "Bob little is actually not so little and eats lamb";
		
		double comparedValue = compareSentences(string1, string2);
		System.out.println(comparedValue);
	}
	
	/**
	 * @param firstString : String one to compare
	 * @param secondString : String two which is compared with string one
	 * @return a percentage match of how both strings are identical to each other (case insensitive comparison)
	 */
	public static double compareSentences(String firstString, String secondString) {
		String[] uniqueWords = KesavaUtil.getUniqueWordsFrom(firstString + " " +  secondString);
		Map<String, Integer> frequencyMapFirst = KesavaUtil.getWordFrequencyMap(uniqueWords, firstString);
		Map<String, Integer> frequencyMapSecond = KesavaUtil.getWordFrequencyMap(uniqueWords, secondString);
		
		Integer scalarMultiplicationResult = KesavaUtil.getScalarProduct(frequencyMapFirst, frequencyMapSecond);
		double magnitude1 = KesavaUtil.getMagnitude(frequencyMapFirst);
		double magnitude2 = KesavaUtil.getMagnitude(frequencyMapSecond);
		return Math.toDegrees(Math.acos(scalarMultiplicationResult * 1.0d / (magnitude1 * magnitude2)));
	}

}
