import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class EvoHighwayProtection implements Runnable{

	public static final int CONNECTED = 1;
	public static final int SCATTERED = 2;

	/** population size */
	public int populationSize = 500;
	/** ratio of how many individuals are keps after external selection */
	public double parentRatio = 0.2; 
	/** strategy version */
	public int stratVersion = CONNECTED;
	
	/** if true, the grid is printed to the console */
	public boolean print = false;
	/** if true, the development is printed to the console */
	public boolean printSteps = false;
	/** while true, the simulation continues */
	public boolean keepRunning = true;
	/** if true, the current state is saved */
	public boolean save = false;
	
	/** configuration file */
	Properties config;
	
	/** saves the history of the best strategy in each generation */
	private ArrayList<int[][]> history = new ArrayList<int[][]>();
	
	int maxGenerations = 0;
	
	
	static int bestTime = -1;
	static double bestFitness = -1;
	static int fitnessSum = 0;
	static int runCounter = 0;
	
	/**
	 * Constructor
	 */
	public EvoHighwayProtection() {
		
		config = new Properties();
		InputStream input = null;
		String s;

		try {
			input = new FileInputStream("parameters.config");

			// load a properties file
			config.load(input);

			// get the property value and print it out
			s = config.getProperty("populationSize");
			populationSize = Integer.parseInt(s);
			
			s = config.getProperty("parentRatio");
			parentRatio = Double.parseDouble(s);
			
			s = config.getProperty("stratVersion");
			if(s.equalsIgnoreCase("connected"))
				stratVersion = CONNECTED;
			else if(s.equalsIgnoreCase("scattered"))
				stratVersion = SCATTERED;

		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		Strategy.setParameters(config);
	}
	
	public static void main(String[] args) {
		
//		bench(10, 200);
		
		
		
		EvoHighwayProtection e = new  EvoHighwayProtection();
		
		Thread t = new Thread(e);
		t.start();
		
		Scanner input = new Scanner(System.in);
		String s;
		
		while(true) {
			s = input.nextLine();
			
			if(s.equalsIgnoreCase("bench")) {
				e.keepRunning = false;
				PrintWriter benchLog = null;
				try {
					benchLog = new PrintWriter("bench.txt","UTF-8");
				} catch (FileNotFoundException | UnsupportedEncodingException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				try {
					t.join();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				System.out.println("Start Bench");
				double budget = 1.5;
				while(budget >= 0.99) {
					bench(30, 500, budget);
					if(Strategy.mode == Strategy.Mode.CatchFire)
						benchLog.println(budget + " " + bestFitness + " " + bestTime);
					else
						benchLog.println(budget + " " + bestFitness + " " + bestTime);
					benchLog.flush();
					System.out.println("Bench finished");
					System.out.println("c=" + Strategy.budget);
					System.out.println("Time: " + bestTime);
					System.out.println("Burning: " + bestFitness);
					Toolkit.getDefaultToolkit().beep();
					budget -= 0.05;
					//input.nextLine();
				}
				break;
			}
			if(s.equalsIgnoreCase("reset")) {
				e.keepRunning = false;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e = new EvoHighwayProtection();
				t = new Thread(e);
				t.start();
			}
			if(s.equalsIgnoreCase("show"))
				e.print = true;
			if(s.equalsIgnoreCase("steps"))
				e.printSteps = true;
			if(s.equalsIgnoreCase("stop")) {
				e.save = true;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.keepRunning = false;
				break;
			}
			if(s.equalsIgnoreCase("save"))
				e.save = true;
		}
		input.close();
		
		try {
			t.join();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		System.out.println("simulations: " + runCounter);
		System.out.println("mean fittness: " + fitnessSum/runCounter);
	}
	
	public static void bench(int runs, int generations, double budget) {
		bestTime = -1;
		bestFitness = -1;
		for(int i=0;i<runs;i++) {
			EvoHighwayProtection e = new  EvoHighwayProtection();
			e.setMaxGenerations(generations);
			Strategy.budget = budget;
			if(Strategy.mode == Strategy.Mode.ProtectHighway)
				Strategy.initialAccount = budget;
			
			Thread t = new Thread(e);
			t.start();
			
			try {
				t.join();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public void setMaxGenerations(int genetations) {
		maxGenerations = genetations;
	}
	
	/**
	 * Calculates indices according to the roulette select method.
	 * @param n
	 * @return index i with probability (n-i)/z
	 */
	public int rouletteSelect(int n) {
		// calculate the total weight
		double z = n*(n+1)/2;

		// get a random value
		double value = Strategy.rand.nextDouble() * z;	
		// locate the random value based on the weights
		for(int i=0; i<=n; i++) {		
			value -= n-i;		
			if(value <= 0) return i;
		}
		// when rounding errors occur, we return the last item's index 
		return n;
	}

	@Override
	public void run() {
		
		// delete old files
		File dir = new File("historyPlot");
		if(dir.exists()) {
			String[] toDelete = dir.list();
			for(String s:toDelete) {
				File curr = new File(dir.getPath(),s);
				curr.delete();
			}
		}
		else
			dir.mkdir();
		dir = new File("stepsPlot");
		if(dir.exists()) {
			String[] toDelete = dir.list();
			for(String s:toDelete) {
				File curr = new File(dir.getPath(),s);
				curr.delete();
			}
		}
		else
			dir.mkdir();
		dir = new File("steps");
		if(dir.exists()) {
			String[] toDelete = dir.list();
			for(String s:toDelete) {
				File curr = new File(dir.getPath(),s);
				curr.delete();
			}
		}
		else
			dir.mkdir();
		
		// create new population
		Strategy[] population = new Strategy[populationSize];
		for(int i=0;i<population.length;i++) {
			switch (stratVersion) {
			case 1:
				population[i] = new ConnectedStrategy();
				break;
				
			case 2:
				population[i] = new ScatteredStrategy();
				break;
				
			default:
				break;
			}
			population[i].simulate(false,false,false);
		}
		
		// create log file to keep track of fitness
		PrintWriter log = null;
		try {
			log = new PrintWriter("fitness.log","UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// start simulation
		long lastTime = 0;
		Strategy bestSoFar = null;
		for(int generation=0;keepRunning && (maxGenerations==0 || generation<maxGenerations);generation++) {
			// sort population by fitness
			Arrays.sort(population);
			
			if(bestSoFar == null || bestSoFar.compareTo(population[0])>0) {
				// best strategy has improved
				bestSoFar = population[0];
				
				// add line to log
				log.println(generation + " " + bestSoFar.fitness());
				log.flush();
				
				// save grid
				history.add(bestSoFar.cloneCurrentGrid());
				
				// print to console (max once every second)
				if(System.currentTimeMillis() - lastTime > 1000) {
					bestSoFar.printGrid();
					System.out.println("Generation: " + generation);
					System.out.println();
					lastTime = System.currentTimeMillis();
				}
			}
			
			// print currently best strategy if wanted
			if(print) {
				population[0].printGrid();
				System.out.println("Generation: " + generation);
				System.out.println();
				print = false;
			}
			
			// print development of strategy to console if wanted
			if(printSteps) {
				population[0].simulate(true,true,false);
				population[0].printGrid();
				printSteps = false;
			}
			
			// saves current state if wanted
			if(save) {
				System.out.print("Saving");
				for(int sec=0;sec<3;sec++) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.print(".");
				}
				population[0].simulate(true, false, true);
				System.out.println(" done!");
				save = false;
			}
			
			
			int parent1, parent2;
			for(int j=(int)(populationSize*parentRatio);j<population.length;j++) {
				// PARENT SELECTION
				parent1 = rouletteSelect((int)(populationSize*parentRatio));
				do{
					parent2 = rouletteSelect((int)(populationSize*parentRatio));
				} while (parent1 == parent2);
				
				// INHERITANCE
				switch (stratVersion) {
				case 1:
					population[j] = new ConnectedStrategy((ConnectedStrategy)population[parent1],(ConnectedStrategy)population[parent2]);
					break;
					
				case 2:
					population[j] = new ScatteredStrategy((ScatteredStrategy)population[parent1],(ScatteredStrategy)population[parent2]);
					break;

				default:
					break;
				}
				
				// MUTATION
				population[j].mutate();
				
				// FITNESS EVALUATION
				population[j].simulate(false,false,false);
			}
		}
		
		// close log file
		log.close();
		
		// saves history
		dir = new File("history");
		if(dir.exists()) {
			String[] toDelete = dir.list();
			for(String s:toDelete) {
				File curr = new File(dir.getPath(),s);
				curr.delete();
			}
		}
		else
			dir.mkdir();
		int time = 0;
		int minX=history.get(0).length-1, minY=history.get(0)[0].length-1, maxX=0, maxY=0;
		for(int[][] grid:history) {
		    for(int x=0;x<grid.length;x++)
		    {
		        for(int y=0;y<grid[0].length;y++)
		        {
		            if(grid[x][y] != Strategy.FREE)
		            {
		                if(x<minX) minX = x;
		                if(y<minY) minY = y;
		                if(x>maxX) maxX = x;
		                if(y>maxY) maxY = y;
		            }
		        }
		    }
		}
		for(int[][] grid:history) {
			Strategy.save(grid,"history/history_"+(time++),minX,maxX,minY,maxY);
		}
		
		runCounter++;
		fitnessSum += population[0].fitness();
		if(Strategy.mode == Strategy.Mode.CatchFire) {
			if(bestTime<0 || population[0].timeToEncloseFire < bestTime)
				bestTime = population[0].timeToEncloseFire;
			
			if(bestFitness<0 || population[0].fitness() < bestFitness)
				bestFitness = population[0].fitness();
		}
		else {
			if(bestTime<0 || population[0].timeToEncloseFire > bestTime)
				bestTime = population[0].timeToReachHighway;
			
			if(bestFitness<0 || population[0].fitness() < bestFitness)
				bestFitness = population[0].fitness();
		}
		

		
		
		System.out.println("Used seed: " + Strategy.seed);
	}
}
