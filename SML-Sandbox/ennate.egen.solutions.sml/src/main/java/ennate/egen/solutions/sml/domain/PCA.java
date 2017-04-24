package ennate.egen.solutions.sml.domain;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import egen.solutions.ennate.egen.solutions.sml.driver.SanketML;

import java.io.IOException;
import java.util.ArrayList;

public class PCA {
    public static void main(String[] args) {
        SanketML pca = new SanketML();
        try {
            pca.loadData("iris.data.txt",",", 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        pca.populateTrainTestSets(80);

        // Pre PCA

        Double prePCAAccuracy = Utils.classify(pca.getTrainingData(), pca.getTestingData(), 4);

        // PCA
        double[][] covarianceMatrix = Utils.computeCovarianceMatrix(pca.getTotalDataset(), 4);
        Matrix matrix = new Matrix(covarianceMatrix);
        EigenvalueDecomposition ed = new EigenvalueDecomposition(matrix);

        ArrayList<PrincipalComponents>  principalComponents = PCAUtils.assignPrincipalComponents(ed);
        principalComponents = PCAUtils.getHighestPrincipalComponents(principalComponents, 2);
        ArrayList<Data> filteredTestData = PCAUtils.filterDimensions(pca.getTestingData(), principalComponents);
        ArrayList<Data> filteredTrainingData = PCAUtils.filterDimensions(pca.getTrainingData(), principalComponents);

        // Post PCA

        Double postPcaAccuracy =  Utils.classify(filteredTrainingData, filteredTestData, principalComponents.size());

        // Comparison Pre and Post PCA
        System.out.println("==========================================================================================");
        System.out.println("Pre PCA  Accuracy ==> " + prePCAAccuracy + " Post PCA Accuracy ==> " +  postPcaAccuracy);
        System.out.println("==========================================================================================");
    }
}
