package egen.solutions.ennate.egen.solutions.sml.driver;

public class IrisDriver {
	
	/*
	 * Ennate Labs - modernize the legacy software.
	 * 
	 * microservices - speed and scalability. 
	 * 4 packages - diagnostic package, productize the service offering, 30 second pitch.
	 * IT services company. 
	 * 
	 * Design sprint - is it a viable idea - beer recommendation algorithm.
	 * 
	 */

	public static void main(String[] args) {
		DataOperations irisProblem = new DataOperations();
		irisProblem.loadArrayData("iris.data.txt", 4);
		irisProblem.populateTrainTestSets(80);

		System.out.println(" Number of training samples = " + irisProblem.getTrainingData().size());
		System.out.println(" Number of testing samples = " + irisProblem.getTestingData().size());

		irisProblem.buildGaussianModel();
		System.out.println("Accuracy Percentage = " + irisProblem.getAccuracy() + " %");
	}

	public static void main1(String[] args) {
		DataOperations bloodProblem = new DataOperations();

		// Recency (months),Frequency (times),Monetary (c.c. blood),Time
		// (months),"whether he/she donated blood in March 2007"

		bloodProblem.loadArrayData("transfusion.data.txt", 4);
		bloodProblem.populateTrainTestSets(92);
		
		System.out.println(" Number of training samples = " + bloodProblem.getTrainingData().size());
		System.out.println(" Number of testing samples = " + bloodProblem.getTestingData().size());

		bloodProblem.buildGaussianModel();
		System.out.println(bloodProblem.getGd().getModels().size());
		System.out.println(bloodProblem.getAccuracy());
	}
	
	public static void main3(String[] args) {
		DataOperations glassProblem = new DataOperations();

		// Recency (months),Frequency (times),Monetary (c.c. blood),Time
		// (months),"whether he/she donated blood in March 2007"

		glassProblem.loadArrayData("glass.data.csv", 9);
		glassProblem.populateTrainTestSets(90);
		
		System.out.println(" Number of training samples = " + glassProblem.getTrainingData().size());
		System.out.println(" Number of testing samples = " + glassProblem.getTestingData().size());

		glassProblem.buildGaussianModel();
		System.out.println(glassProblem.getGd().getModels().size());
		System.out.println(glassProblem.getAccuracy());
	}
}
