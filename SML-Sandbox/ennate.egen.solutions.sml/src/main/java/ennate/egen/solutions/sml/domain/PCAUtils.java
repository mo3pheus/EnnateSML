package ennate.egen.solutions.sml.domain;


import Jama.EigenvalueDecomposition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PCAUtils {
    public static ArrayList<PrincipalComponents> assignPrincipalComponents(EigenvalueDecomposition eigenValues) {
        ArrayList<PrincipalComponents> principalComponents = new ArrayList<PrincipalComponents>();
        for(int i=0; i<eigenValues.getRealEigenvalues().length; i++) {
            principalComponents.add(new PrincipalComponents(i, eigenValues.getRealEigenvalues()[i]));
        }

        return principalComponents;
    }

    public  static ArrayList<PrincipalComponents> getHighestPrincipalComponents(ArrayList<PrincipalComponents> principalComponents, int requiredNumberOfComponents) {
        Collections.sort(principalComponents, new Comparator<PrincipalComponents>() {
            public int compare(PrincipalComponents o1, PrincipalComponents o2) {
                return Double.compare(o1.getValue(), o2.getValue());
            }
        });

        return new ArrayList<PrincipalComponents>(principalComponents.subList(principalComponents.size()-requiredNumberOfComponents, principalComponents.size()));
    }

    public static ArrayList<Data> filterDimensions(ArrayList<Data> data, ArrayList<PrincipalComponents> principalComponents) {
        ArrayList<Data> filteredData = new ArrayList<Data>();
        int size = principalComponents.size();

        for(Data sample: data) {
            Data sampleData =  new Data(size);
            sampleData.setClassId(sample.getClassId());
            Double[] fields = new Double[size];
            for(int i=0; i<size; i++) {
                fields[i] = sample.getFields()[principalComponents.get(i).getDimensionNumber()];
            }
            sampleData.setFields(fields);
            filteredData.add(sampleData);
        }
        return filteredData;
    }
}
