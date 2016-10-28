import java.util.Random;

public class Perzeptron {

	private int _n;
	private int _m;
	private double[][] _weightMatrix;
	private double[] _biasWeights;
	
	public Perzeptron(int n, int m) {
		_n = n;
		_m = m;
		_weightMatrix = new double[_m][_n];
		_biasWeights = new double[_m];
//		initializeWeights();
		initializeBoolean();
	}
	
	private void initializeBoolean() {
		for(int i=0;i<_m;i++) {
			 _biasWeights[i] = 0.5;
			for(int j=0;j<_n;j++) {
				_weightMatrix[i][j] = -1; 
			}
		}
	}
	
	private void initializeWeights() {
		Random r = new Random();
		for(int i=0;i<_m;i++) {
			 _biasWeights[i] = r.nextDouble()-0.5;
			for(int j=0;j<_n;j++) {
				_weightMatrix[i][j] = r.nextDouble()-0.5; 
			}
		}
	}
	
	public double[] calculate(double[] input) {
		if(input.length != _n) return null; //input has invalid dimension
		
		double[] output = new double[_m];
		double net;
		
		for(int i=0;i<_m;i++) {
			net = 0;
			for(int j=0;j<_n;j++) {
				net += input[j]*_weightMatrix[i][j];
			}
			net += _biasWeights[i];
			if(net >= 0)
				output[i] = 1;
			else
				output[i] = 0;
		}
		
		return output;
	}
	
	public void print() {
		System.out.println("Matrix: ");
		for(int i=0;i<_m;i++) {
			for(int j=0;j<_n;j++) {
				System.out.print(_weightMatrix[i][j] + " ");
			}
			System.out.println("");
		}
		System.out.println("Bias: ");
		for(int i=0;i<_m;i++) {
			System.out.print(_biasWeights[i] + " ");
		}
		System.out.println("");
	}
}
