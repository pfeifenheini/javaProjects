import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

public class RBFNet {
	
	public static final int DEFAULT_RBF_NEURONS = 8;
	public static final long DEFAUL_SEED = new Random().nextLong();
	public static final double DEFAULT_WIDTH = 0.05;
	public static final double DEFAULT_LEARNING_RAITE = 0.05;
	
	public static class Pattern{
		public double[] input;
		public double[] output;
		
		public Pattern(double[] input, double[] output) {
			this.input = input.clone();
			this.output = output.clone();
		}
	}
	
	/** seed for random number generator */
	private long seed;
	/** dimension of the input */
	private int inputDimension;
	/** dimension of the output */
	private int outputDimension;
	/** number of RBF neurons */
	private int rbfNeurons;
	
	/** weights[i][j] = weight connecting RBF neuron i with output neuron j.
	 *  The largest value of i contains the BIAS weight of output neuron j. */
	private double[][] weights;
	/** centers[i] contains the center of RBF neuron i */
	private double[][] centers;
	/** widths[i] contains the RBF-width of RBF neuron i */
	private double[] widths;
	
	/** set of training patterns */
	private Vector<Pattern> trainingData;
	/** set of test patterns */
	private Vector<Pattern> testData;
	
	/** used for calculation done at RBF neurons */
	private double[] resultsRBF;
	/** used for calculation done at output neurons */
	private double[] resultsOutput;
	/** delta values for output neurons */
	private double[] deltaValues;
	
	/** learing rate */
	private double learingRate = DEFAULT_LEARNING_RAITE;
	
	/** log file */
	private PrintWriter log = null;
	
	//TODO docu
	public RBFNet(int inputDimension,
			int outputDimension,
			int rbfNeurons,
			Vector<Pattern> trainingData,
			Vector<Pattern> testData,
			long seed) {
		this.seed = seed;
		this.inputDimension = inputDimension;
		this.outputDimension = outputDimension;
		this.trainingData = trainingData;
		this.testData = trainingData;
		if(testData != null) // if no test data is given, use training data instead
			this.testData = testData;
		this.rbfNeurons = rbfNeurons;
		
		weights = new double[rbfNeurons+1][outputDimension];
		centers = new double[rbfNeurons][inputDimension];
		widths = new double[rbfNeurons];

		initialize();
		
		resultsRBF = new double[rbfNeurons+1];
		resultsRBF[rbfNeurons] = 1.0; // BIAS fixed to 1
		resultsOutput = new double[outputDimension];
		deltaValues = new double[outputDimension];
	}
	
	/**
	 * Initializes the network.
	 */
	private void initialize() {
		Random r = new Random(seed);
		
		// initialize weights
		for(int i=0;i<weights.length;i++) {
			for(int j=0;j<weights[i].length;j++) {
				weights[i][j] = r.nextDouble()-0.5;
			}
		}
		
		// initialize widths
		for(int i=0;i<widths.length;i++) {
			widths[i] = DEFAULT_WIDTH;
		}
		
		// initialize centers (randomly from input data)
		List<Integer> indices = new ArrayList<>(trainingData.size());
		for(int i=0;i<trainingData.size();i++) {
			indices.add(i);
		}
		Collections.shuffle(indices, r);
		for(int i=0;i<centers.length;i++) {
			Pattern p = trainingData.get(indices.get(i%indices.size()));
			for(int j=0;j<centers[i].length;j++) {
				centers[i][j] = p.input[j];
			}
		}
	}
	
	/**
	 * sets the widths of all neurons to the given value
	 * @param val new width
	 */
	public void setAllWidths(double val) {
		for(int i=0;i<widths.length;i++) {
			widths[i] = val;
		}
	}
	
	/**
	 * sets the width of a single neuron to a new value
	 * @param neuron
	 * @param newWidth
	 */
	public void setWidth(int neuron, double newWidth) {
		assert(neuron < rbfNeurons);
		widths[neuron] = newWidth;
	}
	
