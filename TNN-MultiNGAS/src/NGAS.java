import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class NGAS {
	/** seed used for random number generator */
	private static long SEED = 42;
	/**
	 * sets the seed used for the random number generator
	 * @param seed
	 */
	public static void setSeed(long seed) {SEED = seed;}
	/**
	 * @return current seed
	 */
	public static long getSeed() {return SEED;}
	/** global random number generator */
	private static Random RAND_GEN = null;
	
	/**
	 * Implements a single neuron of the neural GAS.
	 */
	private class Neuron implements Comparable<Neuron> {
		/** center vector */
		double[] center;
		
		/** response to the last presented stimulus */
		double response = -1;
		
		/**
		 * Initializes a new neuron. Center is randomly taken from the unit cube.
		 * @param dimension dimension of the input area
		 */
		public Neuron(int dimension) {
			center = new double[dimension];
			for(int i=0;i<center.length;i++){
				center[i] = RAND_GEN.nextDouble();
			}
		}
		
		/** 
		 * calculates the response of the given neuron as the Euclidian distance to the center
		 * @param stimulus
		 * @return distance to the center
		 */
		public double presentStimulus(double[] stimulus) {
			assert(center.length == stimulus.length);
			response = 0;
			for(int i=0;i<center.length;i++) {
				response += (center[i]-stimulus[i])*(center[i]-stimulus[i]);
			}
			response = Math.sqrt(response);
			return response;
		}
		
		/**
		 * applies the learning rule to the center
		 * @param eta learning rate
		 * @param h neighborhood
		 * @param stimulus
		 */
		public void adjustCenter(double eta, double h, double[] stimulus) {
			for(int i=0;i<center.length;i++) {
				center[i] += eta*h*(stimulus[i]-center[i]);
			}
		}

		@Override
		public int compareTo(Neuron other) {
			if(response<other.response)
				return -1;
			if(response>other.response)
				return 1;
			return 0;
		}
	}
	
	/** list of all neurons */
	private ArrayList<Neuron> neurons;
	
	/** initial learning rate */
	private double etaInit;
	/** final learning rate */
	private double etaFinal;
	/** maximal time for training */
	private double timeMax;
	/** initial size for the neighborhood function */
	private double sigmaInit;
	/** final size for the neighborhood function */
	private double sigmaFinal;
	
	/**
	 * Initializes a new NGAS. Centers are initializes randomly from the unit cube
	 * @param dimension dimension of the input area
	 * @param numberOfNeurons number of neurons to be used
	 * @param etaInit initial learning rate
	 * @param etaFinal final learning rate
	 * @param timeMax time after which the learning rate reaches the final value
	 * @param sigmaInit initial size for the Gaussian which is used as neighborhood function
	 * @param sigmaFinal final size for the Gaussian which is used as neighborhood function
	 */
	public NGAS( int dimension,
			int numberOfNeurons,
			double etaInit,
			double etaFinal,
			double timeMax,
			double sigmaInit,
			double sigmaFinal) {
		if(RAND_GEN == null)
			RAND_GEN = new Random(SEED);
		
		neurons = new ArrayList<Neuron>(numberOfNeurons);
		for(int i=0;i<numberOfNeurons;i++)
			neurons.add(new Neuron(dimension));
		
		this.etaInit = etaInit;
		this.etaFinal = etaFinal;
		this.timeMax = timeMax;
		this.sigmaInit = sigmaInit;
		this.sigmaFinal = sigmaFinal;
		
	}
	
	/**
	 * returns the response of the neuron with the closest center to the presented stimulus
	 * @param stimulus
	 */
	public double response(double[] stimulus) {
		double minSoFar=-1, curr;
		for(Neuron neuron:neurons) {
			curr = neuron.presentStimulus(stimulus);
			if(curr<minSoFar || minSoFar<0)
				minSoFar = curr;
		}
		return minSoFar;
	}
	
	/**
	 * adjusts the centers of all neurons according to the presented stimulus
	 * @param stimulus
	 * @param time
	 */
	public void teach(double[] stimulus, double time) {
		for(Neuron neuron:neurons) {
			neuron.presentStimulus(stimulus);
		}
		
		Collections.sort(neurons);
		
		double eta = eta(time);
		
		for(int dist=0;dist<neurons.size();dist++) {
			neurons.get(dist).adjustCenter(eta, h(dist,time), stimulus);
		}
	}
	
	/**
	 * implements the neighborhood function as a Gaussian
	 * @param distance
	 * @param time
	 * @return
	 */
	private double h(double distance, double t) {
		return Math.exp(-0.5*((distance*distance)/(sigma(t)*sigma(t))));
	}
	
	/**
	 * function implementing the learning rate decaying over time
	 * @param t current time
	 * @return learning rate
	 */
	private double eta(double t) {
		if(t > timeMax)
			return etaFinal;
		return etaInit*Math.pow(etaFinal/etaInit, t/timeMax);
	}
	
	/**
	 * size of the Gaussian
	 * @param t
	 * @return
	 */
	private double sigma(double t) {
		if(t > timeMax)
			return sigmaFinal;
		return sigmaInit*Math.pow(sigmaFinal/sigmaInit, t/timeMax);
	}
	
	/**
	 * saves the centers of the net to a file
	 * @param file
	 */
	public void centersToFile(PrintWriter file) {
		for(Neuron n:neurons) {
			for(double coord:n.center) {
				file.print(coord + " ");
			}
			file.println();
		}
	}

	@Override
	public String toString() {
		String s = "";
		s = neurons.size() + " neurons\n"
				+ "learning rate from " + etaInit + " to " + etaFinal + " after time " + timeMax + "\n"
				+ "sigma (gaussian) from " + sigmaInit + " to " + sigmaFinal + "\n";
		s += "CENTERS\n";
		for(Neuron neuron:neurons) {
			s += Arrays.toString(neuron.center) + "\n";
		}
		return s;
	}
}
