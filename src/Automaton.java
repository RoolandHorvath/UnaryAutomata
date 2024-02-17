import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Automaton {
    private HashMap<Integer, Set<Integer>> transitions;
    private HashSet<Integer> endStates;
    private int totalStates;
    private String currentAutomata;
    private HashMap<Integer, Set<Integer>> epsilonTransitions;

    public Automaton(int totalStates, String currentAutomata) {
        this.totalStates = totalStates;
        this.transitions = new HashMap<>();
        this.endStates = new HashSet<>();
        this.currentAutomata = currentAutomata;
        this.epsilonTransitions = new HashMap<>();
    }

    public void addTransition(int fromState, int toState) {
        transitions.computeIfAbsent(fromState, k -> new HashSet<>()).add(toState);
        // System.out.println("Adding transition: " + fromState + " -> " + toState); // Debug print
    }

    public void addEndState(int state) {
        endStates.add(state);
    }

    public boolean hasHalfEndStates() {
        return endStates.size() * 2 >= totalStates;
    }

    public int getTotalStates() {
        return totalStates;
    }

    public Automaton cloneAutomaton() {
        Automaton cloned = new Automaton(this.totalStates, this.currentAutomata);
        for (Map.Entry<Integer, Set<Integer>> entry : this.transitions.entrySet()) {
            cloned.transitions.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        // Clone epsilon transitions
        for (Map.Entry<Integer, Set<Integer>> entry : this.epsilonTransitions.entrySet()) {
            cloned.epsilonTransitions.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        cloned.endStates = new HashSet<>(this.endStates);
        return cloned;
    }

    public HashMap<Integer, Set<Integer>> getTransitions() {
        return transitions;
    }

    public HashSet<Integer> getEndStates() {
        return endStates;
    }

    public void incrementTotalStates() {
        this.totalStates++;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Loaded automaton: ").append(currentAutomata).append("\n");
    
        // Regular transitions
        sb.append("Transitions:\n");
        for (Map.Entry<Integer, Set<Integer>> entry : transitions.entrySet()) {
            entry.getValue().forEach(toState -> sb.append("State ").append(entry.getKey()).append(" -> ").append(toState).append("\n"));
        }
    
        // Epsilon transitions
        if (!epsilonTransitions.isEmpty()) {
            sb.append("Epsilon Transitions:\n");
            for (Map.Entry<Integer, Set<Integer>> entry : epsilonTransitions.entrySet()) {
                entry.getValue().forEach(toState -> sb.append("State ").append(entry.getKey()).append(" -> \u03B5 -> ").append(toState).append("\n"));
            }
        }
    
        // End states
        sb.append("End states: ").append(endStates).append("\n");
        return sb.toString();
    }
    

    public String getCurrentAutomata() {
        return currentAutomata;
    }

    public void addEpsilonTransition(int fromState, int toState) {
        epsilonTransitions.computeIfAbsent(fromState, k -> new HashSet<>()).add(toState);
    }
}
