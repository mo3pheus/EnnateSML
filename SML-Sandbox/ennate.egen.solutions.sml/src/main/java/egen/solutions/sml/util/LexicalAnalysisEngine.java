/**
 * 
 */
package egen.solutions.sml.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author sanketkorgaonkar
 *
 */
public class LexicalAnalysisEngine {

	public static void main(String[] args) {

		long start = System.currentTimeMillis();
		String sentenceA = "Harish Chetty keeps talking and talking and talking - some call it yapping - but we just calling Chettying.";
		String sentenceB = "Yapping is not a good thing. But calling friends is.";

		System.out.println(compareSentences(sentenceA, sentenceB));
		System.out.println(" Time taken = " + (System.currentTimeMillis() - start));
	}

	private static String sanitize(String x) {
		return x.toLowerCase();
	}

	/**
	 * O(n) = 3n = n - linear complexity
	 * 
	 * @param sentenceA
	 * @param sentenceB
	 * @return
	 */
	private static double compareSentences(String sentenceA, String sentenceB) {
		double theta = 0.0d;

		Map<String, Integer> uniqueWords = new HashMap<String, Integer>();
		Map<String, Integer> sentenceAWords = new HashMap<String, Integer>();
		Map<String, Integer> sentenceBWords = new HashMap<String, Integer>();

		// get unique words
		for (String s : sentenceA.split("\\s")) {
			String sLower = sanitize(s);
			sLower = sLower.replace(".", "");
			System.out.println(sLower);
			uniqueWords.put(sLower, 1);
			if (sentenceAWords.containsKey(sLower)) {
				int count = sentenceAWords.get(sLower);
				sentenceAWords.put(sLower, ++count);
			} else {
				sentenceAWords.put(sLower, 1);
			}
		}

		for (String s : sentenceB.split("\\s")) {
			String sLower = sanitize(s);
			sLower = sLower.replace(".", "");
			System.out.println(sLower);
			uniqueWords.put(sLower, 1);
			if (sentenceBWords.containsKey(sLower)) {
				int count = sentenceBWords.get(sLower);
				sentenceBWords.put(sLower, ++count);
			} else {
				sentenceBWords.put(sLower, 1);
			}
		}

		// build vectors
		int[] vectorA = new int[uniqueWords.keySet().size()];
		int[] vectorB = new int[uniqueWords.keySet().size()];
		int i = 0;
		for (String s : uniqueWords.keySet()) {
			if (sentenceAWords.containsKey(s)) {
				vectorA[i] = sentenceAWords.get(s);
			} else {
				vectorA[i] = 0;
			}

			if (sentenceBWords.containsKey(s)) {
				vectorB[i] = sentenceBWords.get(s);
			} else {
				vectorB[i] = 0;
			}
			i++;
		}

		System.out.println(" Array A = " + Arrays.toString(vectorA));
		System.out.println(" Array B = " + Arrays.toString(vectorB));

		// calculate the angle
		theta = getAngle(vectorA, vectorB);

		return theta;
	}

	private static double getAngle(int[] vectorA, int[] vectorB) {
		double aDotB = 0.0d;

		for (int i = 0; i < vectorA.length; i++) {
			aDotB += vectorA[i] * vectorB[i];
		}

		double magnitudeA = getMagnitude(vectorA);
		double magnitudeB = getMagnitude(vectorB);

		double cosineTheta = aDotB / (magnitudeA * magnitudeB);
		double theta = Math.acos(cosineTheta);

		theta = 180.0d / Math.PI * theta;
		return theta;
	}

	private static double getMagnitude(int[] vector) {

		double sum = 0.0d;
		for (int i = 0; i < vector.length; i++) {
			sum += (vector[i] * vector[i]);
		}

		sum = Math.sqrt(sum);

		return sum;
	}
}
