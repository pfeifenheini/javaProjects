import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class MPerzeptron {
	
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
	
	/** seed for random number generator */
	private long seed = 0;
	/** dimension of the input */
	private int inputDimension;
	/** dimension of the output */
	private int outputDimension;
	/** total number of layers (including input/output layer) */
	private int layers;
	/** number of neurons per layer (including input and output layer) */
	private int[] neuronsPerLayer;
	/** weight matrices for each layer. Index i is matrix connecting layer i and i+1. 
	 * BIAS has the last index */
	private ArrayList<double[][]> weightMatrices;
	/** used transfer function per layer (excluding input layer) */
	private TransferFunction[] transfer;
	/** learning rate per layer (excluding input layer) */
	private double[] learningRate;
	public void setLearningRate(double[] newRate){learningRate = newRate;}
	
	/** net sums of each neuron (including input layer) */
	private ArrayList<double[]> netSums;
	/** output of each neuron (including input layer) */
	private ArrayList<double[]> neuronOutputs;
	/** delta value of each neuron (including input layer) */
	private ArrayList<double[]> deltaValues;
	
	/** log file */
	private PrintWriter log = null;
	/** written lines in log */
	private int logLine = 0;

	
	/**
	 * 
	 * @param neuronsPerLayer number of neurons per layer, starting with input layer
	 * @param transfer transfer function per layer, starting with first layer after input layer
	 * @param learningRate learning rate per layer, starting with first layer after input layer
	 * @param seed seed used for random number generator
	 */
	public MPerzeptron(int[] neuronsPerLayer, TransferFunction[] transfer, double[] learningRate, long seed) {
		this.seed = seed;
		this.neuronsPerLayer = neuronsPerLayer.clone();
		layers = neuronsPerLayer.length;
		inputDimension = neuronsPerLayer[0];
		outputDimension = neuronsPerLayer[neuronsPerLayer.length-1];
		this.transfer = transfer.clone();
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
	
	/**
	 * initializes a weight matrix with random weights between -2 and 2
	 * @param matrix
	 */
	private void initializeRandomWeights(double[][] matrix) {
		Random r = new Random(seed);
		for(int i=0;i<matrix.length;i++) {
			for(int j=0;j<matrix[i].length;j++) {
				matrix[i][j] = 4*r.nextDouble()-2;
			}
		}
	}
	
	/**
	 * calculates the output of the current network for a given input vector
	 * @param input input array
	 * @return output array
	 */
	public double[] calculate(double[] input) {
		if(input.length != inputDimension) return null;
		
		//calculate net sums and output for input layer
		for(int i=0;i<input.length;i++) {
			netSums.get(0)[i] = input[i];
			neuronOutputs.get(0)[i] = input[i];
		}
		
		//calculate net sums and output for other layers
		double w_ij, y_i;
		for(int layer=1;layer<layers;layer++) {
			for(int neuron=0;neuron<neuronsPerLayer[layer];neuron++) {
				netSums.get(layer)[neuron] = weightMatrices.get(layer-1)[neuron][neuronsPerLayer[layer-1]]; //initialize Sum with BIAS
				for(int weight=0;weight<neuronsPerLayer[layer-1];weight++) {
					w_ij = weightMatrices.get(layer-1)[neuron][weight];
					y_i = netSums.get(layer-1)[weight];
					netSums.get(layer)[neuron] += w_ij*y_i;
				}
				//calculate output using transfer function
				neuronOutputs.get(layer)[neuron] = trans(netSums.get(layer)[neuron],transfer[layer-1],false);
			}
		}
		//return output of the last layer
		return neuronOutputs.get(layers-1).clone();
	}
	
	/**
	 * 
	 * @param x input value
	 * @param trans function that shall be used
	 * @param derivative if true, the derivative is calculated
	 * @return
	 */
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
		else { //TransferFunction.ID
			if(derivative)
				return 1.0;
			else
				return x;
		}
	}
	
	private Vector<Pattern> readPatternsFromFile(String fileName) {
		BufferedReader br = null;
		String line;
		Scanner s;
		Vector<Pattern> patterns = new Vector<Pattern>();
		double[] input = new double[inputDimension];
		double[] output = new double[outputDimension];
		try {
			br = new BufferedReader(new FileReader(fileName));
			while((line = br.readLine()) != null) {
				if(line.charAt(0) != '#') {
					s = new Scanner(line).useLocale(Locale.US);
					for(int i=0;i<inputDimension;i++) {
						input[i] = s.nextDouble();
					}
					for(int i=0;i<outputDimension;i++) {
						output[i] = s.nextDouble();
					}
					patterns.add(new Pattern(input,output));
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
//			System.exit(1);
			return null;
		} finally {
			try {
				if(br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return patterns;
	}
	
	/**
	 * trains the network with patters read from a file
	 * @param trainingPatterns contains the training patterns
	 * @param testOnly if true, it only tests patterns. No weights are changed.
	 * @param iterations how often each pattern is trained
	 */
	public void train(Vector<Pattern> trainingPatterns, int iterations) {
		int progress=0;
		for(int i=0;i<iterations;i++) {
			for(Pattern p:trainingPatterns) {
				teach(p.input,p.output);
			}
			test(trainingPatterns,false);
			Collections.shuffle(trainingPatterns,new Random(seed));
			if((double)i/iterations>((double)progress+10)/100) {
				progress+=10;
				System.out.println(progress+ "%");
			}
		}
		log.flush();
		log.close();
	}
	
	/**
	 * calls training function with patterns from a file
	 * @param fileName file containing the patterns
	 * @param iterations how often each pattern is trained
	 */
	public void train(String fileName, int iterations) {
		Vector<Pattern> p = readPatternsFromFile(fileName);
		if(p != null)
			train(readPatternsFromFile(fileName),iterations);
	}
	
	/**
	 * calculates the error of the network w.r.t. test patterns
	 * @param testPatterns contains test patterns
	 * @return accumulated error over all tested patterns
	 */
	public double test(Vector<Pattern> testPatterns, boolean show) {
		double total = 0, err;
		double[] output;
//		int counter=1;
		for(Pattern p:testPatterns) {
			output = calculate(p.input);
			if(show) {
				System.out.print("In: " + Arrays.toString(p.input));
				System.out.print(" out: "  + Arrays.toString(output));
				System.out.println(" (teacher: "  + Arrays.toString(p.output) + ")");
				
			}
			err = error(p.output, output);
//			log(err);
			total += err;
//			System.out.println("Test pattern " + counter++ + " has error " + err);
		}
		log(total);
//		System.out.println("Total error: " + total);
		return total;
	}
	
	/**
	 * calculates the error of the network w.r.t. test patterns
	 * @param fileName contains test patterns
	 * @return accumulated error over all tested patterns
	 */
	public double test(String fileName) {
		Vector<Pattern> p = readPatternsFromFile(fileName);
		if(p != null)
			return test(readPatternsFromFile(fileName),true);
		return -1;
	}
	
	/**
	 * teaches the network a single pattern
	 * @param input input
	 * @param teacher desired output
	 */
	public void teach(double[] input, double[] teacher) {
		if(input.length != inputDimension) return; //input has invalid dimension
		if(teacher.length != outputDimension) return; //output has invalid dimension
		
		double[] output = calculate(input);
		
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
						out_i = 1; //BIAS
					
					weightMatrices.get(layer-1)[neuron][weight] += learningRate[layer-1] * delta_j * out_i;
				}
			}
		}
	}
	
	/**
	 * calculates error between a two vectors
	 * @param teacher teacher output
	 * @param output network output
	 * @return error
	 */
	public double error(double[] teacher,double[] output) {
		assert(teacher.length == output.length);
		double err = 0;
		for(int i=0;i<teacher.length;i++)
			err += (teacher[i]-output[i])*(teacher[i]-output[i]);
		return err/2;
	}
	
	/**
	 * logs the error in a file
	 * @param err value to log
	 */
	private void log(double err) {
		if(log == null) {
			try {
				log = new PrintWriter("learning.curve", "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		log.println(logLine++ + " " + err);
	}
	
	/**
	 * @return string that represents the state of the network
	 */
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
//		String trainingFile = "training1.dat";
//		int[] neurons = {4,2};
//		TransferFunction[] transfer = {TransferFunction.TANH};
//		double[] learningRates = {0.05};
		
//		String trainingFile = "training1.dat";
//		int[] neurons = {4,8,2};
//		TransferFunction[] transfer = {TransferFunction.TANH,TransferFunction.TANH};
//		double[] learningRates = {0.05,0.05};
		
//		String trainingFile = "training2.dat";
//		int[] neurons = {2,1};
//		TransferFunction[] transfer = {TransferFunction.ID};
//		double[] learningRates = {0.05};
		
//		String trainingFile = "training2.dat";
//		int[] neurons = {2,5,5,1};
//		TransferFunction[] transfer = {TransferFunction.TANH,TransferFunction.TANH,TransferFunction.ID};
//		double[] learningRates = {0.005,0.001,0.0001};
		
//		String trainingFile = "XOR.dat";
//		int[] neurons = {2,2,1};
//		TransferFunction[] transfer = {TransferFunction.TANH,TransferFunction.FERMI};
//		double[] learningRates = {0.8,0.2};
		
//		String trainingFile = "838codec.dat";
//		int[] neurons = {8,2,8};
//		TransferFunction[] transfer = {TransferFunction.FERMI,TransferFunction.FERMI};
//		double[] learningRates = {0.8,0.2};
		
		// file with training patterns
		String trainingFile = "training.dat";
		
		// file with test patterns
		String testFile = "test.dat";
		
		// neurons per layer (starting with input layer)
		int[] neurons = {4,5,2};
		
		// transfer functions per layer (starting with second layer)
		TransferFunction[] transfer = {TransferFunction.TANH,TransferFunction.TANH};
		
		// learning rates per layer (starting with second layer) 
		double[] learningRates = {0.8,0.2};
		
		// seed for random number generator
		long seed = new Random().nextLong();
//		seed = 0;
		System.out.println("seed: " + seed);
		
		MPerzeptron p = new MPerzeptron(neurons, transfer, learningRates, seed);
		
		System.out.println("current error (training data): " + p.test(trainingFile));
		System.out.println("current error (test data): " + p.test(testFile));
		p.train(trainingFile,100000);
		System.out.println("current error: (training data) " + p.test(trainingFile));
		System.out.println("current error (test data): " + p.test(testFile));
	}
}
