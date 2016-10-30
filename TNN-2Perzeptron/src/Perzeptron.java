import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * implements a simple 2-layer Perzeptron.
 * @author Martin Kretschmer
 *
 */
public class Perzeptron {

	public static enum TransferFunction {
		BINARY, FERMI, TANH
	}
	/** input dimension */
	private int _n;
	/** output dimension */
	private int _m;
	/** weight matrix */
	private double[][] _weightMatrix;
	/** bias weights */
	private double[] _biasWeights;
	/** used transfer function */
	private TransferFunction _transfer;
	
	/**
	 * initializes a new network with weights read from a file
	 * @param inputDimension
	 * @param outputDimension
	 * @param transfer
	 * @param fileWithWeights
	 */
	public Perzeptron(int inputDimension, int outputDimension, TransferFunction transfer, String fileWithWeights) {
		initialize(inputDimension, outputDimension, transfer);
		initializeFromFile(fileWithWeights);
	}
	
	/**
	 * initializes a new network with random weights
	 * @param inputDimension
	 * @param outputDimension
	 * @param transfer
	 */
	public Perzeptron(int inputDimension, int outputDimension, TransferFunction transfer) {
		initialize(inputDimension, outputDimension, transfer);
		initializeRandomWeights();
	}
	
	/**
	 * initializes a new network
	 * @param inputDimension
	 * @param outputDimension
	 * @param transfer
	 */
	private void initialize(int inputDimension,int outputDimension, TransferFunction transfer) {
		_n = inputDimension;
		_m = outputDimension;
		_weightMatrix = new double[_m][_n];
		_biasWeights = new double[_m];
		_transfer = transfer;
	}
	
	/**
	 * initializes the weight matrix and bias randomly
	 * with values between -0.5 and 0.5
	 */
	private void initializeRandomWeights() {
		Random r = new Random();
		for(int i=0;i<_m;i++) {
			 _biasWeights[i] = r.nextDouble()-0.5;
			for(int j=0;j<_n;j++) {
				_weightMatrix[i][j] = r.nextDouble()-0.5; 
			}
		}
	}
	
	/**
	 * reads network weights from a file
	 * @param fileName path to the file
	 */
	private void initializeFromFile(String fileName) {
		BufferedReader br = null;
		String line;
		Scanner s;
		
		try {
			br = new BufferedReader(new FileReader(fileName));
			while((line = br.readLine()) != null) {
				s = new Scanner(line);
				//TODO implement reading of the weights depending on file format
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
	 * utilizes the network to process an acutal input
	 * @param input input array (needs to have the correct dimension)
	 * @return output array the network has calculated
	 */
	public double[] calculate(double[] input) {
		if(input.length != _n) return null; //input has invalid dimension
		
		double[] output = new double[_m];
		double net;
		
		for(int i=0;i<_m;i++) { //for each neuron in the output layer
			net = 0;
			for(int j=0;j<_n;j++) {
				net += input[j]*_weightMatrix[i][j]; //calculate the weighted sum
			}
			net += _biasWeights[i]; //add BIAS weight
			
			//use chose transfer function to calculate output of the neuron
			if(_transfer == TransferFunction.BINARY) {
				if(net >= 0)
					output[i] = 1;
				else
					output[i] = 0;
			} else if(_transfer == TransferFunction.FERMI) {
				output[i] = 1/(1+Math.exp(-net)); //Calculate output using Fermi-Function
			} else {
				double a = Math.exp(net);
				double b = Math.exp(-net);
				output[i] = (a-b)/(a+b);
			}
		}
		
		return output;
	}
	
	/**
	 * trains the network with patters read from a file
	 * @param fileName path to the file
	 */
	public void train(String fileName) {
		BufferedReader br = null;
		String line;
		Scanner s;
		double[] trainInput = new double[_n];
		double[] trainOutput = new double[_m];
		try {
			br = new BufferedReader(new FileReader(fileName));
			while((line = br.readLine()) != null) {
				s = new Scanner(line);
				for(int i=0;i<_n;i++) {
					trainInput[i] = s.nextDouble();
				}
				for(int i=0;i<_m;i++) {
					trainOutput[i] = s.nextDouble();
				}
				teach(trainInput,trainOutput);
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
		if(teacherInput.length != _n) return; //input has invalid dimension
		if(teacherOutput.length != _m) return; //output has invalid dimension
		
		System.out.print("Teach : ");
		for(int i=0;i<_n;i++)
			System.out.print(teacherInput[i] + " ");
		System.out.print("  |  ");
		for(int i=0;i<_m;i++)
			System.out.print(teacherOutput[i] + " ");
		System.out.println();
		
		//TODO implement teaching mechanism
	}
	
	/**
	 * calculates the error between two vectors
	 * @param vec1 first vector
	 * @param vec2 second vector
	 * @return error (non negative real value)
	 */
	public double error(double[] vec1,double[] vec2) {
		if(vec1.length != vec2.length) return 0;
		double diff=0;
		for(int i=0;i<vec1.length;i++) {
			diff += (vec1[i]-vec2[i])*(vec1[i]-vec2[i]);
		}
		return diff/2;
	}
	
	/**
	 * prints the current weight matrix and bias weights to the console
	 */
	public String toString() {
		String s = "";
		s += "Matrix: \n";
		for(int i=0;i<_m;i++) {
			for(int j=0;j<_n;j++) {
				s += _weightMatrix[i][j] + " ";
			}
			s += "\n";
		}
		s += "Bias: \n";
		for(int i=0;i<_m;i++) {
			s += _biasWeights[i] + " ";
		}
		s += "\n";
		return s;
	}
}
