// Original code for concatenation and star operations by PeterDomC on GitHub:
// https://github.com/PeterDomC/Lifa/blob/main/src/automataAlgorithms/Operations.java

import java.util.HashSet;
// import java.util.Map;
// import java.util.Set;

public class AutomatonOperations {

    public static Automaton square(Automaton originalAutomaton) {
        int originalTotalStates = originalAutomaton.getTotalStates();
        Automaton squaredAutomaton = new Automaton(originalTotalStates * 2,
                originalAutomaton.getCurrentAutomata() + "^2");

        // Copy original transitions and adjust for the squared automaton
        for (int i = 0; i < originalTotalStates; i++) {
            int fromState = i;
            int toState = originalAutomaton.getTransitions().getOrDefault(i, new HashSet<>()).iterator().next();
            squaredAutomaton.addTransition(fromState, toState); // Copy original transitions
            squaredAutomaton.addTransition(fromState + originalTotalStates, toState + originalTotalStates); // Adjusted copy for the square
        }

        // In the square operation, after copying transitions
        for (int endState : originalAutomaton.getEndStates()) {
            // Assuming the start state of the duplicate is 'originalTotalStates'
            squaredAutomaton.addEpsilonTransition(endState, originalTotalStates); // Directly to the start of the duplicate
            squaredAutomaton.addEndState(endState + originalTotalStates);
        }

        return squaredAutomaton;
    }

    public static Automaton star(Automaton automaton) {
        Automaton newAutomaton = automaton.cloneAutomaton();
        int newStartState = newAutomaton.getTotalStates(); // New state becomes the new start state
        newAutomaton.incrementTotalStates();

        newAutomaton.addEndState(newStartState); // New start state is also an accepting state

        newAutomaton.addEpsilonTransition(newStartState, 0); // Epsilon transition to original start state

        // Epsilon transitions from every accepting state back to the new start state
        automaton.getEndStates().forEach(endState -> {
            if (endState != newStartState) { // Avoid loop on the new start state itself
                newAutomaton.addEpsilonTransition(endState, newStartState);
            }
        });

        return newAutomaton;
    }

    // private static void applyStarOperation(Automaton automaton) {
    // int newStartState = automaton.getTotalStates(); // New state becomes the new
    // start state
    // automaton.incrementTotalStates();
    // New start state is also an accepting state
    // automaton.addEndState(newStartState);
    // Add a transition from the new start state to the original start state to
    // accept the empty string
    // automaton.addTransition(newStartState, 0);

    // For each end state, add a transition back to the new start state (to enable
    // repetition)
    // automaton.getEndStates().forEach(endState ->
    // automaton.addTransition(endState, newStartState));

    // Ensure the original start state (State 0) maintains its existing transition
    // This step may need adjustment based on your automaton's design to ensure
    // unary compliance

    // For each original end state, ensure it transitions to allow repetition
    // Note: For unary automata, this may involve redirecting the original
    // transition to the new start state if necessary
    // This might require careful handling to not violate the unary property
    // }

    public static Automaton concatenate(Automaton first, Automaton second) {
        // Calculate the total number of states and create a new concatenated automaton
        int totalStates = first.getTotalStates() + second.getTotalStates();
        Automaton concatenated = new Automaton(totalStates,
                first.getCurrentAutomata() + "+" + second.getCurrentAutomata());

        // Copy all transitions from the first automaton
        first.getTransitions().forEach((state, transitions) -> transitions
                .forEach(transition -> concatenated.addTransition(state, transition)));

        // Adjust and copy all transitions from the second automaton, offset by the size
        // of the first
        second.getTransitions().forEach((state, transitions) -> transitions.forEach(transition -> concatenated
                .addTransition(state + first.getTotalStates(), transition + first.getTotalStates())));

        // For each end state of the first automaton, add a transition to the start
        // state of the second automaton
        first.getEndStates().forEach(endState -> {
            // Ensure not to introduce multiple transitions for unary NFAs
            if (second.getTotalStates() > 0) { // Check if the second automaton is not empty
                concatenated.addTransition(endState, first.getTotalStates()); // Transition to the start of the second automaton
            }
        });

        // Set the end states of the concatenated automaton based on the second
        // automaton's end states
        second.getEndStates().forEach(endState -> concatenated.addEndState(endState + first.getTotalStates()));

        return concatenated;
    }
}