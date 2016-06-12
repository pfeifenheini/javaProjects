
public class City {
	/** city id */
	public int id;
	/** x coordinate */
	public int x;
	/** y coordinate */
	public int y;
	
	/**
	 * constructor
	 * 
	 * @param id unique id
	 * @param x x-coordinate
	 * @param y y-coordinate
	 */
	City(int id, int x, int y) {
		this.id = id;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * calculates the distance to an other city
	 * 
	 * @param other city
	 * @return distance to 'other' city
	 */
	public double distanceTo(City other) {
		return Math.sqrt((x-other.x)*(x-other.x) + (y-other.y)*(y-other.y));
	}
	
	/**
	 * override of toString method
	 */
	@Override
	public String toString() {
		return id + ": (" + x + "," + y + ")";
	}
	
}
