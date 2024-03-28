// Original code for concatenation and star operations by PeterDomC on GitHub:
// https://github.com/PeterDomC/Lifa/blob/main/src/automataAlgorithms/Operations.java
// Original code for NFA to DFA conversion and DFA minimization from the dk.brics.automaton library:
// https://github.com/cs-au-dk/dk.brics.automaton

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            squaredAutomaton.addTransition(fromState + originalTotalStates, toState + originalTotalStates); // Adjusted
                                                                                                            // copy for
                                                                                                            // the
                                                                                                            // square
        }

        // In the square operation, after copying transitions
        for (int endState : originalAutomaton.getEndStates()) {

            squaredAutomaton.addEpsilonTransition(endState, originalTotalStates); // Directly to the start of the
                                                                                  // duplicate
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

    public static Automaton concatenate(Automaton firstAutomaton, Automaton secondAutomaton) {
        int totalStates = firstAutomaton.getTotalStates() + secondAutomaton.getTotalStates();
        Automaton concatenatedAutomaton = new Automaton(totalStates,
                firstAutomaton.getCurrentAutomata() + "+" + secondAutomaton.getCurrentAutomata());

        // Copy transitions from the first automaton
        for (int i = 0; i < firstAutomaton.getTotalStates(); i++) {
            final int currentState = i; // Final variable for lambda expression
            firstAutomaton.getTransitions().getOrDefault(i, new HashSet<>())
                    .forEach(toState -> concatenatedAutomaton.addTransition(currentState, toState));
        }

        // Copy transitions from the second automaton, adjusting their states
        for (int i = 0; i < secondAutomaton.getTotalStates(); i++) {
            final int offsetState = i + firstAutomaton.getTotalStates(); // Adjusted state index
            secondAutomaton.getTransitions().getOrDefault(i, new HashSet<>()).forEach(toState -> concatenatedAutomaton
                    .addTransition(offsetState, toState + firstAutomaton.getTotalStates()));
        }

        // Connect the end states of the first automaton to the start state of the
        // second automaton
        firstAutomaton.getEndStates().forEach(
                endState -> concatenatedAutomaton.addEpsilonTransition(endState, firstAutomaton.getTotalStates()));

        // Set the end states of the concatenated automaton to those of the second
        // automaton, adjusted
        secondAutomaton.getEndStates()
                .forEach(endState -> concatenatedAutomaton.addEndState(endState + firstAutomaton.getTotalStates()));

        return concatenatedAutomaton;
    }

    // Static variable to keep track of the start state ID for the DFA
    private static int startStateId;

    public static Automaton convertToDFA(Automaton nfa) {
        // A mapping from sets of NFA states to DFA state IDs
        Map<Set<Integer>, Integer> stateMapping = new HashMap<>();
        // Creating a new DFA automaton, initializing it with 0 states and a modified
        // name to indicate conversion
        Automaton dfa = new Automaton(0, nfa.getCurrentAutomata() + "_DFA");
        // A list to manage sets of NFA states that need to be processed
        List<Set<Integer>> statesToProcess = new LinkedList<>();
        // Counter for DFA states; it's used to assign unique IDs to new DFA states
        int dfaStateCounter = 0;

        // Reset startStateId to -1 for a new conversion process
        startStateId = -1;

        // Calculate the epsilon closure of the NFA's start state and add it to the
        // states to be processed
        Set<Integer> startStateClosure = nfa.epsilonClosure(Collections.singleton(0));
        statesToProcess.add(startStateClosure);
        // Map the start state closure to the first DFA state (ID 0)
        stateMapping.put(startStateClosure, dfaStateCounter++);
        // Explicitly setting the start state ID of the DFA to 0
        startStateId = 0;

        //System.out.println("Initial DFA state (start state closure): " + startStateClosure + " as state 0");
        // If the start state closure includes any of NFA's end states, mark the
        // corresponding DFA state as an end state
        if (startStateClosure.stream().anyMatch(nfa.getEndStates()::contains)) {
            dfa.addEndState(startStateId);
            //System.out.println("Adding start state as end state: " + startStateId);
        }
        //System.out.println("Starting NFA to DFA conversion...");

        // Process each set of NFA states until there are no more states to process
        while (!statesToProcess.isEmpty()) {
            Set<Integer> current = statesToProcess.remove(0);
            int currentDFAState = stateMapping.get(current);

            Set<Integer> newState = new HashSet<>();
            // For each state in the current set, calculate the set of states reachable
            // through transitions
            for (Integer state : current) {
                Set<Integer> nextStates = nfa.getTransitions(state);
                for (Integer nextState : nextStates) {
                    newState.addAll(nfa.epsilonClosure(Collections.singleton(nextState)));
                }
            }

            // If this new set of states doesn't already have a corresponding DFA state,
            // create one
            if (!stateMapping.containsKey(newState)) {
                stateMapping.put(newState, dfaStateCounter);
                statesToProcess.add(newState);
                //System.out.println("Adding new DFA state: " + newState + " as state " + dfaStateCounter);
                // Check if the new set of states includes any end states and mark the DFA state
                // accordingly
                if (newState.stream().anyMatch(nfa.getEndStates()::contains)) {
                    dfa.addEndState(dfaStateCounter);
                    //System.out.println("Adding end state: " + dfaStateCounter);
                }
                dfaStateCounter++;
            }

            // Add a transition in the DFA from the current DFA state to the DFA state
            // corresponding to the new set of states
            int newStateDFAId = stateMapping.get(newState);
            dfa.addTransition(currentDFAState, newStateDFAId);
            //System.out.println("Adding transition from DFA state " + currentDFAState + " to DFA state " + newStateDFAId);
        }

        // After processing all states, set the total number of states in the DFA
        dfa.setTotalStates(dfaStateCounter);
        //System.out.println("DFA conversion complete. Total states: " + dfaStateCounter);
        return dfa;
    }

    public static Automaton minimizeDFA(Automaton dfa) {
        //System.out.println("Starting DFA minimization...");
        // Identify the accepting (end) states of the DFA
        Set<Integer> acceptingStates = dfa.getEndStates();
        // A set to store non-accepting states, initially empty
        Set<Integer> nonAcceptingStates = new HashSet<>();
        // Populate the nonAcceptingStates set by checking each state
        for (int i = 0; i < dfa.getTotalStates(); i++) {
            if (!acceptingStates.contains(i))
                nonAcceptingStates.add(i);
        }

        // Create initial partitions of accepting and non-accepting states
        List<Set<Integer>> partitions = new ArrayList<>();
        partitions.add(acceptingStates);
        if (!nonAcceptingStates.isEmpty())
            partitions.add(nonAcceptingStates);
        //System.out.println("Initial partitions: Accepting=" + acceptingStates + ", Non-accepting=" + nonAcceptingStates);

        // Boolean to keep track of whether the partitions changed in the last iteration
        boolean changed;
        do {
            changed = false; // Reset the flag at the start of each iteration
            List<Set<Integer>> newPartitions = new ArrayList<>();
            // Evaluate each current partition for possible splitting
            for (Set<Integer> partition : partitions) {
                //System.out.println("Evaluating partition: " + partition);
                List<Set<Integer>> splitResults = splitPartition(partition, dfa, partitions);
                //System.out.println("Split results: " + splitResults);
                // If a partition was split, set changed to true
                if (splitResults.size() > 1)
                    changed = true;
                newPartitions.addAll(splitResults);
            }
            // Update partitions for the next iteration
            partitions = new ArrayList<>(newPartitions);
        } while (changed); // Continue while changes are happening
        //System.out.println("Minimization complete. Final partitions: " + partitions);

        // After finalizing partitions, construct the minimized DFA
        Automaton minimizedDFA = new Automaton(0, dfa.getCurrentAutomata() + "_minimized");
        // A map from partitions to their new state IDs in the minimized DFA
        Map<Set<Integer>, Integer> partitionToStateID = new HashMap<>();
        int newStateID = 0; // Counter for assigning new state IDs

        // Assign each partition a new state ID and mark end states
        for (Set<Integer> partition : partitions) {
            minimizedDFA.incrementTotalStates();
            partitionToStateID.put(partition, newStateID);
            if (partition.stream().anyMatch(dfa.getEndStates()::contains)) {
                minimizedDFA.addEndState(newStateID); // Mark as end state if it contains any of DFA's original end
                                                      // states
            }
            newStateID++;
        }

        // Set up transitions for the minimized DFA based on the original DFA's
        // transitions and the new partitions
        for (Map.Entry<Set<Integer>, Integer> partitionEntry : partitionToStateID.entrySet()) {
            Set<Integer> partition = partitionEntry.getKey();
            Integer newFromState = partitionEntry.getValue();

            final List<Set<Integer>> finalPartitions = new ArrayList<>(partitions); // A final copy to use inside
                                                                                    // lambdas

            // Set transitions for each state in the partition
            for (Integer state : partition) {
                dfa.getTransitions(state).forEach(originalToState -> {
                    Set<Integer> targetPartition = findPartitionThatContains(originalToState, finalPartitions);
                    if (targetPartition != null) {
                        Integer newToState = partitionToStateID.get(targetPartition);
                        minimizedDFA.addTransition(newFromState, newToState);
                    }
                });
            }
        }

        minimizedDFA.setTotalStates(newStateID);
        //System.out.println("Minimization complete. Total states: " + newStateID);
        return minimizedDFA;
    }

    // Splits a given partition into finer partitions based on their
    // distinguishability
    private static List<Set<Integer>> splitPartition(Set<Integer> partition, Automaton dfa,
            List<Set<Integer>> currentPartitions) {
        Map<Integer, Set<Integer>> newPartitions = new HashMap<>();

        // Iterate over each state in the partition
        for (Integer state : partition) {
            Set<Integer> transitions = dfa.getTransitions(state);
            Integer targetState = transitions.isEmpty() ? null : transitions.iterator().next();

            // Determine which partition the targetState belongs to
            Integer targetPartitionIndex = findPartitionIndex(targetState, currentPartitions);
            // Group states by the partition index of their target state
            newPartitions.computeIfAbsent(targetPartitionIndex, k -> new HashSet<>()).add(state);
        }

        return new ArrayList<>(newPartitions.values());
    }

    // Finds the index of the partition that contains the given state
    private static Integer findPartitionIndex(Integer state, List<Set<Integer>> partitions) {
        for (int i = 0; i < partitions.size(); i++) {
            if (state == null || partitions.get(i).contains(state)) {
                return i; // Index of the partition containing the state
            }
        }
        return null; // Indicates the state wasn't found in any partition
    }

    // Finds the partition that contains the given state
    private static Set<Integer> findPartitionThatContains(Integer state, List<Set<Integer>> partitions) {
        for (Set<Integer> partition : partitions) {
            if (partition.contains(state)) {
                return partition; // The partition containing the state
            }
        }
        return null; // Indicates no partition contains the state
    }

}