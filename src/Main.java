import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String filename = "UnaryAutomata\\UnaryAutomataList\\unarydfa10.txt"; // File path to load automata from
        AutomatonLoader loader = new AutomatonLoader(); // Instance of AutomatonLoader to load automata
        Scanner scanner = new Scanner(System.in); // Scanner to read user input from console

        try {
            // Load automata from file
            List<Automaton> automata = loader.loadAutomataFromFile(filename);
            System.out.println("Loaded " + automata.size() + " automata.");
            // Present options for operations to perform on automata
            System.out.println("Choose an operation: \n1. Square\n2. Concatenation\n3. Star");
            int choice = scanner.nextInt();

            Map<String, Integer> xyCombinations = new HashMap<>(); // Map to store combinations of states and their occurrences
            int processedCount = 0; // Count of processed automata

            for (int i = 0; i < automata.size(); i++) {
                Automaton automaton = automata.get(i);
                // Skip automata that do not have half end states
                if (!automaton.hasHalfEndStates()) continue;

                processedCount++;
                Automaton operationResult = null; // Store result of operations here

                switch (choice) {
                    case 1: // Square operation
                        operationResult = AutomatonOperations.square(automaton);
                        break;
                    case 2: // Concatenation operation
                        if (i + 1 < automata.size() && automata.get(i + 1).hasHalfEndStates()) {
                            operationResult = AutomatonOperations.concatenate(automaton, automata.get(i + 1));
                            i++; // Skip next automaton since it's used for concatenation
                        }
                        break;
                    case 3: // Star operation
                        operationResult = AutomatonOperations.star(automaton);
                        break;
                }

                // If operation resulted in a non-null automaton, process it
                if (operationResult != null) {
                    Automaton dfaResult = AutomatonOperations.convertToDFA(operationResult);
                    Automaton minimizedResult = AutomatonOperations.minimizeDFA(dfaResult);
                    System.out.println(minimizedResult);

                    // Update the XY combinations map with the results of the operation
                    updateXYCombinations(minimizedResult, xyCombinations);
                }
            }
            
            System.out.println("Processed " + processedCount + " automata with exactly half end states.");
            // Print the combinations of total states and end states found across all processed automata
            printXYCombinations(xyCombinations);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading the file.");
        } finally {
            scanner.close(); // Close the scanner to prevent resource leak
        }
    }

    // Updates the map of XY combinations with the total and end states of a minimized automaton result
    private static void updateXYCombinations(Automaton minimizedResult, Map<String, Integer> xyCombinations) {
        String xyKey = minimizedResult.getTotalStates() + "," + minimizedResult.getEndStates().size();
        xyCombinations.merge(xyKey, 1, Integer::sum); // Merge updates the value by summing if the key already exists
    }

    // Prints out the XY combinations and their occurrences
    private static void printXYCombinations(Map<String, Integer> xyCombinations) {
        System.out.println("X,Y Combinations (Total States, End States) and their occurrences after minimization:");
        xyCombinations.entrySet().stream()
            .sorted((entry1, entry2) -> Integer.compare(
                Integer.parseInt(entry2.getKey().split(",")[0]), // Sort by total number of states in descending order
                Integer.parseInt(entry1.getKey().split(",")[0])
            ))
            .forEach(entry -> System.out.println("[" + entry.getKey() + "] -> " + entry.getValue()));
    }
}
