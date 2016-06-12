import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Implements an evolutionary algorithm to find the longest tour that visits
 * each city of a given city-list exactly twice. The different parts of an
 * evolutionary algorithm are implemented as follows:
 * 
 * Initialization:     Random permutation of a tour that contains every city exactly twice
 * External Selection: Discard the worst {@link #_lambda} tours
 * Parent Selection:   Uniform randomly picked from the best {@link #_mu} tours
 * Inheritance:        Exact copy of a single parent
 * Mutation:           Swap of two uniform randomly picked cities in the tour.
 *                     Mutation is not applied to the parents, but to all other tours.
 * Fitness Evaluation: Summation of the distances of consecutive cities in the tour
 * Termination:        By choice of the user. Type 'stop' in command line. 
 * 
 * @author Martin
 *
 */
public class Population implements Runnable {
	
	/** simulation runs as long as this variable is true */
	private volatile boolean isRunning = true;
	/** best tour */
	private volatile double best;
	/** worst tour */
	private volatile double worst;
	/** arithmetic mean of all tours */
	private volatile double mean;
	/** counter for generations */
	private volatile int generation = 0;
	/** contains the best of all tours */
	private volatile Tour bestTour;
	/** population size */
	private int _P;
	/** number of parents */
	private int _mu;
	/** number of offspring */
	private int _lambda;
	/** population */
	private ArrayList<Tour> _pop;
	/** matrix containing all distances between cities */
	private double[][] _distanceMatrix;
	
	/**
	 * initializes a new population
	 * 
	 * @param P population size
	 * @param mu number of parents
	 * @param lambda number of offspring
	 * @param cities list containing all cities
	 */
	Population(int P, int mu, int lambda, ArrayList<City> cities) {
		_P = P;
		_mu = mu;
		_lambda = lambda;
		
		// calculate distance matrix
		_distanceMatrix = new double[cities.size()][cities.size()];
		for(int i=0;i<cities.size();i++) {
			for(int j=0;j<cities.size();j++) {
				_distanceMatrix[i][j] = cities.get(i).distanceTo(cities.get(j));
			}
		}
		
		// initialize population
		_pop = new ArrayList<Tour>(_P);
		for(int i=0;i<_P;i++) {
			_pop.add(new Tour(cities,_distanceMatrix));
		}
	}
	
	/**
	 * @return length of the best tour
	 */
	public double getBest() {
		return best;
	}
	/**
	 * @return length of the worst tour
	 */
	public double getWorst() {
		return worst;
	}
	
	/**
	 * @return arithmetic mean of all tour lengths
	 */
	public double getMean() {
		return mean;
	}
	
	/**
	 * @return number of generations
	 */
	public int getGenerations() {
		return generation;
	}
	
	/**
	 * implicitly by ordering the list of all tours in a decreasing order
	 * all but the worst lambda individuals survive
	 */
	private void externalSelection() {
		Collections.sort(_pop);
		bestTour = _pop.get(0);
	}
	
	/**
	 * a parent is randomly selected from one of the best mu individuals
	 * 
	 * @return index of the parent
	 */
	private int parentSelection() {
		return (int) (_mu*Math.random());
	}
	
	/**
	 * each of the last lambda individuals is replaced by an offspring
	 * an offspring is just a copy of one parent
	 */
	private void inheritance() {
		for(int i=(_P-_lambda);i<_P;i++) {
			_pop.set(i, new Tour(_pop.get(parentSelection())));
		}
	}
	
	/**
	 * all non-parents mutate
	 */
	private void mutation() {
		for(int i=_mu;i<_P;i++) {
			_pop.get(i).mutate();
		}
	}
	
	/**
	 * the fitness evaluation only needs to be done for mutated individuals
	 * and is already done in the mutation method of a tour
	 */
	private void fitnessEvaluation() {
		
	}

	/**
	 * runs the simulation until {@link isRunning} is set to false
	 */
	@Override
	public void run() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("output.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		while(isRunning) {
			externalSelection();
			best = _pop.get(0).length();
			worst =  _pop.get(_P-1).length();
			calcMean();
			if(generation < 20) { // the first 20 generations are printed to the standard output 
				printStatus();
			}
			if(generation < 1000) { // the first 1000 generations are saved in a text file
				writer.println(generation + " " + best + " " + worst + " " + mean);
			}
			inheritance();
			mutation();
			fitnessEvaluation();
			generation++;
		}
		
		writer.close();
	}
	
	/**
	 * calculates the arithmetic mean of all tours
	 */
	private void calcMean() {
		double sum = 0;
		for(Tour t : _pop) {
			sum += t.length();
		}
		mean =  sum/_P;
	}
	
	/** 
	 * stops the simulation
	 */
	public void kill() {
		isRunning = false;
		System.out.println("stoping simulation");
	}
	
	/**
	 * outputs the best tour to the standard output
	 */
	public void printBest() {
		System.out.println(bestTour.toString());
	}
	
	/**
	 * outputs some status information to the standard output
	 */
	public void printStatus() {
		System.out.println("Best: " + (int)getBest()
		+ ", Worst: " + (int)getWorst()
		+ ", Mean: " + (int)getMean()
		+ ", Generation: " + getGenerations());
	}
	
	/**
	 * override of the toString method
	 */
	@Override
	public String toString() {
		String output = "";
		int counter = 1;
		for(Tour t : _pop) {
			output += counter++ + ":" + (int)t.length() + ", ";
		}
		return output;
	}
}
