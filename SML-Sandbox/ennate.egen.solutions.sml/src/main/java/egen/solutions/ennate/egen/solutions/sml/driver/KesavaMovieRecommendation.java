/**
 * 
 */
package egen.solutions.ennate.egen.solutions.sml.driver;

/**
 * @author ktalabattula
 *	Movie recommendation algorithm
 *	
 *	What does it do?
 *
 *	1. Load all movies & series data from a JSON file
 *	2. Populate a training data set (< 80%) of the total data loaded, and save the rest for testing
 *	3. Generate a set of recommendation sets from the training data set
 *	4. Get feedback from user(s) for each recommendation set for every item in the recommendation set
 *		rewards: 1 - Like the item, 0.5 - Didn't watch, 0 - Doesn't like the item
 *	5. Evaluate conditional probability matrix on the training set
 *	6. Evaluate a Knap-sack algorithm to come up with a new recommendation set from the testing set
 *	   and calculate a reward estimate.
 *	7. Now gather the feedback from the same user for the new recommendation set from testing set,
 *	   calculate the actual reward and check the accuracy percentage of the recommendation engine.
 */
public class KesavaMovieRecommendation {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