	/**
	 * sets the center of a single neuron to a new value
	 * @param neuron
	 * @param newCenter
	 */
	public void setCenter(int neuron, double[] newCenter) {
		assert(neuron < rbfNeurons);
		assert(newCenter.length == inputDimension);
		centers[neuron] = newCenter.clone();
		
	}
	
	/**
	 * sets the weight connecting an RBF neuron with an output neuron to a new value
	 * @param rbfNeuron
	 * @param outputNeuron
	 * @param newWeight
	 */
	public void setWeight(int rbfNeuron, int outputNeuron, double newWeight) {
		assert(rbfNeuron < rbfNeurons);
		assert(outputNeuron < outputDimension);
		weights[rbfNeuron][outputNeuron] = newWeight;
	}

	/**
	 * calculates the square of the euclidian distance between two vectors
	 * @param vector
	 * @param center
	 * @return
	 */
	private double distance2(double[] vector, double[] center) {
		assert(vector.length == center.length);
		
		double sum = 0;
		for(int i=0;i<vector.length;i++) {
			sum += (vector[i]-center[i])*(vector[i]-center[i]);
		}
		return sum;
	}
	
	/**
	 * used as transfer function for an RBF neuron
	 * @param square the square of the usual input argument
	 * @return
	 */
	private double gaussian2(double square, double width) {
		return Math.exp(-square/(2*width*width));
	}
	
	/**
	 * calculates the output of the current network for a given input
	 * @param input input vector
	 * @return output vector
	 */
	public double[] calculate(double[] input) {
		assert(input.length == inputDimension);
		
		// results for RBF neurons
		for(int k=0;k<rbfNeurons;k++) {
			resultsRBF[k] = gaussian2(distance2(input,centers[k]),widths[k]);
		}
		
		// results for output neurons
		for(int m=0;m<outputDimension;m++) {
			// initialize result with BIAS
			resultsOutput[m] = weights[rbfNeurons][m];
			
			// calculate weighted sum
			for(int k=0;k<rbfNeurons;k++) {
				resultsOutput[m] += weights[k][m] * resultsRBF[k];
			}
		}
		
		return resultsOutput;
	}
	
	/**
	 * Trains the network using the training Data
	 * @param iterations how often the training set is presented
	 */
	public void train(int iterations) {
		Random r = new Random(seed);
		System.out.println("Error before training: " + globalError(trainingData));
		int progress = 0;
		for(int it=0;it<iterations;it++) {
			for(Pattern p : trainingData) {
				teach(p.input,p.output);
			}
//			System.out.println("Global error: " + globalError(trainingData));
			log(it);
			Collections.shuffle(trainingData,r);
			if((double)it/(double)iterations>(double)progress/100.0) {
				progress += 10;
				System.out.println("Training " + progress + "% complete");
			}
		}
		System.out.println("Error after training: " + globalError(trainingData));
	}
	
	/**
	 * Teaches a single pattern
	 * @param input teacher input
	 * @param teacher teacher output
	 */
	private void teach(double[] input, double[] teacher) {
		assert(input.length == inputDimension);
		assert(teacher.length == outputDimension);
		
//		System.out.println("Input: " + Arrays.toString(input) +
//				", Teacher: " + Arrays.toString(teacher) + 
//				", Output: " + Arrays.toString(calculate(input)));
		calculate(input);

		for(int m=0;m<outputDimension;m++) {
			deltaValues[m] = teacher[m]-resultsOutput[m];
			for(int k=0;k<=rbfNeurons;k++) {
				assert(resultsRBF[rbfNeurons] == 1.0);
				weights[k][m] += learingRate*deltaValues[m]*resultsRBF[k];
			}
		}
	}
	
	/**
	 * Prints the test data and for each pattern the current output of the network
	 */
	public void test() {
		for(Pattern p : testData) {
			System.out.println("Input: " + Arrays.toString(p.input) +
			", Teacher: " + Arrays.toString(p.output) + 
			", Output: " + Arrays.toString(calculate(p.input)));
		}
	}
	
