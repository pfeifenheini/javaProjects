import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class MPerzeptron {

	private long SEED = 0;
	
	public static enum TransferFunction{
		FERMI, TANH, ID
	}
	
	private class Pattern{
		public double[] input;
		public double[] output;
		
		public Pattern(double[] input, double[] output) {
			this.input = input.clone();
			this.output = output.clone();
		}
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
	/** delta value of each neuron (including input layer) */
	private ArrayList<double[]> deltaValues;
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
		deltaValues = new ArrayList<double[]>(neuronsPerLayer.length);
		for(int layer=0;layer<layers;layer++) {
			if(layer<neuronsPerLayer.length-1) {
				double[][] matrix = new double[neuronsPerLayer[layer+1]][neuronsPerLayer[layer]+1];
				initializeRandomWeights(matrix);
				weightMatrices.add(matrix);
			}
			netSums.add(new double[neuronsPerLayer[layer]]);
			neuronOutputs.add(new double[neuronsPerLayer[layer]]);
			deltaValues.add(new double[neuronsPerLayer[layer]]);
		}
	}
	
	private void initializeRandomWeights(double[][] matrix) {
		Random r = new Random(SEED); //TODO gain control over random generator
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
				neuronOutputs.get(layer)[neuron] = trans(netSums.get(layer)[neuron],transfer[layer-1],false);
//				System.out.println("neuron=(" + layer + "," + neuron + "), sum=" + netSums.get(layer)[neuron] + ", out=" + neuronOutputs.get(layer)[neuron]);
			}
		}
		return neuronOutputs.get(layers-1);
	}
	
	private double trans(double x, TransferFunction trans, boolean derivative) {
		if(trans == TransferFunction.TANH) {
			if(derivative)
				return 1-(Math.tanh(x)*Math.tanh(x));
			else
				return Math.tanh(x);
		}
		else if(trans == TransferFunction.FERMI) {
			if(derivative)
				return 1/(1+Math.exp(-x))-((1/(1+Math.exp(-x)))*(1/(1+Math.exp(-x))));
			else
				return 1/(1+Math.exp(-x));
		}
		else {
			if(derivative)
				return 1;
			else
				return x;
		}
	}
	
	/**
	 * trains the network with patters read from a file
	 * @param fileName path to the file
	 */
	public void train(String fileName, boolean testOnly, int iterations) {
		BufferedReader br = null;
		String line;
		Scanner s;
		Vector<Pattern> trainingData = new Vector<Pattern>();
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
					trainingData.add(new Pattern(trainInput,trainOutput));
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
		
		if(testOnly) {
			for(Pattern p:trainingData) {
				teach(p.input,p.output,testOnly);
			}
		}
		else {
			int progress=0;
			for(int i=0;i<iterations;i++) {
				for(Pattern p:trainingData) {
					teach(p.input,p.output,testOnly);
				}
				Collections.shuffle(trainingData,new Random(SEED));
				if((double)i/iterations>((double)progress+10)/100) {
					progress+=10;
					System.out.println(progress+ "%");
				}
			}			
		}
	}
	
	/**
	 * teaches the network a single pattern
	 * @param input input
	 * @param teacher desired output
	 */
	public void teach(double[] input, double[] teacher, boolean testOnly) {
		if(input.length != inputDimension) return; //input has invalid dimension
		if(teacher.length != outputDimension) return; //output has invalid dimension
		
		double[] output = calculate(input);
		
		if(testOnly) {
			System.out.print("Teach: ");
			for(int i=0;i<inputDimension;i++)
				System.out.print(input[i] + " ");
			System.out.print("--> ");
			for(int i=0;i<outputDimension;i++)
				System.out.print((int)(teacher[i]-output[i]) + " ");
//			for(int i=0;i<outputDimension;i++)
//				System.out.print(teacher[i] + " ");
//			System.out.print(" compared to ");
//			for(double d:output) System.out.print(d + " ");
			System.out.println();
			return;
		}
		
		// calculate delta for output layer
		for(int neuron=0;neuron<neuronsPerLayer[layers-1];neuron++) {
			deltaValues.get(layers-1)[neuron] = (teacher[neuron] - output[neuron]) * trans(netSums.get(layers-1)[neuron],transfer[layers-2],true);
		}
		
		// calculate delta for upper layers
		double delta_k, w_hk, delta_h;
		for(int layer=layers-2;layer>0;layer--) {
			for(int neuron=0;neuron<neuronsPerLayer[layer];neuron++) {
				delta_h = 0;
				for(int nextLayerNeuron=0;nextLayerNeuron<neuronsPerLayer[layer+1];nextLayerNeuron++) {
					delta_k = deltaValues.get(layer+1)[nextLayerNeuron];
					w_hk = weightMatrices.get(layer)[nextLayerNeuron][neuron];
					delta_h += delta_k * w_hk;
				}
				
				delta_h = delta_h * trans(netSums.get(layer)[neuron],transfer[layer-1],true);
				
				deltaValues.get(layer)[neuron] = delta_h;
			}
		}
		
		// change weights
		double delta_j, out_i;
		for(int layer=1;layer<layers;layer++) {
			for(int neuron=0;neuron<neuronsPerLayer[layer];neuron++) {
				for(int weight=0;weight<=neuronsPerLayer[layer-1];weight++) {
					delta_j = deltaValues.get(layer)[neuron];
					
					if(weight<neuronsPerLayer[layer-1])
						out_i = neuronOutputs.get(layer-1)[weight];
					else
						out_i = 1;
					
					weightMatrices.get(layer-1)[neuron][weight] += learningRate * delta_j * out_i;
				}
			}
		}
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
		int[] neurons = {4,5,5,2};
		TransferFunction[] transfer = {TransferFunction.FERMI,TransferFunction.TANH,TransferFunction.TANH};
		MPerzeptron p = new MPerzeptron(neurons, transfer, 0.1);
		System.out.println(p.toString());
		p.train("training1.dat",true,1);
		p.train("training1.dat",false,5000000);
		System.out.println(p.toString());
		p.train("training1.dat",true,1);
//		double[] output = p.calculate(input);
//		for(double d:output) System.out.print(d + " ");
		
	}
}
