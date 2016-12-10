import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;

public class MultiNGAS {
	/** global random number generator */
	private static Random RAND_GEN = null;
	
	/** set of training patterns */
	private ArrayList<double[]> trainingPatterns;
	/** list of all partner nets */
	private ArrayList<NGAS> partnerNets = null;
	
	private int learningIterations;
	
	/**
	 * 
	 * @param N input dimension
	 * @param M number of partner nets
	 * @param P number of training patterns
	 * @param K number of neurons (per net)
	 * @param etaInit initial learning rate
	 * @param etaFinal final learning rate
	 * @param learningIterations how often the pattern set is taught
	 * @param sigmaInit initial size of the Gaussian (neighborhood function)
	 * @param sigmaFInal final size of the Gaussian (neighborhood function)
	 */
	public MultiNGAS(
			int N,
			int M,
			int P,
			int K,
			double etaInit,
			double etaFinal,
			int learningIterations,
			double sigmaInit,
			double sigmaFinal
			) {
		//initialize nets
		partnerNets = new ArrayList<NGAS>(M);
		for(int i=0;i<M;i++)
			partnerNets.add(new NGAS(N,K,etaInit,etaFinal,learningIterations,sigmaInit,sigmaFinal));
		
		//initialize 2 circular, non overlapping areas
		double[] center1 = new double[N];
		double[] center2 = new double[N];
		double radius = 0.25;
		for(int i=0;i<N;i++) {
			center1[i] = 0.25;
			center2[i] = 0.75;
		}
		
		//draw training patterns
		trainingPatterns = new ArrayList<double[]>(P);
		for(int i=0;i<P;i++) {
			if(i%2 == 0)
				trainingPatterns.add(randomPointInHyperSphere(center1, radius));
			else
				trainingPatterns.add(randomPointInHyperSphere(center2, radius));
		}
		Collections.shuffle(trainingPatterns, RAND_GEN);
		
		this.learningIterations = learningIterations;
	}
	
	/**
	 * Sets the training set, if a different set is supposed to be used instead the initialy generated
	 * @param trainingSet
	 */
	public void setTrainingPatterns(ArrayList<double[]> trainingSet) {
		trainingPatterns = trainingSet;
	}
	
	/**
	 * Returns a point uniformly drawn from a hyper sphere. Note that this function is non-deterministic.
	 * It generates uniformly distributed points in the hyper-cube containing the sphere and then tests
	 * whether the point lies inside the sphere.
	 * @param center coordinates of the center
	 * @param radius
	 * @return a point picked uniformly at random from the hyper sphere
	 */
	public static double[] randomPointInHyperSphere(double[] center, double radius) {
		if(RAND_GEN == null)
			RAND_GEN = new Random(NGAS.getSeed());
		
		//calculate special case immediately
		if(center.length == 2)
			return randomPointInCircle(center[0], center[1], radius);
		
		double[] point = new double[center.length];
		double length;
		do {
			length = 0;
			for(int i=0;i<center.length;i++) {
				point[i] = (2*RAND_GEN.nextDouble()-1)*radius;
				length += point[i]*point[i];
			}
		}while(Math.sqrt(length)>radius);
		
		for(int i=0;i<point.length;i++) {
			point[i] += center[i];
		}
		return point;
	}
	
	/**
	 * returns a point uniformly random from a circle with given center and radius
	 * @param centerX x-coordinate of the center
	 * @param centerY y-coordinate of the center
	 * @param radius
	 * @return array of two doubles. First entry is x-coordinate, second the x-coordinate
	 */
	public static double[] randomPointInCircle(double centerX, double centerY, double radius) {
		double[] coords = {0.0 , 0.0};
		if(RAND_GEN == null)
			RAND_GEN = new Random(NGAS.getSeed());
		
		double angle = RAND_GEN.nextDouble()*2*Math.PI;
		double distance = radius*Math.sqrt(RAND_GEN.nextDouble());
		coords[0] = distance*Math.cos(angle)+centerX;
		coords[1] = distance*Math.sin(angle)+centerY;
		
		return coords;
	}
	
	/**
	 * trains the Multi GAS with the set of training patterns
	 */
	public void startTraining() {
		double maxResponse = -1, response;
		NGAS maxResponseGAS = null;
		int progress = 0;
		for(int t=0;t<learningIterations;t++) {
			if((double)t/learningIterations>((double)progress+10)/100) {
				progress+=10;
				System.out.println("training " + progress+ "% complete");
			}
			
			for(double[] stimulus:trainingPatterns) { //pick stimulus
				for(NGAS gas:partnerNets) { //find gas with winning neuron
					response = gas.response(stimulus);
					if(response<maxResponse || maxResponse<0) {
						maxResponse = response;
						maxResponseGAS = gas;
					}
				}
				maxResponseGAS.teach(stimulus, t); //adjust centers
				maxResponse = -1;
			}
			Collections.shuffle(trainingPatterns, RAND_GEN);
		}
		System.out.println("training 100% complete");
	}
	
