package ennate.egen.solutions.sml.domain;

import java.util.ArrayList;
import java.util.Arrays;

public class DisplayUtils {
    public static void displayClassifiedSamples(ArrayList<Result> classifiedData) {
        for(Result sampleData: classifiedData) {
            System.out.println("=============================");
            System.out.println("sample ==> " + Arrays.toString(sampleData.getSample().getFields()));
            System.out.println("sample class Id ==> " + sampleData.getSample().getClassId());
            System.out.println("computed class Id ==> " + sampleData.getClassId());
            System.out.println("pdf score of sample for class computed ==> " + sampleData.getConfidence());
            System.out.println("=============================");
        }
    }
}