	/**
	 * Calculates the mean error over a set of test patterns. 
	 * @param patternSet test patterns
	 * @return mean error
	 */
	private double globalError(Vector<Pattern> patternSet) {
		if(patternSet == null) patternSet = testData;
		double err = 0;
		for(Pattern p : patternSet) {
			calculate(p.input);
			for(int i=0;i<outputDimension;i++) {
				err += (p.output[i]-resultsOutput[i])*(p.output[i]-resultsOutput[i]);
			}
		}
		return err/patternSet.size();
	}
	
	/**
	 * @return mean error over the set of training patterns
	 */
	public double globalErrorTraining() {
		return globalError(trainingData);
	}
	
	/**
	 * Writes the current error over the training and test data in a log file 
	 * @param iteration current batch
	 */
	private void log(int iteration) {
		if(log == null) {
			try {
				log = new PrintWriter("learning.curve", "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		
		log.println(iteration + " " + globalError(trainingData) + " " + globalError(testData));
	}
	
	/**
	 * Reads test patterns from a file
	 * @param fileName file name/ path
	 * @param inputDimension dimension of the input space
	 * @param outputDimension dimension of the output space
	 * @return vector containing one training per entry
	 */
	public static Vector<Pattern> readPatternsFromFile(String fileName, int inputDimension, int outputDimension) {
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
	
	public String toString() {
		String s = "--- RBF net ---"
				+ "\ninput dimension = " + inputDimension
				+ "\noutput dimension = " + outputDimension
				+ "\nRBF neurons = " + rbfNeurons;
		for(int i=0;i<rbfNeurons;i++) {
			s += "\ncenter[" + i + "]=" + Arrays.toString(centers[i]) + ", width=" + widths[i];
		}
		
		s += "\nweight matrix:";
		for(int i=0;i<outputDimension;i++) {
			s += "\noutput neuron[" + i + "]: ";
			for(int j=0;j<rbfNeurons;j++) {
				if(j==rbfNeurons-1) s += ", bias=";
				s += weights[j][i] + " ";
			}
		}
		
		return s;
	}
	
	/**
	 * Demonstration what can be done with the network
	 */
	public void demo() {
		System.out.println("--> DEMO STARTED <--");
		System.out.println("The network is initialized and looks like this.");
		System.out.println(toString());
		System.out.println("\nWe can adjust the centers.");
		for(int i=0;i<rbfNeurons;i++) {
			double[] newCenter = centers[i].clone();
			for(int j=0;j<newCenter.length;j++) {
				newCenter[j] = Math.round(newCenter[j]);
			}
			setCenter(i, newCenter);
		}
		System.out.println(toString());
		System.out.println("\nWe can adjust the widths.");
		for(int i=0;i<rbfNeurons;i++) {
			setWidth(i, 0.1*(i+1));
		}
		System.out.println(toString());
		System.out.println("\nAnd we can adjust the weights.");
		Random r = new Random();
		for(int i=0;i<rbfNeurons;i++) {
			for(int j=0;j<outputDimension;j++) {
				setWeight(i,j,r.nextDouble()-0.5);
			}
		}
		System.out.println(toString());
		System.out.println("The training data consists of " + trainingData.size() + " patterns.");
		System.out.println("Currently the average error of each pattern is " + globalErrorTraining());
		int iterations = 5000;
		System.out.println("Lets see how this changes after " + iterations + " training sessions.");
		train(iterations);
		System.out.println("This is achieved only by adjusting the weights. The current network looks like this");
		System.out.println(toString());
		System.out.println("--> DEMO complete <--");
	}
	
	public static void main(String[] args) {
		
		int inputDimension = 4;
		int outputDimension = 2;
		String trainingFile = "training1.dat";
		String testFile = trainingFile;
		int rbfNeurons = 8;
		double widths = 0.5;
		long seed = 42;
		int learningIterations = 50000;
		
		RBFNet net = new RBFNet(
				inputDimension,
				outputDimension,
				rbfNeurons,
				readPatternsFromFile(trainingFile, inputDimension, outputDimension),
				readPatternsFromFile(testFile, inputDimension, outputDimension),
				seed);
		net.setAllWidths(widths);

		net.demo();
//		System.out.println("Initial");
//		net.test();
//		System.out.println(net.toString());
//		net.train(learningIterations);
//		System.out.println("After training");
//		net.test();
	}

}
