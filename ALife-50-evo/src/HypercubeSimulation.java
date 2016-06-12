import java.util.*;

public class HypercubeSimulation {

	public static int N = 1000;
	public static int Z = 10000;
	public static int DistributionRes = 1000;
	public static int Tests = 2000000;
	
	
	public static double[] diff(Point x, Point y) {
		double[] diff = new double[N];
		for(int i=0;i<N;i++) {
			diff[i] = x.coordinates[i] - y.coordinates[i];
		}
		return diff;
	}
	
	public static double dotProduct(double[] v1, double[] v2) {
		double sum = 0;
		for(int i=0;i<v1.length;i++) {
			sum += v1[i]*v2[i];
		}
		return sum;
	}
	
	public static double norm(double[] v) {
		double sum = 0;
		for(int i=0;i<v.length;i++) {
			sum += v[i]*v[i];
		}
		return Math.sqrt(sum);
	}
	
	public static double angle(double[] v1, double[] v2) {
		return Math.acos(dotProduct(v1, v2)/(norm(v1)*norm(v2)));
	}
	
	public static void main(String[] args) {
		
		ArrayList<Point> points = new ArrayList<Point>(Z);
		for(int i=0;i<Z;i++) {
			points.add(new Point(N));
		}
		double averageDistance = 0;
		double averageAngle = 0;
		double varianceDist = 0;
		double varianceAng = 0;
		double standardabweichungDist = 0;
		double standardabweichungAng = 0;
		int progress = 0;
		int lastProgress = 0;
		int[] distributionDist = new int[DistributionRes];
		int[] distributionAng = new int[DistributionRes];
		double maxDistance = Math.sqrt(N);
		
		System.out.println(progress + "%");
//		for(int i=0;i<Z;i++){
//			for(int j=i+1;j<Z;j++) {
//				distance = points.get(i).distance(points.get(j));
//				distribution[(int)((distance/maxDistance)*DistributionRes)]++;
//				averageDistance += distance;
//			}
//
//			progress = (int) ((((double)i/(double)Z)*100));
//			if(progress >= lastProgress+5) {
//				lastProgress = progress;
//				System.out.println(progress + "%");
//			}
//		}
		
		int a,b,c,d;
		
		double[] distances = new double[Tests];
		double[] angles = new double[Tests];
		
		for(int i=0;i<Tests;i++) {
			a = (int)(Math.random()*Z);
			b = (int)(Math.random()*Z);
			if(a==b) {
				i--;
				continue;
			}
			distances[i] = points.get(a).distance(points.get(b));
			distributionDist[(int)((distances[i]/maxDistance)*DistributionRes)]++;
			averageDistance += distances[i];
			
			progress = (int) ((((double)i/(double)(Tests*2))*100));
			if(progress >= lastProgress+5) {
				lastProgress = progress;
				System.out.println(progress + "%");
			}
		}
		
		for(int i=0;i<Tests;i++) {
			a = (int)(Math.random()*Z);
			b = (int)(Math.random()*Z);
			c = (int)(Math.random()*Z);
			d = (int)(Math.random()*Z);
			if(a==b || c==d || (a==c && b == d)) {
				i--;
				continue;
			}
			double[] v1 = diff(points.get(a),points.get(b));
			double[] v2 = diff(points.get(c),points.get(d));
			
			angles[i] = angle(v1,v2);
			if(angles[i] > (Math.PI/2)) {
				angles[i] = Math.PI-angles[i];
			}
//			System.out.println(angle);
			distributionAng[(int)((angles[i]/(Math.PI/2))*DistributionRes)]++;
			
			averageAngle += angles[i];
			
			progress = (int) ((((double)(i+Tests)/(double)(Tests*2))*100));
			if(progress >= lastProgress+5) {
				lastProgress = progress;
				System.out.println(progress + "%");
			}
		}
		
//		averageDistance = averageDistance/((Z*(Z-1)/2));
		averageDistance = averageDistance/Tests;
		averageAngle = averageAngle/Tests;
		
		for(int i=0;i<Tests;i++) {
			varianceDist += (distances[i]-averageDistance)*(distances[i]-averageDistance);
			varianceAng += (angles[i]-averageAngle)*(angles[i]-averageAngle);
		}
		varianceDist = varianceDist/Tests;
		varianceAng = varianceAng/Tests;
		
		standardabweichungDist = Math.sqrt(varianceDist);
		standardabweichungAng = Math.sqrt(varianceAng);
		
		System.out.println("Average Distance = " + averageDistance);
		System.out.println("Average Angle = " + averageAngle);
		System.out.println("Variance Distance = " + varianceDist);
		System.out.println("Variance Angle = " + varianceAng);
		System.out.println("Standard Deviation Distance = " + standardabweichungDist);
		System.out.println("Standard Deviation Angle = " + standardabweichungAng);
		
		System.out.println("\nDistribution Dist/ Ang:\n");
		for(int i=0;i<DistributionRes;i++) {
//			if(distributionDist[i] != 0 || distributionAng[i] != 0)
				System.out.println(i + "	" + distributionDist[i] + "	" + distributionAng[i]);			
		}
	}
}
