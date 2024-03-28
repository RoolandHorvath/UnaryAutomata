import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    static boolean specialCaseFound = false;
    static int counter = 0;
    public static void main(String[] args) {
        String filename = "UnaryAutomata\\UnaryAutomataList\\unarydfa14.txt";
        AutomatonLoader loader = new AutomatonLoader();
        Scanner scanner = new Scanner(System.in);

        try {
            List<Automaton> automata = loader.loadAutomataFromFile(filename);
            System.out.println("Loaded " + automata.size() + " automata.");
            System.out.println("Choose an operation: \n1. Square\n2. Concatenation\n3. Star\n4. Extended Concatenation with Automatic Batch Processing");
            int choice = scanner.nextInt();

            Map<String, Integer> xyCombinations = new HashMap<>();
            int processedCount = 0;
            final int BATCH_SIZE = 1000;
            for (int i = 0; i < automata.size(); i++) {
                Automaton automaton = automata.get(i);
                if (!automaton.hasHalfEndStates()) continue;

                processedCount++;
                Automaton operationResult = null;

                switch (choice) {
                    case 1:
                        operationResult = AutomatonOperations.square(automaton);
                        break;
                    case 2:
                        if (i + 1 < automata.size() && automata.get(i + 1).hasHalfEndStates()) {
                            operationResult = AutomatonOperations.concatenate(automaton, automata.get(i + 1));
                            i++; // Skip next automaton since it's used for concatenation
                        }
                        break;
                    case 3:
                        operationResult = AutomatonOperations.star(automaton);
                        break;
                    case 4:
                        // Process concatenation with every other automaton
                        for (int j = 0; j < automata.size(); j++) {
                            if (i != j && automata.get(j).hasHalfEndStates()) {
                                Automaton concatenatedResult = AutomatonOperations.concatenate(automaton, automata.get(j));
                                Automaton dfaResult = AutomatonOperations.convertToDFA(concatenatedResult);
                                Automaton minimizedResult = AutomatonOperations.minimizeDFA(dfaResult);
                                checkAndPrintResults(automaton, automata.get(j), minimizedResult, xyCombinations);
                                
                                if ((j + 1) % BATCH_SIZE == 0) {
                                    batchProcessedMessage();
                                }
                            }
                        }
                        break;
                }

                if (operationResult != null) {
                    Automaton dfaResult = AutomatonOperations.convertToDFA(operationResult);
                    Automaton minimizedResult = AutomatonOperations.minimizeDFA(dfaResult);
                    System.out.println(minimizedResult);

                    updateXYCombinations(minimizedResult, xyCombinations);
                }
            }
            if (!specialCaseFound && choice == 4) { 
                System.out.println("No special case of concatenation was found.");
            }
            System.out.println("Processed " + processedCount + " automata with exactly half end states.");
            printXYCombinations(xyCombinations);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading the file.");
        } finally {
            scanner.close();
        }
    }

    private static void checkAndPrintResults(Automaton automaton1, Automaton automaton2, Automaton minimizedResult, Map<String, Integer> xyCombinations) {
        int m = automaton1.getTotalStates();
        int k = automaton1.getEndStates().size();
        int n = automaton2.getTotalStates();
        int l = automaton2.getEndStates().size();

        // Conditions for special notification
        if (minimizedResult.getTotalStates() > (m * n) / 2 && minimizedResult.getEndStates().size() > (m * n) / 4) {
            specialCaseFound = true;
            System.out.println("Found special case automaton with conditions met for concatenation of:");
            System.out.println("First Automaton: " + automaton1.getCurrentAutomata() + " (States: " + m + ", End States: " + k + ")");
            System.out.println("Second Automaton: " + automaton2.getCurrentAutomata() + " (States: " + n + ", End States: " + l + ")");
            System.out.println("Resulting Automaton after Minimization: " + minimizedResult.getCurrentAutomata());
            System.out.println("Total States: " + minimizedResult.getTotalStates() + ", End States: " + minimizedResult.getEndStates().size());
            System.out.println("Resulting Automaton Details: ");
            System.out.println(minimizedResult);
        }

        // Print details or store information as needed
        updateXYCombinations(minimizedResult, xyCombinations);
    }

    private static void updateXYCombinations(Automaton minimizedResult, Map<String, Integer> xyCombinations) {
        String xyKey = minimizedResult.getTotalStates() + "," + minimizedResult.getEndStates().size();
        xyCombinations.merge(xyKey, 1, Integer::sum);
    }

    private static void printXYCombinations(Map<String, Integer> xyCombinations) {
        System.out.println("X,Y Combinations (Total States, End States) and their occurrences after minimization:");
        // Convert the map into a stream, sort it by the total number of states in descending order, and then collect the results
        xyCombinations.entrySet().stream()
            .sorted((entry1, entry2) -> Integer.compare(
                Integer.parseInt(entry2.getKey().split(",")[0]), // Parse X from the second entry
                Integer.parseInt(entry1.getKey().split(",")[0])  // Parse X from the first entry
            ))
            .forEach(entry -> System.out.println("[" + entry.getKey() + "] -> " + entry.getValue()));
    }
    private static void batchProcessedMessage() {
        String message = "Processed a batch of 1000. Continuing";
        for (int i = 0; i < counter % 3 + 1; i++) {
            message += ".";
        }
        System.out.println(message);
        counter++;
    }
    
}