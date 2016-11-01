import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class MPerzeptron {

	public static enum TransferFunction{
		FERMI, TANH, ID
	}
	
	private int inputDimension;
	private int outputDimension;
	/** total number of layers (including input/output layer) */
	private int layers;
	private int[] neuronsPerLayer;
	/** weight matrices for each layer. Index i is matrix connecting layer i and i+1. 
	 * BIAS has the last index */
	private ArrayList<double[][]> weightMatrices;
	private TransferFunction[] transfer;
	/** net sums of each neuron (including input layer) */
	private ArrayList<double[]> netSums;
	/** output of each neuron (including input layer) */
	private ArrayList<double[]> neuronOutputs;
	private double learningRate;
	
	/**
	 * 
	 * @param neuronsPerLayer array containing the number of neurons per layer, starting with input layer at index 0
	 * @param transfer array containing the transfer function per layer, starting with first layer after input layer at index 0
	 */
	public MPerzeptron(int[] neuronsPerLayer, TransferFunction[] transfer, double learningRate) {
		this.neuronsPerLayer = neuronsPerLayer;
		layers = neuronsPerLayer.length;
		inputDimension = neuronsPerLayer[0];
		outputDimension = neuronsPerLayer[neuronsPerLayer.length-1];
		this.transfer = transfer;
		this.learningRate = learningRate;
		weightMatrices = new ArrayList<double[][]>(neuronsPerLayer.length-1);
		netSums = new ArrayList<double[]>(neuronsPerLayer.length);
		neuronOutputs = new ArrayList<double[]>(neuronsPerLayer.length);
		for(int i=0;i<neuronsPerLayer.length;i++) {
			if(i<neuronsPerLayer.length-1) {
				double[][] matrix = new double[neuronsPerLayer[i+1]][neuronsPerLayer[i]+1];
				initializeRandomWeights(matrix);
				weightMatrices.add(matrix);
			}
			netSums.add(new double[neuronsPerLayer[i]]);
			neuronOutputs.add(new double[neuronsPerLayer[i]]);
		}
	}
	
	private void initializeRandomWeights(double[][] matrix) {
		Random r = new Random(); //TODO gain control over random generator
		for(int i=0;i<matrix.length;i++) {
			for(int j=0;j<matrix[i].length;j++) {
				matrix[i][j] = 4*r.nextDouble()-2;
//				matrix[i][j] = -10;
			}
		}
	}
	
	public double[] calculate(double[] input) {
		if(input.length != inputDimension) return null;
		
		for(int i=0;i<input.length;i++) {
			netSums.get(0)[i] = input[i];
			neuronOutputs.get(0)[i] = input[i];
		}
		
		for(int layer=1;layer<layers;layer++) {
			
			for(int neuron=0;neuron<neuronsPerLayer[layer];neuron++) {
				netSums.get(layer)[neuron] = weightMatrices.get(layer-1)[neuron][neuronsPerLayer[layer-1]]; //initialize Sum with BIAS
				for(int weight=0;weight<neuronsPerLayer[layer-1];weight++) {
					netSums.get(layer)[neuron] += weightMatrices.get(layer-1)[neuron][weight]*netSums.get(layer-1)[weight];
				}
				neuronOutputs.get(layer)[neuron] = trans(netSums.get(layer)[neuron],transfer[layer-1]);
//				System.out.println("neuron=(" + layer + "," + neuron + "), sum=" + netSums.get(layer)[neuron] + ", out=" + neuronOutputs.get(layer)[neuron]);
			}
		}
		
		return neuronOutputs.get(layers-1);
	}
	
	private double trans(double x,TransferFunction trans) {
		//TODO make dependent on actual transfer function
		if(trans == TransferFunction.TANH)
			return Math.tanh(x);
		else
			return x;
	}
	
	/**
	 * trains the network with patters read from a file
	 * @param fileName path to the file
	 */
	public void train(String fileName) {
		BufferedReader br = null;
		String line;
		Scanner s;
		double[] trainInput = new double[inputDimension];
		double[] trainOutput = new double[outputDimension];
		try {
			br = new BufferedReader(new FileReader(fileName));
			while((line = br.readLine()) != null) {
				if(line.charAt(0) != '#') {
					s = new Scanner(line).useLocale(Locale.US);
					for(int i=0;i<inputDimension;i++) {
						trainInput[i] = s.nextDouble();
					}
					for(int i=0;i<outputDimension;i++) {
						trainOutput[i] = s.nextDouble();
					}
					teach(trainInput,trainOutput);
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.exit(1);
		} finally {
			try {
				if(br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 * teaches the network a single pattern
	 * @param teacherInput input
	 * @param teacherOutput desired output
	 */
	public void teach(double[] teacherInput, double[] teacherOutput) {
		if(teacherInput.length != inputDimension) return; //input has invalid dimension
		if(teacherOutput.length != outputDimension) return; //output has invalid dimension
		
		System.out.print("Teach: ");
		for(int i=0;i<inputDimension;i++)
			System.out.print(teacherInput[i] + " ");
		System.out.print("--> ");
		for(int i=0;i<outputDimension;i++)
			System.out.print(teacherOutput[i] + " ");
		System.out.print(" compared to ");
		
		//TODO implement teaching mechanism
		double[] output = calculate(teacherInput);
		for(double d:output) System.out.print(d + " ");
		System.out.println();
	}
	
	public String toString() {
		String s = "";
		
		int layer = 1;
		for(double[][] matrix:weightMatrices) {
			s += "weights between layer " + (layer-1) + " and " + layer + ":\n";
			for(int neuron=0;neuron<matrix.length;neuron++) {
				for(int weight=0;weight<matrix[neuron].length;weight++) {
					if(weight == matrix[neuron].length-1)
						s += "<";
					s += matrix[neuron][weight];
					if(weight == matrix[neuron].length-1)
						s += ">";
					else
						s+= " ";
				}
				s += "\n";
			}
			layer++;
		}
		return s;
	}
	
	public static void main(String[] args) {
		int[] neurons = {4,10,10,2};
		TransferFunction[] transfer = {TransferFunction.TANH,TransferFunction.TANH,TransferFunction.TANH};
		MPerzeptron p = new MPerzeptron(neurons, transfer, 0.1);
		System.out.println(p.toString());
		p.train("training1.dat");
//		double[] output = p.calculate(input);
//		for(double d:output) System.out.print(d + " ");
	}
}
