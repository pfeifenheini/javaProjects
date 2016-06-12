

public class Point {
		public int dimension;
		public double[] coordinates;
		
		public Point(int dimension) {
			this.dimension = dimension;
			coordinates = new double[dimension];
			for(int i = 0;i<dimension;i++) {
				coordinates[i] = Math.random();
			}
		}
		
		public Point(Point toCopy) {
			dimension = toCopy.coordinates.length;
			coordinates = toCopy.coordinates.clone();
		}
		
//		public Point(double x, double y) {
//			dimension = 2;
//			coordinates = new ArrayList<Double>();
//			coordinates.add(x);
//			coordinates.add(y);
//		}
		
		public double distance(Point other) {
			double sum = 0;
			double diff;
			for(int pos=0;pos<dimension;pos++) {
				diff = coordinates[pos] - other.coordinates[pos];
				sum += diff*diff;
			}
			return Math.sqrt(sum);
		}
		
		public String toString() {
			return coordinates.toString();
		}
	}
