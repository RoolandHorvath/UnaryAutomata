import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Reading automata from a file
class AutomatonLoader {

    // Method to load automata from a file where each line represents one automaton
    // The stateSequence parameter is used to filter automata based on their end state designations
    public List<Automaton> loadAutomataFromFile(String filename, String stateSequence) throws IOException {
        List<Automaton> automata = new ArrayList<>();
        // Try-with-resources to ensure the reader is closed properly after use
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            // Read each line of the file until the end is reached
            while ((line = reader.readLine()) != null) {
                // Split the line at its midpoint; the second half specifies end state designations ('t' or 'f')
                String endStatesString = line.substring(line.length() / 2);
                // Check if the line's second half matches the specified state sequence
                if (endStatesString.equals(stateSequence)) {
                    // Parse the line into an Automaton object if it matches the end state criteria
                    Automaton automaton = parseAutomaton(line);
                    // Add the parsed Automaton to the list
                    automata.add(automaton);
                }
            }
        }
        // Return the list of Automaton objects that match the end state criteria
        return automata;
    }

    // Method to parse a single line from the file into an Automaton object
    private Automaton parseAutomaton(String definition) {
        // The total number of states is half the length of the line (each state has a transition and an end state flag)
        int totalStates = definition.length() / 2;
        // Create a new Automaton object with the calculated number of states and the full line as its definition
        Automaton automaton = new Automaton(totalStates, definition);

        // For each state in the automaton
        for (int i = 0; i < totalStates; i++) {
            int fromState = i; // The current state
            // Convert the character representing the transition target state into an integer
            int toState = charToStateNumber(definition.charAt(i));
            // Add the transition from the current state to the target state in the Automaton
            automaton.addTransition(fromState, toState);

            // If the character indicating whether the current state is an end state is 't', mark it as an end state
            if (definition.charAt(totalStates + i) == 't') {
                automaton.addEndState(i);
            }
        }

        // Return the fully constructed Automaton object
        return automaton;
    }

    // Method to convert a character from the transition definition into a state number
    // This handles digits '0'-'9' and extends to other characters for states beyond 9
    private int charToStateNumber(char c) {
        // If the character is a digit, convert it directly to the corresponding integer
        if (c >= '0' && c <= '9') {
            return c - '0'; // Direct conversion for digits
        }
        // Handle special characters for states beyond '9' using a switch expression
        return switch (c) {
            case ':' -> 10;
            case ';' -> 11;
            case '<' -> 12;
            case '=' -> 13;
            case '>' -> 14;
            case '?' -> 15;
            // Throw an exception for unexpected characters
            default -> throw new IllegalArgumentException("Unexpected character: " + c);
        };
    }
}