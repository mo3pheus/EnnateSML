package ennate.egen.solutions.sml.domain;

public class Utils {
    public static Double[] initializeArray(Double[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i] = 0.0d;
        }
        return array;
    }

    public static String printArray(Double[] doubles) {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < doubles.length; i++) {
            builder.append(String.format("field[%d]: %f ", i, doubles[i]));
        }

        return builder.toString();
    }

    public static Double[][] initializeMatrix(Double[][] matrix, Integer rows, Integer columns) {

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix[i][j] = 0.0d;
            }
        }

        return matrix;
    }
}
