import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Starts an evolutionary algorithm with parameters given by the user.
 * 
 * @author Martin
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
			e.printStackTrace();
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
		System.out.println("status / s - gives an overview of the current simulation");
		System.out.println("best       - gives the currently best strategy");
		System.out.println("stop       - stops the simulation");
	}
	
	public static void main(String[] args) {
		// **********
		// USER INPUT
		// **********
		Scanner user_input = new Scanner(System.in);
		String error = "value not allowed!";
		System.out.print("enter population size\nP = ");
		int P = user_input.nextInt();
		while(P < 0) {
			System.out.print(error + "\nP = ");
			P = user_input.nextInt();
		}
		System.out.print("enter number of parents\nmu = ");
		int mu = user_input.nextInt();
		while(mu < 0 || mu >P) {
			System.out.print(error + "\nmu = ");
			mu = user_input.nextInt();
		}
		System.out.print("enter offspring size (enter 0 for default P-mu)\nlambda = ");
		int lambda = user_input.nextInt();
		while(lambda < 0 || lambda > (P-mu)) {
			System.out.print(error + "\nlambda = ");
			lambda = user_input.nextInt();
		}
		if(lambda == 0) lambda = P-mu;
		System.out.println("enter (relative) path to file containing cities:");
		ArrayList<City> cities = readFile(user_input.next());
		
		// ****************
		// START SIMULATION
		// ****************
		Population p = new Population(P,mu,lambda,cities);
		Thread simulation = new Thread(p);
		simulation.start();
		System.out.println("Simulation started!");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		printHelp();
		
		// ***********************
		// INPUT DURING SIMULATION
		// ***********************
		String command = user_input.next();
		while(!command.equals("stop")) {
			if(command.equals("help")) {
				printHelp();
			}
			else if(command.equals("s") || command.equals("status")) {
				p.printStatus();
			}
			else if(command.equals("best")) {
				p.printBest();
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
		p.kill();
		try {
			simulation.join();
			System.out.println("simulation stoped");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		user_input.close();

	}

}
