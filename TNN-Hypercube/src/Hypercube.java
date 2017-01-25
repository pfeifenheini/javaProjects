import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;

public class Hypercube {

	/** random number generator */
	Random rand;
	/** seed used by random number generator */
	long seed;
	
	/** sample size */
	int P;
	/** dimension of hypercube */
	int d;
	/** set of random vectors, W[i] is the i-th vector and W[i][j] its j-th entry */
	double[][] W;
	
	/** resolution for the generated histograms */
	int histogramResolution = 20;
	
	/** mean (expected) length of the vectors */ 
	double L;
	/** smallest length among all vectors */
	double LMin;
	/** largest length among all vectors */
	double LMax = 0;
	/** histogram of vector lengths */
	int[] LHisto;
	
	/** mean (expected) angle between each vector and the diagonal vector (i.e. [1,1,1,1,...,1]) */
	double alpha;
	/** smallest angle among all vectors */
	double alphaMin;
	/** largest angle among all vectors */
	double alphaMax = 0;
	/** histogram of angles */
	int[] alphaHisto;
	
	/** mean (expected) distance between two vectors */
	double E;
	/** smallest distance among all vectors */
	double EMin;
	/** largest distance among all vectors */
	double EMax = 0;
	/** histogram of distances */
	int[] EHisto;
	
	/**
	 * Initializes a set of vectors randomly drawn from the unit-hypercube
	 * @param P Number of samples
	 * @param d Dimension of the hypercube
	 * @param seed Seed used by random number generator
	 */
	public Hypercube(int P, int d, long seed) {
		this.P = P;
		this.d = d;
		this.seed = seed;
		rand = new Random(seed);
		W = new double[P][d];
		System.out.println("Start program with parameters");
		System.out.println("P = " + P);
		System.out.println("d = " + d);
		System.out.println("seed = " + seed);
		System.out.println("Initialize random vectors");
		for(int i=0;i<P;i++) {
			for(int j=0;j<d;j++) {
				W[i][j] = rand.nextDouble();
			}
		}
		LMin = Math.sqrt(d);
		alphaMin = Math.PI;
		EMin = LMin; 
	}
	
	/**
	 * Calculates the euclidian length of a vector
	 * @param v input vector
	 * @return euclidian length
	 */
	private double vectorLength(double[] v) {
		double length = 0;
		for(double value:v) {
			length += value*value;
		}
		return Math.sqrt(length);
	}
	
	/**
	 * calculates the angle (value between 0.0 and PI) between a vector and the space diagonal
	 * @param v input vector
	 * @param length length of the input vector
	 * @return angle between v and space diagonal
	 */
	private double angleToDiagonal(double[] v, double length) {
		double sum = 0;
		for(double value:v) {
			sum += value;
		}
		sum = sum/(length*Math.sqrt(d));
		return Math.acos(sum);
	}
	
	/**
	 * calculates euclidian distance between two vectors
	 * @param v1 first input vector
	 * @param v2 second input vector
	 * @return distance between v1 and v2
	 */
	private double euclidianDistance(double[] v1, double[] v2) {
		assert(v1.length == v2.length);
		double sum = 0;
		for(int i=0;i<v1.length;i++) {
			sum += (v1[i]-v2[i])*(v1[i]-v2[i]);
		}
		return Math.sqrt(sum);
	}
	
	/**
	 * calculates the mean values for the lengths, angles and distances and their distributions
	 * which are saved in a histogram.
	 */
	public void calculateDistributions() {
		double length, angle, distance;
		double[] lengths = new double[P];
		double[] angles = new double[P];
		double[][] distances = new double[P][P];
		L=0;
		alpha=0;
		E=0;
		
		System.out.println("Calculate distributions");
		int progress = 0;
		for(int i=0;i<P;i++) {
			if((double)i/P>((double)progress+10)/100) {
				progress+=10;
				System.out.println(progress+ "%");
			}
			
			length = vectorLength(W[i]);
			angle = angleToDiagonal(W[i], length);
			
			lengths[i] = length;
			angles[i] = angle;
			
			L += length;
			alpha += angle;
			
			if(length > LMax)
				LMax = length;
			if(length < LMin)
				LMin = length;
			if(angle > alphaMax)
				alphaMax = angle;
			if(angle < alphaMin)
				alphaMin = angle;
			
			for(int j=i+1;j<P;j++) {
				distance = euclidianDistance(W[i], W[j]);
				distances[i][j] = distance;
				E += distance;
				
				if(distance > EMax)
					EMax = distance;
				if(distance < EMin)
					EMin = distance;
			}
		}
		L = L/P;
		alpha = alpha/P;
		E = E/(P*(P-1)/2);

		
		LHisto = new int[histogramResolution];
		alphaHisto = new int[histogramResolution];
		EHisto = new int[histogramResolution];
		int index;
		for(int i=0;i<P;i++) {
			index = (int) ((lengths[i]-LMin)*((histogramResolution)/(LMax-LMin)));
			LHisto[Math.min(index, histogramResolution-1)]++;
			
			index = (int) ((angles[i]-alphaMin)*((histogramResolution)/(alphaMax-alphaMin)));
			alphaHisto[Math.min(index, histogramResolution-1)]++;
			for(int j=i+1;j<P;j++) {
				index = (int) ((distances[i][j]-EMin)*((histogramResolution)/(EMax-EMin)));
				EHisto[Math.min(index, histogramResolution-1)]++;
			}
		}
		System.out.println("Mean length = " + L);
		System.out.println("range from " + LMin + " to " + LMax);
		System.out.println("Histogram " + Arrays.toString(LHisto));
		
		System.out.println("\nMean angle = " + alpha);
		System.out.println("range from " + alphaMin + " to " + alphaMax);
		System.out.println("Histogram " + Arrays.toString(alphaHisto));
		
		System.out.println("\nMean distance = " + E);
		System.out.println("range from " + EMin + " to " + EMax);
		System.out.println("Histogram " + Arrays.toString(EHisto));
	}
	
	public void saveDistributions() {
		PrintWriter lengthFile = null;
		PrintWriter angleFile = null;
		PrintWriter distanceFile = null;
		try {
			lengthFile = new PrintWriter("length.histogram","UTF-8");
			angleFile = new PrintWriter("angle.histogram","UTF-8");
			distanceFile = new PrintWriter("distance.histogram","UTF-8");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		for(int i=0;i<histogramResolution;i++) {
			lengthFile.println(LMin+i*((LMax-LMin)/histogramResolution) + " " + (double)LHisto[i]/P);
			angleFile.println(alphaMin+i*((alphaMax-alphaMin)/histogramResolution) + " " + (double)alphaHisto[i]/P);
			distanceFile.println(EMin+i*((EMax-EMin)/histogramResolution) + " " + (double)EHisto[i]/(P*(P-1)/2));
		}
		lengthFile.close();
		angleFile.close();
		distanceFile.close();
	}
	
	public static void main(String[] args) {
//		long seed = new Random().nextLong();
		long seed = 42;
		Hypercube h = new Hypercube(10000,1000,seed);
		h.calculateDistributions();
		h.saveDistributions();
	}

}
