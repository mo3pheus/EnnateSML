package egen.solutions.ennate.egen.solutions.sml.driver;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * Created by jmalakalapalli on 4/26/17.
 */
public class NeuralNetworks {
    static Neuron[] hiddenNeurons, outputNeurons;
    static double[] errorsOfOutputNeurons;
    static double[] errorsOfHiddenNeurons;
    final static double LEARNING_RATE =  0.8;
    static double[] inputs;
    public static void main(String[] args) {
        int ni, nh, no;

        double[] targets;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter number of input neurons");
        ni = sc.nextInt();
        inputs = new double[ni];

        System.out.println("Enter number of hidden neurons");
        nh = sc.nextInt();
        hiddenNeurons = new Neuron[nh];

        System.out.println("Enter number of output neurons");
        no = sc.nextInt();
        outputNeurons = new Neuron[no];

        System.out.println("Enter the input values");
        for (int i = 0; i < ni; i++) {
            inputs[i] = sc.nextDouble();
        }

        System.out.println("Following prompts you to enter weights and bias for " + nh + " hidden neurons");
        for (int i = 0; i < nh; i++) {
            System.out.println("For " + i + " hidden neuron");
            System.out.println("Enter input weights");
            double[] inputWeights = new double[ni];
            double bias;
            for (int j = 0; j < ni; j++) {
                inputWeights[j] = sc.nextDouble();
            }
            System.out.println("Enter bias");
            bias = sc.nextDouble();

            hiddenNeurons[i] = new Neuron(inputWeights, bias);
            computeInputToNeuron(inputs, hiddenNeurons[i]);
            hiddenNeurons[i].setOutput();
        }

        System.out.println("Following prompts you to enter weights and bias for " + no + " output neurons");
        for (int i = 0; i < no; i++) {
            System.out.println("For " + i + " output neuron");
            System.out.println("Enter input weights");
            double[] inputWeights = new double[ni];
            double bias;
            for (int j = 0; j < nh; j++) {
                inputWeights[j] = sc.nextDouble();
            }
            System.out.println("Enter bias");
            bias = sc.nextDouble();

            outputNeurons[i] = new Neuron(inputWeights, bias);
            computeInputToNeuron(hiddenNeurons, outputNeurons[i]);
            outputNeurons[i].setOutput();
        }

        targets = new double[nh];
        System.out.println("Enter the target values");
        for (int i = 0; i < no; i++) {
            targets[i] = sc.nextDouble();
        }


        printOutputValues();

        errorsOfOutputNeurons = new double[no];

        // Calculate errors of output neurons
        IntStream.range(0, no)
                .forEach(i -> {
                    errorsOfOutputNeurons[i] = computeErrorsOfOutputNeuron(outputNeurons[i], targets[i]);
                });


        errorsOfHiddenNeurons = new double[nh];

        // Calculate (back-propagate) errors of hidden layer
        IntStream.range(0, nh)
                .forEach(i -> {
                    double[] weights = new double[no];
                    IntStream.range(0, no)
                            .forEach(j -> {
                                weights[j] = outputNeurons[j].getInputWeights()[i];
                                errorsOfHiddenNeurons[i] = computeErrorsOfHiddenNeuron(hiddenNeurons[i], weights);
                            });
                });

        // Change output layer weights
        IntStream.range(0, no)
                .forEach(i -> computeNewWeightsForOutputNeuron(outputNeurons[i], errorsOfOutputNeurons[i]));

        // Change hidden layer weights
        IntStream.range(0, nh)
                .forEach(i -> computeNewWeightsForHiddenNeuron(hiddenNeurons[i], errorsOfHiddenNeurons[i]));


        computeForwardPass();
        printOutputValues();

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

        public void setOutput() {
            output = (double)1 / (1 + Math.exp(-input));
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
        System.out.println("The output values are ");
        Arrays.stream(outputNeurons)
                .forEach(outputNeuron -> System.out.println(outputNeuron.getOutput()));
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

}
