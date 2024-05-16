import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class AutomatonLoader {

    // Load automata from a file, each line representing an automaton
    public List<Automaton> loadAutomataFromFile(String filename) throws IOException {
        List<Automaton> automata = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            // Read each line from the file and parse it into an automaton
            while ((line = reader.readLine()) != null) {
                Automaton automaton = parseAutomaton(line);
                if (automaton != null) {
                    automata.add(automaton);
                    // System.out.println("Loaded automaton: " + automaton);
                } else {
                    System.out.println("Failed to parse automaton from line: " + line);
                }
            }
        }
        return automata;
    }

    // Parse a string definition into an Automaton object
    private Automaton parseAutomaton(String definition) {
        int totalStates = definition.length() / 2;
        Automaton automaton = new Automaton(totalStates, definition);

        // Iterate through the state transitions and end states
        for (int i = 0; i < totalStates; i++) {
            int toState = charToStateNumber(definition.charAt(i));
            boolean isEndState = definition.charAt(totalStates + i) == 't';

            automaton.addTransition(i, toState);
            if (isEndState) {
                automaton.addEndState(i);
            }
        }

        return automaton;
    }

    // Load automaton definitions from a file where each relevant line starts with "Loaded automaton: "
    public List<String> loadAutomatonDefinitionsFromFile(String filename) throws IOException {
        List<String> definitions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            // Read each line and extract the automaton definition part
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Loaded automaton: ")) {
                    definitions.add(line.substring(18).trim());
                }
            }
        }
        return definitions;
    }

    // Parse a list of string definitions into a list of Automaton objects
    public List<Automaton> parseAutomata(List<String> definitions) {
        List<Automaton> automata = new ArrayList<>();
        for (String def : definitions) {
            automata.add(parseAutomaton(def));
        }
        return automata;
    }

    // Convert a character to a state number based on predefined rules
    private int charToStateNumber(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        switch (c) {
            case ':': return 10;
            case ';': return 11;
            case '<': return 12;
            case '=': return 13;
            case '>': return 14;
            case '?': return 15;
            default:
                throw new IllegalArgumentException("Unexpected character: " + c);
        }
    }

    // Filter automata based on the desired number of end states
    public List<Automaton> filterAutomataByEndStates(List<Automaton> automata, int desiredEndStates) {
        List<Automaton> filteredAutomata = new ArrayList<>();
        for (Automaton automaton : automata) {
            if (automaton.getEndStates().size() == desiredEndStates) {
                filteredAutomata.add(automaton);
            }
        }
        return filteredAutomata;
    }

    // Save a list of automata to a file
    public void saveAutomataToFile(List<Automaton> automata, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Automaton automaton : automata) {
                writer.write(automaton.toCompactString());
                writer.newLine();
            }
        }
    }
}