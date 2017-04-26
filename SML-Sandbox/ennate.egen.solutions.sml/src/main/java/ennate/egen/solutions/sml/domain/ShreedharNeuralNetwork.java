package ennate.egen.solutions.sml.domain;


import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class ShreedharNeuralNetwork {

    public static void main(String[] args) {
        Integer numberOfInputNeurons = 0;
        Integer numberOfHiddenNeurons = 0;
        try {

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter number of input neurons: ");
            String numberOfNeuronsText = scanner.nextLine();
            numberOfInputNeurons  = Integer.valueOf(numberOfNeuronsText);

            System.out.println("Enter number of hidden neurons: ");
            String numberOfHiddenNeuronsText = scanner.nextLine();
            numberOfHiddenNeurons = Integer.valueOf(numberOfHiddenNeuronsText);

            if (numberOfInputNeurons < 2 || numberOfHiddenNeurons < 2) {
                throw new Exception("neurons cannot be less than two");
            }

        } catch (Exception e) {
            System.out.println("Invalid input: "+ e);
            System.exit(0);
        }

        ShreedharNeuralNetwork network = new ShreedharNeuralNetwork();
        network.compute(numberOfInputNeurons, numberOfHiddenNeurons);
    }


    private Double[][] adjacencyMatrix = null;

    public void compute(int numberOfInputNeurons, int numberOfHiddenNeurons) {
        NeuronIndices.getInstance().generateSets(numberOfInputNeurons,numberOfHiddenNeurons);
        Integer matrixSize = NeuronIndices.getInstance().getHighestIndex() + 1;
        generateAdjacencyMatrix(matrixSize);
        performComputation(numberOfInputNeurons, numberOfHiddenNeurons, matrixSize);
    }

    private void performComputation(int numberOfInputNeurons, int numberOfHiddenNeurons, int matrixSize) {
        Random random = new Random();
        int currentRow = 0;

        // Process pseudo neurons - Multiply inputs with their weights
        for (int i = 0; i < numberOfInputNeurons; i++) {
            if (! NeuronIndices.getInstance().isInputIndex(i)) {
                throw new RuntimeException("Invalid number of input neurons"+ numberOfInputNeurons);
            }

            int row = i;
            Double randomGeneratedInput = random.nextDouble();
            for (int col = 0; col < matrixSize; col++) {
                Double rowValue = this.adjacencyMatrix[row][col];

                if (rowValue <= 0.0d) {
                    continue;
                }

                // Summation of inputs and their weights
                this.adjacencyMatrix[row][col] = this.adjacencyMatrix[row][col] * randomGeneratedInput;
            }

            currentRow = i;
        }

        // Process actual neurons
        for (int i = (currentRow + 1); i < matrixSize; i++) {

            if (NeuronIndices.getInstance().isBiasIndex(i)) {
                System.out.println("Exiting");
                System.exit(0);
            }

            if (NeuronIndices.getInstance().isHiddenIndex(i)) {
                int currentColumn = i;
                Double summationX = 0.0d;

                // Calculate new weight
                for (int row = 0; row < matrixSize; row++) {
                    summationX += this.adjacencyMatrix[row][currentColumn];
                }

                Double neuronOutput = this.applySigmoid(summationX);

                // Calculate the new weight by multiplying the neuronOutput to the existing weight on the link
                for (int col = 0; col < matrixSize; col++) {
                    Double value = this.adjacencyMatrix[currentRow][col];

                    if(value <= 0.0d) {
                        continue;
                    }

                    this.adjacencyMatrix[currentRow][col] = value * neuronOutput;
                }

            } else if (NeuronIndices.getInstance().isOutputIndex(i)) {
                int currentColumn = i;
                Double summationX = 0.0d;

                // Calculate new weight
                for (int row = 0; row < matrixSize; row++) {
                    summationX += this.adjacencyMatrix[row][currentColumn];
                }

                Double neuronOutput = this.applySigmoid(summationX);
                int indexOfThisOutputNeuron = i % (numberOfInputNeurons + numberOfHiddenNeurons);

                System.out.println(String.format("OutPut of the Neuron:[%d] is [%f]", indexOfThisOutputNeuron, neuronOutput));
            }

        }

    }


    private Double applySigmoid (Double value) {

        value = value * -1.0d;
        Double exponential = Math.exp(value);
        Double result = 1 / (1 + exponential);

        return result;
    }

    private void generateAdjacencyMatrix (Integer N) {
        Double[][] matrix = Utils.initializeMatrix(new Double[N][N], N, N);
        Random random = new Random();

        for (int i = 0; i < N; i++) {
            Set<Integer> set = NeuronIndices.getInstance().getSetForMatrixBuild(i);
            Integer firstIndex = getIndex(set, Position.FIRST);
            Integer lastIndex = getIndex(set, Position.LAST);

            if (firstIndex != -1 && !firstIndex.equals(lastIndex)) {
                for (int j = firstIndex; j <= lastIndex; j++) {
                    matrix[i][j] = random.nextDouble();
                }
            }
        }

        this.adjacencyMatrix = matrix;
    }


    private Integer getIndex(Set<Integer> integerSet, Position position) {
        if (Objects.isNull(integerSet)) {
            throw new RuntimeException("set cannot be empty");
        }

        if (integerSet.isEmpty()) {
            return -1;
        }

        if (position == Position.FIRST) {
            for (Integer value : integerSet) {
                return value;
            }
        }

        if (position == Position.LAST) {
            Integer index = -1;
            for (Integer value : integerSet) {
                index = value;
            }

            return index;
        }

        throw new RuntimeException("Invalid position");
    }

    private static class NeuronIndices {
        private final Set<Integer> inputIndices = new LinkedHashSet<>();
        private final Set<Integer> hiddenIndices = new LinkedHashSet<>();
        private final Set<Integer> outputIndices = new LinkedHashSet<>();
        private final Set<Integer> firstBiasIndex = new LinkedHashSet<>();
        private final Set<Integer> secondBiasIndex = new LinkedHashSet<>();
        private Integer highestIndex = 0;


        private static NeuronIndices instance = new NeuronIndices();

        public static NeuronIndices getInstance() {
            return instance;
        }

        public void generateSets(int numberOfInputNeurons, int numberOfHiddenNeurons) {

            if (numberOfHiddenNeurons < numberOfInputNeurons) {
                throw new RuntimeException("Hidden neurons should be greater than input neurons");
            }

            int index = 0;

            for (int i = 0; i < numberOfInputNeurons; i++) {
                inputIndices.add(index);
                index++;
            }

            for (int i = 0; i < numberOfHiddenNeurons; i++) {
                hiddenIndices.add(index);
                index++;
            }

            for (int i = 0; i < numberOfInputNeurons; i++) {
                outputIndices.add(index);
                index++;
            }

            firstBiasIndex.add(index++);
            secondBiasIndex.add(index);

            this.highestIndex = index;
        }

        public Set<Integer> getSetForMatrixBuild(Integer index) {

            if (inputIndices.contains(index)) {
                return hiddenIndices;
            }

            if (hiddenIndices.contains(index)) {
                return outputIndices;
            }

            if (outputIndices.contains(index)) {
                return new HashSet<>();
            }

            if (firstBiasIndex.contains(index)) {
                return hiddenIndices;
            }

            if (secondBiasIndex.contains(index)) {
                return outputIndices;
            }

            throw new RuntimeException("Invalid index: "+ index);
        }


        public boolean isBiasIndex (Integer index) {
            return firstBiasIndex.contains(index) || secondBiasIndex.contains(index);
        }

        public boolean isOutputIndex (Integer index) {
            return outputIndices.contains(index);
        }

        public boolean isInputIndex (Integer index) {
            return inputIndices.contains(index);
        }

        public boolean isHiddenIndex (Integer index) {
            return hiddenIndices.contains(index);
        }

        public Integer getHighestIndex() {
            return highestIndex;
        }
    }

    private enum Position { FIRST, LAST }
}
