package egen.solutions.ennate.egen.solutions.sml.driver;

import java.io.IOException;

import ennate.egen.solutions.sml.domain.ClassificationEngine;
import ennate.egen.solutions.sml.domain.DataModel;

public class IrisDriver {

	public static void main(String[] args) {
		DataOperations<ClassificationEngine> irisProblem = new DataOperations<ClassificationEngine>();
		try {
			irisProblem.loadData("iris.data.txt", ",", 4);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		irisProblem.populateTrainTestSets(80);

		System.out.println(" Number of training samples = " + irisProblem.getTrainingData().size());
		System.out.println(" Number of testing samples = " + irisProblem.getTestingData().size());
		
		
		/*
		 * This part should be done by the students. Implement your own version of ClassificationEngine
		 * and compare accuracy you get.
		 * 
		 * 1. Instantiate the classificationEngine, 
		 * 2. Report accuracy.
		 */
		ClassificationEngine classificationEngine = new ClassificationEngine();
		classificationEngine.buildModels(irisProblem.getTrainingData(), 4);
		
		for(DataModel model:classificationEngine.getModles()){
			System.out.println(model.toString());
		}
		irisProblem.setClassificationEngine(classificationEngine);

		System.out.println("Accuracy Percentage = " + irisProblem.getAccuracy() + " %");
	}
	
	/*
	 * public static void main1(String[] args) { DataOperations bloodProblem =
	 * new DataOperations();
	 * 
	 * // Recency (months),Frequency (times),Monetary (c.c. blood),Time //
	 * (months),"whether he/she donated blood in March 2007"
	 * 
	 * bloodProblem.loadArrayData("transfusion.data.txt", 4);
	 * bloodProblem.populateTrainTestSets(92);
	 * 
	 * System.out.println(" Number of training samples = " +
	 * bloodProblem.getTrainingData().size());
	 * System.out.println(" Number of testing samples = " +
	 * bloodProblem.getTestingData().size());
	 * 
	 * bloodProblem.buildGaussianModel();
	 * System.out.println(bloodProblem.getGd().getModels().size());
	 * System.out.println(bloodProblem.getAccuracy()); }
	 * 
	 * public static void main3(String[] args) { DataOperations glassProblem =
	 * new DataOperations();
	 * 
	 * // Recency (months),Frequency (times),Monetary (c.c. blood),Time //
	 * (months),"whether he/she donated blood in March 2007"
	 * 
	 * glassProblem.loadArrayData("glass.data.csv", 9);
	 * glassProblem.populateTrainTestSets(90);
	 * 
	 * System.out.println(" Number of training samples = " +
	 * glassProblem.getTrainingData().size());
	 * System.out.println(" Number of testing samples = " +
	 * glassProblem.getTestingData().size());
	 * 
	 * glassProblem.buildGaussianModel();
	 * System.out.println(glassProblem.getGd().getModels().size());
	 * System.out.println(glassProblem.getAccuracy()); }
	 */
}
