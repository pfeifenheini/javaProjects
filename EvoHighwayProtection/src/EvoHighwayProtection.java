import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class EvoHighwayProtection implements Runnable {

	public static int numberOfThreads = 1;
	
	public static final int populationSize = 200; //300
	public static final int totalIterations = 500000; //400000
	public static final double parentRatio = 0.2; 
	
	private static String lock = new String();
	
	public static boolean printSteps = false;
	
	public static boolean keepRunning = true;
	
	public static boolean save = false;
	
	public static void main(String[] args) {
		EvoHighwayProtection e = new  EvoHighwayProtection();
		e.run();
		
//		Thread[] t = new Thread[numberOfThreads];
//		for(int i=0;i<t.length;i++) {
//			t[i] = new Thread(new EvoHighwayProtection());
//			t[i].start();
//		}
//		for(int i=0;i<t.length;i++) {
//			try {
//				t[i].join();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
	
	public int rouletteSelect(int upperBound) {
		// calculate the total weight
		double n = upperBound*(upperBound+1)/2;

		// get a random value
		double value = Strategy.rand.nextDouble() * n;	
		// locate the random value based on the weights
		for(int i=0; i<=upperBound; i++) {		
			value -= upperBound-i;		
			if(value <= 0) return i;
		}
		// when rounding errors occur, we return the last item's index 
		return upperBound;
	}

	@Override
	public void run() {
		Strategy[] population = new Strategy[populationSize];
		for(int i=0;i<population.length;i++) {
			population[i] = new Strategy();
			population[i].simulate(false,false);
		}
		
		double mean;
		
		
		
		Thread t = new Thread(new Runnable() {
			public void run() {
				Scanner input = new Scanner(System.in);
				while(keepRunning) {
					if(input.nextLine().equalsIgnoreCase("print"))
						printSteps = true;
					if(input.nextLine().equalsIgnoreCase("stop"))
						keepRunning = false;
					if(input.nextLine().equalsIgnoreCase("save"))
						save = true;
				}
				input.close();
			}
		});
		t.start();
		
		int progress=0, lastIteration=0;
		long lastTime = System.currentTimeMillis();
		long remainingTime, second, minute, hour;
		double worstFitness, averageFitness;
//		for(int it=0;it<totalIterations;it++) {
		for(int it=0;keepRunning;it++) {
			mean = 0;
			for(int i=0;i<population.length;i++)
				mean += population[i].fitness();
			mean = mean/population.length;

			Arrays.sort(population);
			
//			System.out.println("###### POPULATION START #####");
//			for(int i=0;i<population.length;i++) {
//				population[i].printGrid();
//			}
//			System.out.println("###### POPULATION END #####");
			
//			try {
//				System.in.read();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			worstFitness = 0;
			averageFitness = 0;
			for(int i=0;i<(int)(populationSize*parentRatio);i++) {
				averageFitness += population[i].fitness();
				if(population[i].fitness()>worstFitness)
					worstFitness = population[i].fitness(); 
			}
			averageFitness = averageFitness/((int)(populationSize*parentRatio));
			
//			System.out.println(population[0].fitness() + " " + averageFitness + " " + worstFitness);
//			population[0].printGrid();
//			population[2].printGrid();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			if(System.currentTimeMillis()-lastTime >= 10000) {
				progress=(int)((double)it/totalIterations*100);
				System.out.print(progress+ "% complete. ");
	//				System.out.println(population[0].fitness() + " " + mean + " " + population[population.length-1].fitness());
				remainingTime = (long)((((System.currentTimeMillis()-lastTime)/1000)/(double)(it-lastIteration))*(totalIterations-it));
				second = remainingTime%60;
				minute = (remainingTime/60)%60;
				hour = (remainingTime/3600)%24;
				System.out.println(String.format("%02d:%02d:%02d remain", hour, minute, second));
//				population[0].printGrid();
				
				if(printSteps) {
					population[0].simulate(true, true);
					printSteps = false;
				}
					
				if(save) {
					population[0].save("best");
					save = false;
				}
				population[0].printGrid();
				
				worstFitness = 0;
				averageFitness = 0;
				for(int i=0;i<(int)(populationSize*parentRatio);i++) {
					averageFitness += population[i].fitness();
					if(population[i].fitness()>worstFitness)
						worstFitness = population[i].fitness(); 
				}
				averageFitness = averageFitness/((int)(populationSize*parentRatio));
				
				System.out.println("average: " + averageFitness);
				System.out.println("worst: " + worstFitness);
				lastTime = System.currentTimeMillis();
				lastIteration = it;
			}

//			System.out.println(population[0].fitness() + " " + mean + " " + population[population.length-1].fitness());
			
			int parent1, parent2;
			for(int j=(int)(populationSize*parentRatio);j<population.length;j++) {
				//copy
//				population[j] = new Strategy(population[rouletteSelect((int)(populationSize*parentRatio))]);
				
				//mixing
				parent1 = rouletteSelect((int)(populationSize*parentRatio));
				do{
					parent2 = rouletteSelect((int)(populationSize*parentRatio));
				} while (parent1 == parent2);
				population[j] = new Strategy(population[parent1],population[parent2]);
				
				population[j].mutate();
				population[j].simulate(false,false);
				
//				System.out.println("\n### parent 1: " + parent1);
//				population[parent1].printGrid();
//				System.out.println("\n### parent 2: " + parent2);
//				population[parent2].printGrid();
//				System.out.println("\n### child");
//				population[j].simulate(false, false);
//				population[j].printGrid();
//				
//				try {
//					System.in.read();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
			
//			for(int i=0;i<population.length;i++) {
//				population[i].mutate();
//				population[i].simulate(false,false);
//			}
			
//			if(counter >= (int)(populationSize*parentRatio))
//				System.out.println(counter + "/" + populationSize*2/3 + " redone. Mutation rate too high");
		}
		
		population[0].simulate(true,false);
		synchronized(lock) {
			population[0].printGrid();
		}
	}

}
