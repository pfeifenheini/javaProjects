import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A tour is represented as an ArrayList containing the cities in the
 * order in which they are visited. Each city is visited exactly twice.
 * To avoid multiple recalculations of the distance between two cities,
 * the distances need to be provided in a 2-dimensional distance matrix
 * in which the entry (i,j) contains the distance between city i and city j. 
 * 
 * @author Martin Kretschmer
 * @author Roland Meneghetti
 */
public class Tour implements Comparable<Tour>{
	
	/** cities are visited according to the order in this list */
	private ArrayList<City> _tour;
	/** total length of the tour */
	private double _length;
	/** the distance matrix */
	private double[][] _distanceMatrix;
	
	/**
	 * initializes a new tour
	 * 
	 * @param cities list containing all cities
	 * @param distanceMatrix matrix containing all distances between cities
	 */
	Tour(ArrayList<City> cities,double[][] distanceMatrix) {
		_tour = new ArrayList<City>(cities); // copy city list
		_tour.addAll(cities); // add city list, now every city is contained twice
		Collections.shuffle(_tour); // random permutation
		_distanceMatrix = distanceMatrix;
		_length = calcLength();
	}
	
	/**
	 * creates a new tour as a copy of a parent tour
	 * 
	 * @param parent tour to copy
	 */
	Tour(Tour parent) {
		_tour = new ArrayList<City>(parent._tour);
		_distanceMatrix = parent._distanceMatrix;
		_length = parent.calcLength();
	}
	
	/**
	 * swaps two uniform randomly picked cities
	 */
	public void mutate() {
		int i = (int) (_tour.size()*Math.random());
		int j = (int) (_tour.size()*Math.random());
		Collections.swap(_tour, i, j);
		_length = calcLength();
	}
	
	/**
	 * calculates the length of the tour
	 * 
	 * @return length of the tour 
	 */
	private double calcLength() {
		double length = 0;
		City last, current;
		for(int i=1;i<_tour.size();i++) {
			last = _tour.get(i-1);
			current = _tour.get(i);
			length += _distanceMatrix[last.id-1][current.id-1];
		}
		return length;
	}
	
	/**
	 * @return length of the tour
	 */
	public double length() {
		return _length;
	}
	
	/**
	 * override of toString method
	 */
	@Override
	public String toString() {
		String output = _length + " | ";
		for(City c : _tour) {
			output += " " + c.id;
		}
		return output;
	}

	/**
	 * implementation of Compatable interface
	 * 
	 * @param t tour
	 */
	@Override
	public int compareTo(Tour t) {
		if(_length < t._length)
			return 1;
		if(_length > t._length)
			return -1;
		return 0;
	}
	/**
	 * first line is the length of the tour
	 * after that one city per line in visited order
	 */
	public void save() {
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("bestTour.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		writer.println(_length);
		System.out.println(_length);
		for(City c : _tour) {
			writer.println(c.id + " " + c.x + " " + c.y);
			System.out.println(c.id + " " + c.x + " " + c.y);
		}
		
		writer.close();
	}
}
