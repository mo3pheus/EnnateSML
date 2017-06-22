package egen.solutions.ennate.egen.solutions.sml.driver;

import java.io.IOException;
import java.util.*;

/**
 * Created by jmalakalapalli on 4/29/17.
 */
public class MovieRecommendationEngine {

    private static HashMap<String, ArrayList<String>> trainingGenreToMovieMap;
    private static HashMap<String, ArrayList<String>> testingGenreToMovieMap;
    private static ArrayList<String> horrorMovies;
    private static ArrayList<String> comedyMovies;
    private static ArrayList<String> actionMovies;
    private static ArrayList<String> dramaMovies;
    private static ArrayList<String> romanceMovies;
    private static final int NO_OF_BATCHES = 1;
    private static final ArrayList<String> alreadyTestedMovies = new ArrayList<>();
    private static int total_action_movies = 0, total_comedy_movies = 0, total_horror_movies = 0, total_drama_movies = 0, total_romance_movies = 0;
    private static int total_liked_movies = 0;
    private static int total_action_liked = 0, total_comedy_liked = 0, total_horror_liked = 0, total_drama_liked = 0, total_romance_liked = 0;
    private static int MAX_WEIGHT = 10;
    private static double[][] m;
    public static void main(String[] args) {

        HashMap<String, String> movieDataset;
        SanketML irisProblem = new SanketML();
        try {
            irisProblem.loadMoviesData("movies.csv", ",");
        } catch (IOException e) {
            e.printStackTrace();
        }

        irisProblem.populateTrainTestMovieSets(95);
        movieDataset = irisProblem.getMovieDataset();
        trainingGenreToMovieMap = irisProblem.getTrainingGenreToMovie();
        testingGenreToMovieMap = irisProblem.getTestingGenreToMovie();


        // Generate 10 batches of 10 movies each


        // Generate 10 batches of 10 movies each

        horrorMovies = trainingGenreToMovieMap.get("Horror");
        comedyMovies = trainingGenreToMovieMap.get("Comedy");
        dramaMovies = trainingGenreToMovieMap.get("Drama");
        actionMovies = trainingGenreToMovieMap.get("Action");
        romanceMovies = trainingGenreToMovieMap.get("Romance");
        int i = 0;


        System.out.println(String.format("Welcome to Movie Recommendation Engine. In the following we will show you %1d movies", 10 * NO_OF_BATCHES));
        System.out.println("Please like or dislike them. 1 for like, 0 for dislike");
        Scanner sc = new Scanner(System.in);
        do {
            List<String> moviesToBeTested = new ArrayList<>();

            String [] twoHorrorMovies = getTwoRandomMovies(horrorMovies);
            moviesToBeTested.addAll(Arrays.asList(twoHorrorMovies));

            String [] twoComedyMovies = getTwoRandomMovies(comedyMovies);
            moviesToBeTested.addAll(Arrays.asList(twoComedyMovies));

            String [] twoActionMovies = getTwoRandomMovies(actionMovies);
            moviesToBeTested.addAll(Arrays.asList(twoActionMovies));

            String [] twoDramaMovies = getTwoRandomMovies(dramaMovies);
            moviesToBeTested.addAll(Arrays.asList(twoDramaMovies));

            String [] twoRomanceMovies = getTwoRandomMovies(romanceMovies);
            moviesToBeTested.addAll(Arrays.asList(twoRomanceMovies));

            Collections.shuffle(moviesToBeTested);
            moviesToBeTested.stream().forEach(movie -> {
                System.out.println(movie);
                int liked = sc.nextInt();
                if (liked == 1) {
                    total_liked_movies++;
                }
                switch (movieDataset.get(movie)) {
                    case "Horror" :
                        total_horror_movies++;
                        if (liked == 1) {
                            total_horror_liked++;
                        }
                        break;
                    case "Comedy" :
                        total_comedy_movies++;
                        if (liked == 1) {
                            total_comedy_liked++;
                        }
                        break;
                    case "Action" :
                        total_action_movies++;
                        if (liked == 1) {
                            total_action_liked++;
                        }
                        break;
                    case "Drama" :
                        total_drama_movies++;
                        if (liked == 1) {
                            total_drama_liked++;
                        }
                        break;
                    case "Romance" :
                        total_romance_movies++;
                        if (liked == 1) {
                            total_romance_liked++;
                        }
                        break;
                    default:
                        break;
                }
            });
            i++;
        } while (i < NO_OF_BATCHES);

        computeProbabilities();

    }

