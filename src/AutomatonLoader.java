import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class AutomatonLoader {

    // Method to load automata from a file where each line represents one automaton
    public List<Automaton> loadAutomataFromFile(String filename) throws IOException {
        List<Automaton> automata = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse the line into an Automaton object
                Automaton automaton = parseAutomaton(line);
                // Check if the automaton has exactly half end states before adding it to the list
                if (automaton != null && automaton.hasHalfEndStates()) {
                    automata.add(automaton);
                }
            }
        }
        return automata;
    }

    private Automaton parseAutomaton(String definition) {
        int totalStates = definition.length() / 2;
        Automaton automaton = new Automaton(totalStates, definition);

        for (int i = 0; i < totalStates; i++) {
            int fromState = i;
            int toState = charToStateNumber(definition.charAt(i));
            automaton.addTransition(fromState, toState);

            // Mark the state as an end state if the corresponding character is 't'
            if (definition.charAt(totalStates + i) == 't') {
                automaton.addEndState(i);
            }
        }

        return automaton;
    }

    private int charToStateNumber(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
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

    public Map<String, Integer> processAutomataFromFile(String filename) throws IOException {
    List<Automaton> automata = new ArrayList<>();
    Map<String, Integer> xyCombinations = new HashMap<>();
    
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
        String line;
        while ((line = reader.readLine()) != null) {
            Automaton automaton = parseAutomaton(line);
            if (automaton != null && automaton.hasHalfEndStates()) {
                automata.add(automaton);
                
                // Update X,Y combination count
                String key = automaton.getTotalStates() + "," + automaton.getEndStates().size();
                xyCombinations.merge(key, 1, Integer::sum);
            }
        }
    }
    
    // Process each automaton as needed here
    // For example: perform operations, convert to DFA, minimize, etc.
    
    return xyCombinations;
}
}