	/**
	 * saves the centers of each NGAS in a separate file
	 */
	public void centersToFiles() {
		int id =0;
		for(NGAS gas:partnerNets) {
			PrintWriter w = null;
			try {
				w = new PrintWriter("GAS" + id++ + ".centers", "UTF-8");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			gas.centersToFile(w);
			w.close();
		}
		
	}
	
	/**
	 * saves all centers in a single file
	 * @param file
	 */
	public void centersToFile() {
		PrintWriter centersFile = null;
		try {
			centersFile = new PrintWriter("PA-D.net", "UTF-8");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		for(NGAS gas:partnerNets) {
			gas.centersToFile(centersFile);
		}
		centersFile.close();
	}
	
	/**
	 * saves the training patterns in a file
	 */
	public void trainingToFile() {
		PrintWriter trainingFile = null;
		try {
			trainingFile  = new PrintWriter("training.patterns", "UTF-8");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		for(double[] p:trainingPatterns) {
			for(double d:p)
				trainingFile.print(d + " ");
			trainingFile.println();
		}
		trainingFile.close();
	}
	
	@Override
	public String toString() {
		String s = "";
		int num = 0;
		for(NGAS gas:partnerNets) {
			s += "Neural GAS #" + num++ + "\n";
			s += gas.toString() + "\n";
		}
		return s;
	}
	
	/**
	 * Implements a demo of a Multi NGAS.
	 * For dimension=2, patters are drawn from three areas in the unit square of the two
	 * dimensional plane. Two circles with centers (0.3,0.75) and (0.7,0.75) and radius 0.15 and
	 * a rectangle [0.2,0.8] [0.2,0.376715868]. Note that the rectangle has the same area as the circles.
	 * For dimension=3, patterns are drawn from 2 spheres with centers (0.25,0.25,0.25) and (0.75,0.75,0.75)
	 * and radius 0.25.
	 * It draws 1000 points from these areas and trains a Multi NGAS with 4 partner nets, each with 20
	 * neurons. Either the learning rate and the neighborhood function decay over time.
	 */
	public static void demo(int dimension) {
		int N = dimension;
		int M = 4;
		int P = 2500;
		int K = 20;
		double etaInit = 0.2;
		double etaFinal = 0.01;
		int trainingIterations = 100;
		double sigmaInit = 10.0;
		double sigmaFinal = 0.1;

		MultiNGAS g = new MultiNGAS(N, M, P, K, etaInit, etaFinal, trainingIterations, sigmaInit, sigmaFinal);
		
		double[] circleLeft = {0.3,0.75,0.15};
		double[] circleRight = {0.7,0.75,0.15};
		double[] rect = {0.2,0.8,0.2,0.376715868};
		
		ArrayList<double[]> trainingSet = new ArrayList<double[]>(P);
		for(int i=0;i<P;i++) {
			if(i%3 == 0) {
				trainingSet.add(randomPointInCircle(circleLeft[0], circleLeft[1], circleLeft[2]));
			}
			else if(i%3 == 1) {
				trainingSet.add(randomPointInCircle(circleRight[0], circleRight[1], circleRight[2]));
			}
			else {
				double[] point = new double[2];
				point[0] = RAND_GEN.nextDouble()*(rect[1]-rect[0])+rect[0];
				point[1] = RAND_GEN.nextDouble()*(rect[3]-rect[2])+rect[2];
				trainingSet.add(point);
			}
		}
		
		if(dimension == 2)
			g.setTrainingPatterns(trainingSet);
		
		g.trainingToFile();
		
//		System.out.println(g.toString());
		g.startTraining();
//		System.out.println(g.toString());
		
		g.centersToFile();
		g.centersToFiles();
		
	}
	
	/**
	 * @param args first argument can contain seed for random number generator, otherwise a random seed is generated
	 */
	public static void main(String[] args) {
		long seed = 42;
		if(args.length>0) {
			seed = new Scanner(args[0]).nextLong();
		}
		else {
			seed = new Random().nextLong();
		}
		NGAS.setSeed(seed);
		System.out.println("Using seed: " + seed);
			
		System.out.print("Please enter the dimension of the input area for the demo ");
		int dimension = 0;
		while(dimension<2 || dimension>3) {
			System.out.print("(2 or 3): ");
			dimension = new Scanner(System.in).nextInt();
		}
		
		if(dimension==2)
			System.out.println("Training set is drawn from two circles and a rectrangle inside the unit square.");
		else
			System.out.println("Training set is drawn from two spheres inside the unit cube.");
		
		demo(dimension);
		
		System.out.println("Please use demoPlot" + dimension + "D.gp to plot the results with gnuplot.");
	}
}
