import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // The path to the file containing automaton definitions
        String filename = "UnaryAutomata\\UnaryAutomataList\\unarydfa6.txt";
        // A specific state sequence to filter the automata being loaded
        String stateSequence = "tttfff"; 

        // Creating an instance of AutomatonLoader to load automata from the file
        AutomatonLoader loader = new AutomatonLoader();
        try {
            // Load automata from the file that match the specified state sequence
            List<Automaton> automata = loader.loadAutomataFromFile(filename, stateSequence);
            // Print the number of automata loaded
            System.out.println("Loaded " + automata.size() + " automata matching the state sequence: " + stateSequence);

            // If at least two automata were loaded, perform concatenation, DFA conversion, and minimization
            if (automata.size() >= 2) {
                // Concatenate the first two automata
                Automaton concatenated = AutomatonOperations.concatenate(automata.get(0), automata.get(1));
                System.out.println("Result of concatenation before DFA conversion and minimization:");
                System.out.println(concatenated);

                // Convert the concatenated automaton to a DFA
                Automaton concatenatedDFA = AutomatonOperations.convertToDFA(concatenated);
                System.out.println("Result of concatenation after DFA conversion:");
                System.out.println(concatenatedDFA); // Print the DFA conversion result

                // Minimize the DFA
                Automaton minimizedConcat = AutomatonOperations.minimizeDFA(concatenatedDFA);
                System.out.println("Result of concatenation after minimization:");
                System.out.println(minimizedConcat); // Print the minimized DFA
            }

            // Perform the star operation if any automata were loaded
            if (!automata.isEmpty()) {
                // Apply the star operation to the first automaton
                Automaton starred = AutomatonOperations.star(automata.get(0));
                System.out.println("Result of star operation before DFA conversion and minimization:");
                System.out.println(starred);

                // Convert the result of the star operation to a DFA
                Automaton starredDFA = AutomatonOperations.convertToDFA(starred);
                System.out.println("Result of star operation after DFA conversion:");
                System.out.println(starredDFA); // Print the DFA conversion result

                // Minimize the DFA resulting from the star operation
                Automaton minimizedStar = AutomatonOperations.minimizeDFA(starredDFA);
                System.out.println("Result of star operation after minimization:");
                System.out.println(minimizedStar); // Print the minimized DFA
            }

            // Perform the square operation if any automata were loaded
            if (!automata.isEmpty()) {
                // Apply the square operation to the first automaton
                Automaton squared = AutomatonOperations.square(automata.get(0));
                System.out.println("Result of square operation before DFA conversion and minimization:");
                System.out.println(squared);

                // Convert the result of the square operation to a DFA
                Automaton squaredDFA = AutomatonOperations.convertToDFA(squared);
                System.out.println("Result of square operation after DFA conversion:");
                System.out.println(squaredDFA); // Print the DFA conversion result

                // Minimize the DFA resulting from the square operation
                Automaton minimizedSquared = AutomatonOperations.minimizeDFA(squaredDFA);
                System.out.println("Result of square operation after minimization:");
                System.out.println(minimizedSquared); // Print the minimized DFA
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an error reading the file.");
        }
    }
}
