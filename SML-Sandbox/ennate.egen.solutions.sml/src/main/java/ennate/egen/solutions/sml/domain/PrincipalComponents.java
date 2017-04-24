package ennate.egen.solutions.sml.domain;

public class PrincipalComponents {
    int dimensionNumber;
    Double value;

    public PrincipalComponents(int dimensionNumber, Double value) {
        this.dimensionNumber = dimensionNumber;
        this.value = value;
    }

    public int getDimensionNumber() {
        return dimensionNumber;
    }

    public double getValue() {
        return value;
    }
}
