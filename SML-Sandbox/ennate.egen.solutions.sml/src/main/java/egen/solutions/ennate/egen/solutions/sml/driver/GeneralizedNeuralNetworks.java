package egen.solutions.ennate.egen.solutions.sml.driver;

import ennate.egen.solutions.sml.domain.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by jmalakalapalli on 5/13/17.
 */
public class GeneralizedNeuralNetworks {
    private static Neuron[] hiddenNeurons, outputNeurons;
    private static double[] errorsOfOutputNeurons;
    private static double[] errorsOfHiddenNeurons;
    private final static double LEARNING_RATE =  0.8;
    private final static double THRESHOLD = 0.001;
    private static final String FILENAME = "neuralNetworkOutput.txt";
    private static ArrayList<Data> testingData;

    static double[] inputs;
    static double[] targets;

    static int ni, nh, no;
    public static void main(String[] args) {
        double sampleError = Double.MAX_VALUE;
        ArrayList<Data> trainingData;
        ni = 4;
        inputs = new double[ni];

        nh = 2;
        hiddenNeurons = new Neuron[nh];

        no = 3;
        outputNeurons = new Neuron[no];


        SanketML irisProblem = new SanketML();
        try {
            irisProblem.loadData("iris.data.txt", ",", 4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        irisProblem.populateTrainTestSets(80);
        trainingData = irisProblem.getTrainingData();
        testingData = irisProblem.getTestingData();


        // shuffle training data.

        Collections.shuffle(trainingData);



        // initialize weights of hidden neurons
        initializeWeightsOfNeurons(hiddenNeurons, ni);

        // initialize weights of output neurons
        initializeWeightsOfNeurons(outputNeurons, nh);


        double cumulativeError;
        int counter = 0;
        do {

            cumulativeError = 0.0d;
            for (Data aTrainingData : trainingData) {
                // initialize inputs
                initializeInputs(aTrainingData);

                // initialize targets
                initializeTargets(aTrainingData);

                computeForwardPass();
                System.out.println("For input " + aTrainingData);
                printOutputValues();
                sampleError = computeTotalError();
                //System.out.println("Sample error is " + sampleError);

                errorsOfOutputNeurons = new double[no];

                // Calculate errors of output neurons
                IntStream.range(0, no)
                        .forEach(j -> {
                            errorsOfOutputNeurons[j] = computeErrorsOfOutputNeuron(outputNeurons[j], targets[j]);
                        });


                errorsOfHiddenNeurons = new double[nh];

                // Calculate (back-propagate) errors of hidden layer
                IntStream.range(0, nh)
                        .forEach(k -> {
                            double[] weights = new double[no];
                            IntStream.range(0, no)
                                    .forEach(j -> {
                                        weights[j] = outputNeurons[j].getInputWeights()[k];
                                        errorsOfHiddenNeurons[k] = computeErrorsOfHiddenNeuron(hiddenNeurons[k], weights);
                                    });
                        });

                // Change output layer weights
                IntStream.range(0, no)
                        .forEach(j -> computeNewWeightsForOutputNeuron(outputNeurons[j], errorsOfOutputNeurons[j]));

                // Change hidden layer weights
                IntStream.range(0, nh)
                        .forEach(j -> computeNewWeightsForHiddenNeuron(hiddenNeurons[j], errorsOfHiddenNeurons[j]));

                cumulativeError += sampleError;
            }
            counter++;
            System.out.println("Total error is " + cumulativeError);

        } while (cumulativeError > 0.18);


        System.out.println("The assumed binary representation for classes are as follows:" +
                "Iris-setosa : 001, Iris-verisicolor: 010, Iris-virginica: 110");
        printWeights();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            StringBuilder dataBuilder = new StringBuilder();

            dataBuilder.append( "\"The assumed binary representation for classes are as follows:\" +\n" +
                    "                \"Iris-setosa : 001, Iris-verisicolor: 010, Iris-virginica: 110\"\n");

            dataBuilder.append("The input at the point of termination are ");
            Arrays.stream(inputs)
                    .forEach(input -> dataBuilder.append(input + " "));

            dataBuilder.append("\nThe target output is ");
            Arrays.stream(targets)
                    .forEach(target -> dataBuilder.append(target + " "));

            dataBuilder.append("\nThe actual output is ");
            Arrays.stream(outputNeurons)
                    .forEach(outputNeuron -> dataBuilder.append(outputNeuron.getOutput() + " "));

            dataBuilder.append("\nThe error is " + sampleError);

            dataBuilder.append("\nThe hidden neurons at the point of termination are as follows");

            Arrays.stream(hiddenNeurons)
                    .forEach(neuron -> dataBuilder.append(neuron.toString()));

            dataBuilder.append("\nThe output neurons at the point of termination are as follows");

            Arrays.stream(outputNeurons)
                    .forEach(neuron -> dataBuilder.append(neuron.toString()));

            bw.write(dataBuilder.toString());

            // no need to close it.
            //bw.close();

            System.out.println("Done writing to file");

        } catch (IOException e) {

            e.printStackTrace();

        }
        computeAccuracy();
        System.out.println("The number of iterations " + counter);
    }

    @Getter
    @Setter
    private static class Neuron {
        double[] inputWeights;
        double input;
        double output;
        double bias;

        public Neuron(double[] inputWeights, double bias) {
            this.inputWeights = inputWeights;
            this.bias = bias;
        }

        public Neuron(double[] inputWeights) {
            this.inputWeights = inputWeights;
            this.bias = 0.0d;
        }

        public void setOutput() {
            output = (double)1 / (1 + Math.exp(-input));
        }

        public void printInputWeights() {
            Arrays.stream(inputWeights)
                    .forEach(weight -> System.out.print(weight + ", "));
        }

        public String toString() {
            StringBuilder dataBuilder = new StringBuilder();
            dataBuilder.append("Neuron: Input weights are ");
            Arrays.stream(inputWeights)
                    .forEach(weight -> dataBuilder.append(weight + ", "));
            dataBuilder.append("output is " + output + " ");
            dataBuilder.append("Net input is " + input + " ");
            dataBuilder.append("Bias is " + bias + " ");

            return dataBuilder.toString();
        }
    }

    public static void computeInputToNeuron(double[] inputs, Neuron neuron) {
        double input;
        input = IntStream.range(0, inputs.length)
                .mapToDouble(i -> inputs[i] * neuron.getInputWeights()[i])
                .sum();
        input += neuron.getBias();

        neuron.setInput(input);
    }

    public static void computeInputToNeuron(Neuron[] neuronInputs, Neuron neuron) {
        double input;
        input = IntStream.range(0, neuronInputs.length)
                .mapToDouble(i -> neuronInputs[i].getOutput() * neuron.getInputWeights()[i])
                .sum();
        input += neuron.getBias();

        neuron.setInput(input);
    }

    public static double computeErrorsOfOutputNeuron(Neuron outputNeuron, double target) {
        return outputNeuron.getOutput() * (1 - outputNeuron.getOutput()) * (target - outputNeuron.getOutput());
    }

    public static double computeErrorsOfHiddenNeuron(Neuron hiddenNeuron, double[] weights) {
        double error = 0.0d;
        for (int i = 0; i < errorsOfOutputNeurons.length; i++) {
            error += errorsOfOutputNeurons[i] * weights[i];
        }
        error *= hiddenNeuron.getOutput() * (1 - hiddenNeuron.getOutput());

        return error;
    }

    public static void computeNewWeightsForOutputNeuron(Neuron outputNeuron, double errorOfOutputNeuron) {
        double[] inputWeights = outputNeuron.getInputWeights();
        IntStream.range(0, inputWeights.length)
                .forEach(i -> {
                    inputWeights[i] = inputWeights[i] + (LEARNING_RATE * errorOfOutputNeuron * hiddenNeurons[i].getOutput());
                });
    }

    public static void computeNewWeightsForHiddenNeuron(Neuron hiddenNeuron, double errorOfHiddenNeuron) {
        double[] inputWeights = hiddenNeuron.getInputWeights();
        IntStream.range(0, inputWeights.length)
                .forEach(i -> {
                    inputWeights[i] = inputWeights[i] + (LEARNING_RATE * errorOfHiddenNeuron * inputWeights[i]);
                });
    }

    public static void printOutputValues() {
        System.out.println("The output values are");
        Arrays.stream(outputNeurons)
                .forEach(outputNeuron -> System.out.println(outputNeuron.getOutput()));
    }

    private static double[] getOutputValues() {
        return Arrays.stream(outputNeurons)
                .mapToDouble(Neuron::getOutput)
                .toArray();

    }

    public static void computeForwardPass() {
        Arrays.stream(hiddenNeurons)
                .forEach(hiddenNeuron -> {
                    computeInputToNeuron(inputs, hiddenNeuron);
                    hiddenNeuron.setOutput();
                });

        Arrays.stream(outputNeurons)
                .forEach(outputNeuron -> {
                    computeInputToNeuron(hiddenNeurons, outputNeuron);
                    outputNeuron.setOutput();
                });
    }

    private static double computeTotalError() {
        double error = 0.0d;
        for (int i = 0; i < no; i++) {
            error += (outputNeurons[i].getOutput() - targets[i]) * (outputNeurons[i].getOutput() - targets[i]);
        }
        error /= 2;

        return error;
    }

    private static void initializeWeightsOfNeurons(Neuron[] neurons, int noOfWeights) {
        for (int i = 0; i < neurons.length; i++) {
            double[] inputWeights = new double[noOfWeights];
            double bias;
            for (int j = 0; j < noOfWeights; j++) {
                inputWeights[j] = Math.random();
            }
            bias = Math.random();

            neurons[i] = new Neuron(inputWeights, bias);
        }
    }

    private static void printWeights() {
        System.out.println("The current weights of the system from hidden neurons to output neurons respectively are ");
        Arrays.stream(hiddenNeurons)
                .forEach(Neuron::printInputWeights);

        Arrays.stream(outputNeurons)
                .forEach(Neuron::printInputWeights);
    }

    private static void computeAccuracy() {
        int totalAccurate = 0;
        for (Data data : testingData) {
            initializeInputs(data);
            initializeTargets(data);
            computeForwardPass();
            double[] actualOutputs = getOutputValues();
            actualOutputs = parseOutputValues(actualOutputs);
            if (Arrays.equals(actualOutputs, targets)) {
                totalAccurate++;
            }
        }
        double accuray = ((double) totalAccurate / testingData.size()) * 100;
        System.out.print("The total accuracy is " + accuray);
    }

    private static void initializeInputs(Data data) {
        for (int j = 0; j < ni; j++) {
            inputs[j] = data.getFields()[j];
        }
    }

    private static double[] parseOutputValues(double[] outputValues) {
       return Arrays.stream(outputValues)
                .map(outputValue -> {
                    if (outputValue > 0.5) {
                        return 1;
                    } else {
                        return 0;
                    }
                }).toArray();
    }

    private static void initializeTargets(Data data) {
        for (int j = 0; j < no; j++) {
            switch (data.getClassId()) {
                case "Iris-setosa":
                    targets = new double[]{0, 0, 1};
                    break;
                case "Iris-versicolor":
                    targets = new double[]{0, 1, 0};
                    break;
                case "Iris-virginica":
                    targets = new double[]{1, 1, 0};
                    break;
            }
        }
    }

}
