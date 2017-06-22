/**
 * 
 */
package egen.solutions.ennate.egen.solutions.sml.driver;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ennate.egen.solutions.sml.domain.Data;
import ennate.egen.solutions.sml.etl.KesavaUtil;

/**
 * @author ktalabattula
 *	K-Means clustering algorithm
 */
public class KMeansCluster {
	public static final Random random = new Random();
	
	public static void main(String[] args) {
		String fileName;
		String delimiter;
		int noOfFields;
		
		if (args.length != 3) {
			try{
				fileName = args[0];
				delimiter = args[1];
				noOfFields = Integer.parseInt(args[2]);
			}
			catch (Exception e) {
				System.out.println("Wrong parameters, executing with default parameters");
				fileName = "iris.data.txt";
				delimiter = ",";
				noOfFields = 4;
			}
		} else {
			fileName = "iris.data.txt";
			delimiter = ",";
			noOfFields = 4;
		}
		
		SanketML machineLearning = new SanketML();
		loadData(fileName, delimiter, noOfFields, machineLearning);
		
		KesavaUtil.evaluatePDF(noOfFields, machineLearning);
		Map<Data, List<Data>> clusters = KesavaUtil.evaluateKClusterWithNClusters(noOfFields, machineLearning, random, 3);
	}
	
	private static void loadData(String fileName, String delimiter, int noOfFields, SanketML preVersion) {
		try {
			preVersion.loadData(fileName, delimiter, noOfFields);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
