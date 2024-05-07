import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
                if (automaton != null) {
                    automata.add(automaton);
                    //System.out.println("Loaded automaton: " + automaton);
                } else {
                    System.out.println("Failed to parse automaton from line: " + line);
                }
            }
        }
        return automata;
    }

    private Automaton parseAutomaton(String definition) {
        int totalStates = definition.length() / 2;
        Automaton automaton = new Automaton(totalStates, definition);

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

    public List<String> loadAutomatonDefinitionsFromFile(String filename) throws IOException {
        List<String> definitions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Loaded automaton: ")) {
                    definitions.add(line.substring(18).trim());
                }
            }
        }
        return definitions;
    }

    public List<Automaton> parseAutomata(List<String> definitions) {
        List<Automaton> automata = new ArrayList<>();
        for (String def : definitions) {
            automata.add(parseAutomaton(def));
        }
        return automata;
    }

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

    public List<Automaton> filterAutomataByEndStates(List<Automaton> automata, int desiredEndStates) {
        List<Automaton> filteredAutomata = new ArrayList<>();
        for (Automaton automaton : automata) {
            if (automaton.getEndStates().size() == desiredEndStates) {
                filteredAutomata.add(automaton);
            }
        }
        return filteredAutomata;
    }
    

    public void saveAutomataToFile(List<Automaton> automata, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Automaton automaton : automata) {
                writer.write(automaton.toCompactString());
                writer.newLine();
            }
        }
    }
}