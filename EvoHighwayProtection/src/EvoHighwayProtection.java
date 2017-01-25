import java.util.Arrays;

public class EvoHighwayProtection {

	public static int numberOfThreads = 4;
	
	private static class Simulation implements Runnable {

		private int id;
		private Strategy[] population;
		private int[][] grid;
		
		public Simulation(Strategy[] population, int id) {
			this.population = population;
			this.id = id;
			grid = new int[Strategy.xBoundary][Strategy.yBoundary];
		}
		
		@Override
		public void run() {
			for(int i=population.length/3+id;i<population.length;i+=numberOfThreads) {
				population[i] = new Strategy(population[Strategy.rand.nextInt(population.length/3)]);
				population[i].mutate(2);
				population[i].simulate(grid,false);
			}
		}
	}
	
	public static void main(String[] args) {
		int[][] grid = new int[Strategy.xBoundary][Strategy.yBoundary];
		
		Strategy[] population = new Strategy[100];
		for(int i=0;i<population.length;i++) {
			population[i] = new Strategy();
			population[i].simulate(grid,false);
		}
		
		double mean;
//		Thread t1 = new Thread(new Simulation(population,0));
//		Thread t2 = new Thread(new Simulation(population,1));
//		Thread t3 = new Thread(new Simulation(population,2));
//		Thread t4 = new Thread(new Simulation(population,3));
		
		double progress=0;
		
		long startTime = System.currentTimeMillis();
		long stopTime = System.currentTimeMillis();
		long sorting = 0, mutation = 0, simulation = 0;
		for(int it=0;it<1000;it++) {
			mean = 0;
			for(int i=0;i<population.length;i++)
				mean += population[i].fitness();
			mean = mean/population.length;
			if((double)it/1000>((double)progress+1)/100) {
				progress+=1;
				System.out.println(progress+ "% complete");
//				System.out.println(population[0].fitness() + " " + mean + " " + population[population.length-1].fitness());
			}

			startTime = System.currentTimeMillis();
			Arrays.sort(population);
			stopTime = System.currentTimeMillis();
			sorting += stopTime-startTime;
			startTime = System.currentTimeMillis();

//			System.out.println(population[0].fitness() + " " + mean + " " + population[population.length-1].fitness());
			
//			t1.run();
//			t2.run();
//			t3.run();
//			t4.run();
//			
//			try {
//				t1.join();
//				t2.join();
//				t3.join();
//				t4.join();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			for(int j=population.length/3;j<population.length;j++) {
				population[j] = new Strategy(population[Strategy.rand.nextInt(population.length/3)]);
				startTime = System.currentTimeMillis();
				population[j].mutate(2);
				population[j].simulate(grid,false);
				stopTime = System.currentTimeMillis();
				simulation += stopTime-startTime;
				startTime = System.currentTimeMillis();
			}
		}
		System.out.println("sorting:    " + sorting);
		System.out.println("simulating: " + simulation);
		
		population[0].simulate(grid, true);
		population[0].printGrid(grid);
	}

}
