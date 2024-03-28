import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// A class designed to load and process Automaton objects from file inputs
class AutomatonLoader {

    // Loads a list of Automaton objects from a specified file, where each line represents one automaton
    public List<Automaton> loadAutomataFromFile(String filename) throws IOException {
        List<Automaton> automata = new ArrayList<>();
        // Utilizes BufferedReader for efficient reading of characters, arrays, and lines
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            // Read file line by line until the end
            while ((line = reader.readLine()) != null) {
                // Convert each line to an Automaton object
                Automaton automaton = parseAutomaton(line);
                // Add the automaton to the list if it's valid and has exactly half end states
                if (automaton != null && automaton.hasHalfEndStates()) {
                    automata.add(automaton);
                }
            }
        }
        return automata; // Return the list of Automaton objects
    }

    // Parses a single line from the file to create an Automaton object
    private Automaton parseAutomaton(String definition) {
        int totalStates = definition.length() / 2; // Determines total states based on input string length
        Automaton automaton = new Automaton(totalStates, definition);

        for (int i = 0; i < totalStates; i++) {
            // Assigns transitions based on character values
            int fromState = i;
            int toState = charToStateNumber(definition.charAt(i));
            automaton.addTransition(fromState, toState);

            // Checks if a state is an end state based on character 't'
            if (definition.charAt(totalStates + i) == 't') {
                automaton.addEndState(i);
            }
        }

        return automaton; // Return the constructed Automaton
    }

    // Converts a character to a state number, supporting a specific range and symbols
    private int charToStateNumber(char c) {
        // Convert numeric characters directly to integers
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        // Convert special characters to designated state numbers
        return switch (c) {
            case ':' -> 10;
            case ';' -> 11;
            case '<' -> 12;
            case '=' -> 13;
            case '>' -> 14;
            case '?' -> 15;
            default -> throw new IllegalArgumentException("Unexpected character: " + c);
        };
    }

    // Processes automata from file to calculate and return X,Y combinations and their occurrences
    public Map<String, Integer> processAutomataFromFile(String filename) throws IOException {
        List<Automaton> automata = new ArrayList<>();
        Map<String, Integer> xyCombinations = new HashMap<>();
        
        // Read file line by line, creating Automaton objects
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Automaton automaton = parseAutomaton(line);
                if (automaton != null && automaton.hasHalfEndStates()) {
                    automata.add(automaton);
                    
                    // Construct a key for X,Y combinations and count occurrences
                    String key = automaton.getTotalStates() + "," + automaton.getEndStates().size();
                    xyCombinations.merge(key, 1, Integer::sum); // Update the count for each combination
                }
            }
        }
        
        return xyCombinations; // Return the map of X,Y combinations and their counts
    }
}
