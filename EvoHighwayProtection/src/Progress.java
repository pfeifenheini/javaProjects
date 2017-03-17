
public class Progress {
	private int totalSteps;
	
	private long startTime;
	
	public Progress(int totalSteps) {
		this.totalSteps = totalSteps;
	}
	
	public void start() {
		startTime = System.currentTimeMillis();
	}
	
	public void progress(int step) {
		
	}
}
