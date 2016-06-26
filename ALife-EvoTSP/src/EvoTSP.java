import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Starts an evolutionary algorithm with parameters given by the user.
 * 
 * @author Martin Kretschmer
 * @author Rolang Meneghetti
 * @see Population
 *
 */
public class EvoTSP {

	/**
	 * Reads the list of cities from a text file.
	 *  
	 * @param filePath path to the file
	 * @return list containing all cities
	 */
	private static ArrayList<City> readFile(String filePath) {
		
		if(filePath.equals("default"))
			filePath = "Positions_PA-E.txt";
		
		BufferedReader br = null;
		String line;
		ArrayList<City> cities = new ArrayList<City>();
		Scanner s;
		int id, x, y;
		try {
			br = new BufferedReader(new FileReader(filePath));
			
			while((line = br.readLine()) != null) {
				
				if(line.charAt(0) != '#') {
					s = new Scanner(line);
					id = s.nextInt();
					x = s.nextInt();
					y = s.nextInt();
					
					cities.add(new City(id,x,y));
				}
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
//			e.printStackTrace();
			return null;
		} finally {
			try {
				if(br != null) br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		return cities;
	}
	
	/**
	 * prints a list of possible commands during the simulation
	 */
	private static void printHelp() {
		System.out.println("--- list of commands ---");
		System.out.println("help       - prints the list of commands");
		System.out.println("status / s - gives an overview of the current simulations");
		System.out.println("best       - gives the currently best strategy");
		System.out.println("gnuplot    - creates for each thread a file with gnuplot commands");
		System.out.println("             to plot the development of the population");
		System.out.println("restart    - reinitializes the program");
		System.out.println("stop       - stops the simulation");
	}
	
	public static void main(String[] args) {
		
		String command = "restart";
		Scanner user_input = new Scanner(System.in);
		while(!command.equals("stop")) {
// **********
// USER INPUT
// **********
			String error = "value not allowed!";
			
			System.out.print("> enter population size: P = ");
			int P = user_input.nextInt();
			while(P < 0) {
				System.out.print(error + "\nP = ");
				P = user_input.nextInt();
			}
			
			System.out.print("> enter number of parents: mu = ");
			int mu = user_input.nextInt();
			while(mu < 0 || mu >P) {
				System.out.print(error + "\nmu = ");
				mu = user_input.nextInt();
			}
			
			System.out.print("> enter offspring size (enter 0 for default P-mu): lambda = ");
			int lambda = user_input.nextInt();
			while(lambda < 0 || lambda > (P-mu)) {
				System.out.print(error + "\nlambda = ");
				lambda = user_input.nextInt();
			}
			if(lambda == 0) {
				lambda = P-mu;
				System.out.println("> lambda set to " + lambda);
			}
			
			System.out.print("> Set number of threads to: ");
			int threadsNumber = user_input.nextInt();
			if(threadsNumber<=0) {
				threadsNumber = 1;
				System.out.println("> Threads can not be 0. Set to 1 instead!");
			}
			
			System.out.print("> enter (relative) path to file containing cities: ");
			ArrayList<City> cities = readFile(user_input.next());

			
			if(cities != null)
			{
				
// ****************
// START SIMULATION
// ****************
				ArrayList<Population> populations = new ArrayList<Population>(threadsNumber);
				ArrayList<Thread> threads = new ArrayList<Thread>(threadsNumber);
				for(int i=0;i<threadsNumber;i++) {
					Population p = new Population(P,mu,lambda,cities,i);
					populations.add(p);
					Thread t = new Thread(p);
					threads.add(t);
					t.setPriority(Thread.NORM_PRIORITY - 1);
				}
				printHelp();
				System.out.println("Symulation starts in");
				for(int i=0;i<3;i++) {
					System.out.print((3-i));
					for(int j=0;j<3;j++) {
						System.out.print(".");
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					}
					System.out.println("");
				}
				for(Thread t : threads) {
					t.start();
				}
				
// ***********************
// INPUT DURING SIMULATION
// ***********************
				command = user_input.next();
				while(!command.equals("stop")) {
					if(command.equals("help")) {
						printHelp();
					}
					else if(command.equals("s") || command.equals("status")) {
						System.out.println("/--- STATUS ---\\");
						for(Population p : populations)
							p.printStatus();
						System.out.println("\\--------------/");
					}
					else if(command.equals("best")) {
						System.out.println("/--- BEST ---\\");
						for(Population p : populations)
							p.printBest();
						System.out.println("\\------------/");
					}
					else if(command.equals("restart")) {
						break;
					}
					else if(command.equals("gnuplot")) {
						for(Population p : populations)
							p.createGnuplotFile();
					}
					else {
						System.out.println("command unknown");
						printHelp();
					}
					command = user_input.next();
				}
				
// ***************
// STOP SIMULATION
// ***************
				for(Population p : populations)
					p.kill();
				try {
					for(Thread t : threads)
						t.join();
					System.out.println("simulation stoped");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				double bestOfAll = 0;
				Population best = null;
				for(Population p : populations) {
					if(p.getBest() > bestOfAll) {
						bestOfAll = p.getBest();
						best = p;
					}
				}
				System.out.println("Best tour found after " + best.getGenerations() + " generations:");
				System.out.println("Saved in 'bestTour.txt'\n");
				best.saveBest();
				
			}
		}
		user_input.close();
	}
}
