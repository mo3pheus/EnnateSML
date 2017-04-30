package egen.solutions.ennate.egen.solutions.sml.driver;

import ennate.egen.solutions.sml.utils.LexicalAnalyzerUtils;

public class LexicalAnalyzer {

    public static void main(String[] args) {
        String sentenceA = "Lorem Ipsum is simply dummy text of the printing and typesetting industry.";
        String sentenceB = "Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book.";

        Double angle = LexicalAnalyzerUtils.compareSentences(sentenceA, sentenceB);
        System.out.println("angle ==> " + angle);
    }
}
