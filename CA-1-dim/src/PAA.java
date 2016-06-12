import java.util.Scanner;
import java.util.Random;

/**
   Authors:
   Martin Kretschmer
   Roland Meneghetti

   How to run?
   Compile this file with 'javac PAA.java' and then run the main
   method of the resulting .class-file with 'java PAA' (assuming you
   are in the folder where this file is)


   This program is intended to simulate a one dimensional cellular
   automaton.  The tape has a fixed width of 84 cells. The
   neighborhood radius can be 1 or 2. The tape can be initially blank
   except the 42nd cell or can be randomly filled. It is also possible
   to enter the rule at runtime.
   Cells set to zero / dead are printed as '-', cells set to one /
   alive are printed as 'X'.
 */
public class PAA { // programming assignment A

    // An enum to represent the user's choice of a seeded or a random tape.
    private enum Pattern {
        SEED, RANDOM;
    }

    // the tape has a fixed width of 84 cells
    private static final int WIDTH = 84;
    // 2 tapes, the first one represents the current generation and
    // the second one the previous generation.  when calculating the
    // new generation the old generation tape is recycled
    private static int[] tape1 = new int[WIDTH];
    private static int[] tape2 = new int[WIDTH];
    // the selected neighborhood radius which can be 1 or 2. Initially 1.
    private static int radius = 1;
    // the selected rule, initially 86. We've taken a boxed long here
    // so that we can represent an invalid rule (e.g. the number is to
    // great for the radius) as null.
    private static Long ruleNumber = 86L;
    // the selected pattern. default is SEED (only 42nd cell set to 1)    
    private static Pattern pattern = Pattern.SEED;

    // a main-method containing a part of the game-logic and some glue
    // code for the UI.
    public static void main(String[] args) {
        // let the user set up his game
        setupSettings();
        // set the tape according to the selected pattern
        setupTape();
        // game loop
        while (true) {
            printTape(tape1);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
            }
            applyRule(radius, ruleNumber, tape1, tape2);
            swap();
        }
    }

    // sets up the tape according to the selected pattern
    private static void setupTape() {
        // set all cells to 0, then set the 42nd cell to 1.
        if (pattern == Pattern.SEED) {
            for (int i = 2; i < 82; ++i) {
                tape1[i] = 0;
            }
            tape1[42] = 1;
        }
        // set the cells to 0 and 1 randomly
        if (pattern == Pattern.RANDOM) {
            Random random = new Random(System.currentTimeMillis());
            for (int i = 2; i < 82; ++i) {
                tape1[i] = random.nextBoolean() ? 1 : 0;
            }
        }
    }

    // this method contains the UI. It's main part is a while loop
    // where the user can break out if he/she just presses enter
    private static void setupSettings() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("==");
            System.out.println("Usage: enter");
            System.out.println("  * 'b' for a big radius (radius = 2)");
            System.out.println("  * 's' for a small radius (radius = 1)");
            System.out.println("  * 'R' for an initial random tape");
            System.out.println("  * 'S' for tape which is empty except for the 42th position");
            System.out.println("  * a (long) integer to set a rule");
            System.out.println("Pattern: " + pattern);
            System.out.println("Rule: " + ruleNumber);
            System.out.println("Radius: " + radius);
            String line = scanner.nextLine();
            // end the program if the user presses q
            if (line.matches("^q$"))
                System.exit(0);
            // end the settings-while-loop when the user just presses enter (basically)
            if (line.matches("^$")) {
                if (ruleNumber == null) {
                    // some error handling if the rule is invalid
                    System.out.println("Rule invalid. Cannot start");
                } else {
                    // start the program :-)
                    break;
                }
            }
            // set the rule when the user entered an integer
            if (line.matches("^0$") || line.matches("^[1-9][0-9]*$")) {
                long tmp = Long.valueOf(line);
                // error handling for big-radius
                if (radius == 2) {
                    if (tmp > 4294967295L) { // 2^32 - 1
                        ruleNumber = null;
                        System.out.println("Fyi: rule too big for selected radius. You must select a valid rule now");
                        continue;
                    }
                }
                // error handling for small-radius
                if (radius == 1) {
                    if (tmp > 255) { // 2^8 - 1
                        ruleNumber = null;
                        System.out.println("Fyi: rule too big for selected radius. You must select a valid rule now");
                        continue;
                    }
                }
                // no errors found in the input, we can accept the input as rule.
                ruleNumber = tmp;
            }
            // set the radius to 1 if the user entered 's' (small-radius)
            if (line.matches("^s$")) {
                radius = 1;
                if (ruleNumber > 255) {
                    ruleNumber = null;
                    System.out.println("Fyi: rule too big for selected radius. You must select a valid rule now");
                }
            }
            // set the radius to 2 if the user entered 'b' (big-radius)
            if (line.matches("^b$"))
                radius = 2;
            // set the pattern to random if the user entered 'R'
            if (line.matches("^R$"))
                pattern = Pattern.RANDOM;
            // set the pattern to seed if the user entered 'S'
            if (line.matches("^S$"))
                pattern = Pattern.SEED;
        }
        System.out.println(""); // print a new line, so the user-input starts at the start of the line
    }

    // print the given tape
    private static void printTape(int[] tape) {
        for (int cell : tape)
            System.out.print(cell == 0 ? "-" : "X");
        System.out.println(""); // print a newline
    }

    // applies the given rule with to currentGen and saves the result to nextGen
    private static void applyRule(int radius, long ruleNumber, int[] currentGen, int[] nextGen) {
        for (int i = 2; i < 82; ++i)
            nextGen[i] = applyRule(radius, ruleNumber, currentGen, i);
    }

    // applies the rule for a single cell
    private static int applyRule(int radius, long ruleNumber, int[] currentGen, int posOfNextGenCell) {
        long tmp = 0;
        for (int pos = posOfNextGenCell - radius; pos <= posOfNextGenCell + radius; ++pos)
            tmp = (tmp << 1) | currentGen[pos];
        int result = (((1 << tmp) & ruleNumber) == 0) ? 0 : 1;
        return result;
    }

    // swaps the generations / boards / tapes
    private static void swap() {
        int[] tmp = tape1;
        tape1 = tape2;
        tape2 = tmp;
    }
    
}
