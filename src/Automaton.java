import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

// Definition of the Automaton class representing a finite automaton, 
// including deterministic (DFA) and nondeterministic (NFA) automata
class Automaton {
    // Maps each state to a set of states it can transition to, for regular transitions
    private HashMap<Integer, Set<Integer>> transitions;
    // A set containing all the end (accepting) states of the automaton
    private HashSet<Integer> endStates;
    // The total number of states in the automaton
    private int totalStates;
    // A string representation or identifier of the current automaton
    private String currentAutomata;
    // Maps each state to a set of states it can transition to via epsilon (ε) transitions
    private HashMap<Integer, Set<Integer>> epsilonTransitions;

    // Constructor to initialize the automaton with a given number of states and its identifier
    public Automaton(int totalStates, String currentAutomata) {
        this.totalStates = totalStates;
        this.transitions = new HashMap<>();
        this.endStates = new HashSet<>();
        this.currentAutomata = currentAutomata;
        this.epsilonTransitions = new HashMap<>();
    }

    // Adds a transition from one state to another
    public void addTransition(int fromState, int toState) {
        transitions.computeIfAbsent(fromState, k -> new HashSet<>()).add(toState);
    }

    // Marks a state as an end (accepting) state
    public void addEndState(int state) {
        endStates.add(state);
    }

    // Checks if at least half of the states are accepting states
    public boolean hasUpToHalfEndStates() {
        int halfEndStates = totalStates / 2;
    
        if (totalStates % 2 == 1) {
            return endStates.size() == halfEndStates;
        } else {
            return endStates.size() == halfEndStates;
        }
    }

    // Returns the total number of states in the automaton
    public int getTotalStates() {
        return totalStates;
    }

    // Creates a copy of the current automaton
    public Automaton cloneAutomaton() {
        Automaton cloned = new Automaton(this.totalStates, this.currentAutomata);
        for (Map.Entry<Integer, Set<Integer>> entry : this.transitions.entrySet()) {
            cloned.transitions.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        for (Map.Entry<Integer, Set<Integer>> entry : this.epsilonTransitions.entrySet()) {
            cloned.epsilonTransitions.put(entry.getKey(), new HashSet<>(entry.getValue()));
        }
        cloned.endStates = new HashSet<>(this.endStates);
        return cloned;
    }

    // Retrieves the set of transitions for a given state
    public HashMap<Integer, Set<Integer>> getTransitions() {
        return transitions;
    }

    // Retrieves the set of end (accepting) states
    public HashSet<Integer> getEndStates() {
        return endStates;
    }

    // Increments the total number of states by one
    public void incrementTotalStates() {
        this.totalStates++;
    }

    // Returns a string representation of the automaton, including its transitions and end states
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Loaded automaton: ").append(currentAutomata).append("\n");
        if (!transitions.isEmpty()) {
            sb.append("Transitions:\n");
            transitions.forEach((fromState, toStates) -> {
                toStates.forEach(toState -> sb.append("State ").append(fromState).append(" -> ").append(toState).append("\n"));
            });
        } else {
            sb.append("No regular transitions recorded.\n");
        }
        if (!epsilonTransitions.isEmpty()) {
            sb.append("Epsilon Transitions:\n");
            epsilonTransitions.forEach((fromState, toStates) -> {
                toStates.forEach(toState -> sb.append("State ").append(fromState).append(" -> ε -> ").append(toState).append("\n"));
            });
        } else {
            sb.append("No epsilon transitions.\n");
        }
        sb.append("End states: ").append(endStates).append("\n");
        return sb.toString();
    }
    public String toCompactString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Automaton ID: ").append(currentAutomata).append(" | Transitions: ");
        transitions.forEach((from, toSet) -> {
            sb.append(from).append("->").append(toSet).append(" ");
        });
        sb.append("| End States: ").append(endStates);
        return sb.toString().trim();
    }

    // Retrieves the identifier of the current automaton
    public String getCurrentAutomata() {
        return currentAutomata;
    }

    // Adds an epsilon transition from one state to another
    public void addEpsilonTransition(int fromState, int toState) {
        epsilonTransitions.computeIfAbsent(fromState, k -> new HashSet<>()).add(toState);
    }

    // Computes the epsilon closure of a given set of states, 
    // which is the set of states reachable from any state in the set including the state itself
    public Set<Integer> epsilonClosure(Set<Integer> states) {
        Set<Integer> closure = new HashSet<>(states);
        Stack<Integer> stack = new Stack<>();
        states.forEach(stack::push);
        while (!stack.isEmpty()) {
            int currentState = stack.pop();
            Set<Integer> epsilonTargets = this.epsilonTransitions.getOrDefault(currentState, new HashSet<>());
            for (Integer target : epsilonTargets) {
                if (closure.add(target)) {
                    stack.push(target);
                }
            }
        }
        return closure;
    }

    // Checks if any of the given states is an accepting state
    public boolean isAcceptingState(Set<Integer> states) {
        for (Integer state : states) {
            if (this.endStates.contains(state)) {
                return true;
            }
        }
        return false;
    }

    // Retrieves the transitions for a specific state
    public Set<Integer> getTransitions(Integer state) {
        return transitions.getOrDefault(state, new HashSet<>());
    }

    // Sets the total number of states in the automaton
    public void setTotalStates(int totalStates) {
        this.totalStates = totalStates;
    }  
}