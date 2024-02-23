import java.io.IOException;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String filename = "UnaryAutomataList\\unarydfa9.txt";

        AutomatonLoader loader = new AutomatonLoader();
        try {
            List<Automaton> automata = loader.loadAutomataFromFile(filename);
            System.out.println("Loaded " + automata.size() + " automata with half end states.");
            // automata.forEach(System.out::println);

            // Example: Selecting the first two automata to concatenate (ensure list size is
            // adequate)
            if (automata.size() >= 2) {
                Automaton concatenated = AutomatonOperations.concatenate(automata.get(0), automata.get(1));
                System.out.println("Result of concatenation:");
                System.out.println(concatenated);
            }

            // Example: Applying the star operation to the first automaton (ensure list has
            // at least one automaton)
            if (!automata.isEmpty()) {
                Automaton starred = AutomatonOperations.star(automata.get(0));
                System.out.println("Result of star operation:");
                System.out.println(starred);
            }

            // Example: Applying the square operation to the first automaton (ensure list
            // has at least one automaton)
            if (!automata.isEmpty()) {
                Automaton squared = AutomatonOperations.square(automata.get(0));
                System.out.println("Result of square operation:");
                System.out.println(squared);
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("There was an error reading the file.");
        }
    }
}