    private static void computeProbabilities() {
        // P(B)
        double pbOfMovieBeingLiked = (double) total_liked_movies / 10 * NO_OF_BATCHES;

        // P(A/B)
        double pbOfActionMovieBeingLiked ;
        double pbOfRomanceMovieBeingLiked;
        double pbOfDramaMovieBeingLiked;
        double pbOfComedyMovieBeingLiked;
        double pbOfHorrorMovieBeingLiked;

        pbOfActionMovieBeingLiked = (double) total_action_liked / total_liked_movies;
        pbOfRomanceMovieBeingLiked = (double) total_romance_liked / total_liked_movies;
        pbOfDramaMovieBeingLiked = (double) total_drama_liked / total_liked_movies;
        pbOfComedyMovieBeingLiked = (double) total_comedy_liked / total_liked_movies;
        pbOfHorrorMovieBeingLiked = (double) total_horror_liked / total_liked_movies;


        // P(A)
        double pbOfMovieBeingAction = (double) total_action_movies / 10 * NO_OF_BATCHES;
        double pbfOfMovieBeingRomance = (double) total_romance_movies / 10 * NO_OF_BATCHES;
        double pbOfMovieBeingDrama = (double) total_drama_movies / 10 * NO_OF_BATCHES;
        double pbOfMovieBeingComedy = (double) total_comedy_movies / 10 * NO_OF_BATCHES;
        double pbOfMovieBeingHorror = (double) total_horror_movies / 10 * NO_OF_BATCHES;

        // P(B/A)

        double pbOfMovieLikedIsAction = (pbOfActionMovieBeingLiked *  pbOfMovieBeingLiked) / pbOfMovieBeingAction;
        double pbOfMovieLikedIsRomance = (pbOfRomanceMovieBeingLiked *  pbOfMovieBeingLiked) / pbfOfMovieBeingRomance;
        double pbOfMovieLikedIsDrama = (pbOfDramaMovieBeingLiked *  pbOfMovieBeingLiked) / pbOfMovieBeingDrama;
        double pbOfMovieLikedIsComedy = (pbOfComedyMovieBeingLiked *  pbOfMovieBeingLiked) / pbOfMovieBeingComedy;
        double pbOfMovieLikedIsHorror = (pbOfHorrorMovieBeingLiked *  pbOfMovieBeingLiked) / pbOfMovieBeingHorror;

        System.out.println("The conditional probability of a user liking a recommendation from the action movies is " + pbOfMovieLikedIsAction);
        System.out.println("The conditional probability of a user liking a recommendation from the romance movies is " + pbOfMovieLikedIsRomance);
        System.out.println("The conditional probability of a user liking a recommendation from the drama movies is " + pbOfMovieLikedIsDrama);
        System.out.println("The conditional probability of a user liking a recommendation from the comedy movies is " + pbOfMovieLikedIsComedy);
        System.out.println("The conditional probability of a user liking a recommendation from the horror movies is " + pbOfMovieLikedIsHorror);

        List<String> movies = new ArrayList<>();
        movies.addAll(testingGenreToMovieMap.get("Action"));
        movies.addAll(testingGenreToMovieMap.get("Romance"));
        movies.addAll(testingGenreToMovieMap.get("Drama"));
        movies.addAll(testingGenreToMovieMap.get("Comedy"));
        movies.addAll(testingGenreToMovieMap.get("Horror"));


        List<Double> values = new ArrayList<>();
        for (int i = 0; i < testingGenreToMovieMap.get("Action").size(); i++) {
            values.add(pbOfMovieLikedIsAction);
        }
        for (int i = 0; i < testingGenreToMovieMap.get("Romance").size(); i++) {
            values.add(pbOfMovieLikedIsRomance);
        }
        for (int i = 0; i < testingGenreToMovieMap.get("Drama").size(); i++) {
            values.add(pbOfMovieBeingDrama);
        }
        for (int i = 0; i < testingGenreToMovieMap.get("Comedy").size(); i++) {
            values.add(pbOfMovieLikedIsComedy);
        }
        for (int i = 0; i < testingGenreToMovieMap.get("Horror").size(); i++) {
            values.add(pbOfMovieLikedIsHorror);
        }

        m = new double[movies.size() + 1][MAX_WEIGHT+1];
        computeKnapsackTable(values);
        List<String> moviesToBeRecommended = getMoviesToBeRecommended(movies);
        System.out.println("Here are the movies you may like based on your history");
        moviesToBeRecommended.stream()
                .forEach(System.out::println);

    }

    private static String[] getTwoRandomMovies(ArrayList<String> movies) {
        Random random = new Random();
        String[] randomMovies = new String[2];
        int i = 0;
        while (i < 2) {
            int k = random.nextInt(movies.size());
            if (!alreadyTestedMovies.contains(movies.get(k))){
                randomMovies[i] = movies.get(k);
                alreadyTestedMovies.add(randomMovies[i]);
                i++;
            }
        }

        return randomMovies;
    }

    public static void computeKnapsackTable(List<Double> values) {
        for (int i = 1; i < values.size() + 1; i++) {
            for (int j = 0; j < MAX_WEIGHT + 1; j++) {
                if (1 > j) {
                    m[i][j] = m[i-1][j];
                } else {
                    m[i][j] = Double.max(m[i-1][j], m[i-1][j - 1] + values.get(i-1));
                }
            }
        }
    }

    public static List<String> getMoviesToBeRecommended(List<String> movies) {
        int j = MAX_WEIGHT;
        int i = movies.size();
        List<String> moviesToBeRecommended = new ArrayList<>();
        while (i > 0) {
            if (m[i][j] > m[i-1][j]) {
                moviesToBeRecommended.add(movies.get(i-1));
                j = j - 1;
            }
            i--;
        }

        return moviesToBeRecommended;
    }

}
