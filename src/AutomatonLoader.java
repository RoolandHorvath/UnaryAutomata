import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class AutomatonLoader {

    public List<Automaton> loadAutomataFromFile(String filename) throws IOException {
        List<Automaton> automata = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Automaton automaton = parseAutomaton(line);
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
            // Adjust the character decoding here
            int toState = charToStateNumber(definition.charAt(i));
            automaton.addTransition(fromState, toState);

            if (definition.charAt(totalStates + i) == 't') {
                automaton.addEndState(i);
            }
        }

        return automaton;
    }

    // Convert encoded characters to state numbers
    private int charToStateNumber(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0'; // Direct conversion for digits
        }
        // Handle special characters for states beyond '9'
        return switch (c) {
            case ':' -> 10;
            case ';' -> 11;
            case '<' -> 12;
            case '=' -> 13;
            case '>' -> 14;
            case '?' -> 15;
            // Add more cases as needed
            default -> throw new IllegalArgumentException("Unexpected character: " + c);
        };
    }
}
