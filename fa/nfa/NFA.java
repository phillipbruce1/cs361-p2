package fa.nfa;

import fa.State;
import fa.dfa.DFA;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class NFA implements NFAInterface {
    // start state
    private NFAState startState;
    // HashMap to store transitions
    private final HashMap<String, String> transitions;
    // LinkedHashSet to store states
    private final LinkedHashSet<NFAState> states;
    // contains the alphabet in the language
    private final LinkedHashSet<Character> alphabet;

    /**
     * Constructor
     */
    public NFA() {
        states = new LinkedHashSet<>();
        transitions = new HashMap<>();
        alphabet = new LinkedHashSet<>();
    }

    /**
     * Check if start state is also a final state. Then create start state.
     *
     * @param name is the label of the start state
     */
    @Override
    public void addStartState(String name) {
        for (Object state : states.toArray()) {
            if (((NFAState) state).getName().equals(name)) {
                startState = (NFAState) state;
                return;
            }
        }
        startState = new NFAState(name, false);
        states.add(new NFAState(name, false));
    }

    /**
     * Adds a new state to the states set.
     *
     * @param name is the label of the state
     */
    @Override
    public void addState(String name) {
        states.add(new NFAState(name, false));
    }

    /**
     * Add final state to states set.
     *
     * @param name is the label of the state
     */
    @Override
    public void addFinalState(String name) {
        states.add(new NFAState(name, true));
    }

    /**
     * Adds a transition to transitions hashmap.
     *
     * @param fromState is the label of the state where the transition starts
     * @param onSymb    is the symbol from the NFA's alphabet.
     * @param toState   is the label of the state where the transition ends
     */
    @Override
    public void addTransition(String fromState, char onSymb, String toState) {
        // if onSymb is not in alphabet, add it
        if (onSymb != 'e')
            alphabet.add(onSymb);
        // check if start state and character have been used already
        if (transitions.get(fromState + onSymb) == null) {
            transitions.put(fromState + onSymb, toState);
        } else {
            // redo addition to hashmap and append new state to end of string
            transitions.put(fromState + onSymb, transitions.get(fromState + onSymb) + " " + toState);
        }
    }

    /**
     * Returns the set of all states.
     *
     * @return collection of all states
     */
    @Override
    public Set<? extends State> getStates() {
        return states;
    }

    /**
     * Iterate through all states and return all final states.
     *
     * @return set of final states
     */
    @Override
    public Set<? extends State> getFinalStates() {
        LinkedHashSet<NFAState> temp = new LinkedHashSet<>();
        for (Object state : states.toArray())
            if (((NFAState) state).isFinalState())
                temp.add((NFAState) state);
        return temp;
    }

    /**
     * Returns the start state.
     *
     * @return start state
     */
    @Override
    public State getStartState() {
        return startState;
    }

    /**
     * Returns a set of the alphabet.
     *
     * @return the alphabet
     */
    @Override
    public Set<Character> getABC() {
        return alphabet;
    }

    /**
     * Converts the NFA to a DFA and returns said DFA.
     *
     * @return equivalent DFA
     */
    @Override
    public DFA getDFA() {
        // String storing start state to add to DFA later
        String dfaStartState = "";
        // set to store a list of states in the DFA
        Set<String> dfaStates = new LinkedHashSet<>();
        // hashmap to store dfa state transitions (i.e. "ABC1", "ABD"), storing beginning and end of each transition
        HashMap<String, String> dfaTransitions = new HashMap<>();
        // queue to store next DFA states to explore
        Queue<String> stateQueue = new ArrayDeque<>();
        // add start state to begin
        stateQueue.add(startState.getName());
        boolean isStartState = true;
        while (!stateQueue.isEmpty()) {
            // remove the next state from the queue
            String currentState = stateQueue.remove();
            // ENSURING CURRENT STATE IS ACCURATE AND FULL
            // get eclosure of currentState
            Set<NFAState> eClosure = eClosureOfStates(currentState);
            // reassign current state to output of nfaSetToAlphabetizedString
            currentState = nfaSetToAlphabetizedString(eClosure);
            // if currentState is not the start state, add it to dfaStates. if it is the start state, assign it to dfaStartState
            if (isStartState) {
                isStartState = false;
                dfaStartState = currentState;
            } else {
                dfaStates.add(currentState);
            }
            // ADDING TRANSITIONS TO HASHMAP
            // iterate through each letter of the alphabet
            for (Object character : alphabet.toArray()) {
                LinkedHashSet<NFAState> nextState = new LinkedHashSet<>();
                // for each NFA state in currentState
                for (int i = 0; i < currentState.length(); i++) {
                    // use getToState to find all possible next states from the current NFA state on the given transition character
                    // if the next state(s) are not already in nextState, add them
                    nextState.addAll(getToState(Objects.requireNonNull(getState(Character.toString(currentState.charAt(i)))), (char) character));
                }
                if (!nextState.isEmpty()) {
                    String nextDFAState = nfaSetToAlphabetizedString(nextState);
                    // if nextDFAState is not already in stateQueue, dfaStartState or dfaStates, add nextDFAState to stateQueue
                    if (!nextDFAState.equals("") && !(stateQueue.contains(nextDFAState) | dfaStates.contains(nextDFAState) | dfaStartState.equals(nextDFAState)))
                        stateQueue.add(nextDFAState);
                    // add transition to dfaTransitions in form of {<currentState><transition character>, <nextDFAState> (i.e. {"ABC0", "BDE"})
                    dfaTransitions.put(currentState + Character.toString((char) character), nextDFAState);
                }
            }
        }
        // once stateQueue is empty, add all states and transitions to the DFA
        DFA dfa = new DFA();
        for (Object state : dfaStates.toArray())
            if (containsFinalState((String) state))
                dfa.addFinalState("[" + (String) state + "]");
            else
                dfa.addState("[" + (String) state + "]");
        dfa.addStartState("[" + dfaStartState + "]");
        // add transitions for each state/transition combo
        dfaStates.add(dfaStartState);
        AtomicBoolean containsNull = new AtomicBoolean(false);
        dfaStates.forEach(state -> {
            alphabet.forEach(c -> {
                if (dfaTransitions.get((String) state + Character.toString(c)) != null)
                    dfa.addTransition("[" + (String) state + "]", c, "[" + dfaTransitions.get((String) state + Character.toString(c)) + "]");
                else {
                    // if the null state is needed, add it
                    if (!containsNull.get()) {
                        containsNull.set(true);
                        dfa.addState("[]");
                        alphabet.forEach(f -> {
                            dfa.addTransition("[]", f, "[]");
                        });
                    }
                    dfa.addTransition("[" + (String) state + "]", c, "[]");
                }
            });
        });
        return dfa;
    }

    /**
     * Method to check if a given DFA state contains an NFA final state.
     *
     * @param state - State(s) to be checked
     * @return - True if state contains a final state, false otherwise
     */
    private boolean containsFinalState(String state) {
        for (int i = 0; i < state.length(); i++) {
            if (state.charAt(i) != ' ' && Objects.requireNonNull(getState(Character.toString(state.charAt(i)))).isFinalState())
                return true;
        }
        return false;
    }

    /**
     * Given a set of NFA states, returns the names of each state in a string in alphabetical order
     *
     * @param states - Set of states to be alphabetized and converted to a string
     * @return - String of states in alphabetical order
     */
    private String nfaSetToAlphabetizedString(Set<NFAState> states) {
        StringBuilder output = new StringBuilder();
        for (NFAState state : states) {
            if (output.length() == 0) {
                output.append(state.getName());
            } else {
                for (int i = 0; i < output.length(); i++) {
                    if (output.charAt(i) > state.getName().charAt(0)) {
                        output.insert(i, state.getName());
                        break;
                    } else if (i == output.length() - 1) {
                        output.append(state.getName());
                        break;
                    }
                }
            }
        }
        return output.toString();
    }

    /**
     * Method to find and return a target NFAState
     *
     * @param target - Name of the target state
     * @return - Target NFAState object, or null if the given state does not exist
     */
    private NFAState getState(String target) {
        if (target.equals(startState.getName()))
            return startState;
        for (Object nfaState : states.toArray())
            if (((NFAState) nfaState).getName().equals(target))
                return (NFAState) nfaState;
        return null;
    }

    /**
     * Gets all possible next states given a start position and a symbol.
     *
     * @param from   - the source state
     * @param onSymb - the label of the transition
     * @return set of next possible states
     */
    @Override
    public Set<NFAState> getToState(NFAState from, char onSymb) {
        // initialize hashset to return possible transition states
        LinkedHashSet<NFAState> possibleStates = new LinkedHashSet<>();
        // get next transition(s) and create tokenizer to iterate through next state(s)
        String next = transitions.get(from.getName() + onSymb);
        // if there are no transitions for the given symbol, return an empty set
        if (next == null) {
            return possibleStates;
        }
        // iterate through all possible next states and find their respective NFAState object
        for (StringTokenizer tk = new StringTokenizer(next, " "); tk.hasMoreTokens(); ) {
            possibleStates.add(getState(tk.nextToken()));
        }
        // if being called by eClosure method, end early
        if (onSymb == 'e')
            return possibleStates;
        // account for empty transitions
        LinkedHashSet<NFAState> allPossibleStates = new LinkedHashSet<>();
        for (Object state : possibleStates.toArray())
            for (Object eclosureState : eClosure((NFAState) state).toArray())
                allPossibleStates.add((NFAState) eclosureState);
        // return all possible next states
        return allPossibleStates;
    }

    /**
     * Returns the eClosure of the given NFA states as a Set of NFAStates
     *
     * @param states - String containing names of all states to get the eclosure of
     * @return - Set of NFAStates
     */
    private Set<NFAState> eClosureOfStates(String states) {
        LinkedHashSet<NFAState> output = new LinkedHashSet<>();
        // for each state in states, if the character is not a space
        for (int i = 0; i < states.length(); i++) {
            if (states.charAt(i) != ' ') {
                // get the NFAState of the current state
                NFAState currentState = getState(Character.toString(states.charAt(i)));
                // if we have not alreaedy visited the current state
                if (!output.contains(currentState)) {
                    // get its eclosure and add all states to output
                    Set<NFAState> eClosureOfCurrentState = eClosure(currentState);
                    for (Object state : eClosureOfCurrentState.toArray())
                        output.add((NFAState) state);
                }
            }
        }
        return output;
    }

    /**
     * Return a set of which states can be reached from the given state on only empty transitions.
     *
     * @param s state to start at
     * @return set of states which can be reached from s on empty transitions
     */
    @Override
    public Set<NFAState> eClosure(NFAState s) {
        // get all next states which can be reaches on an empty transition
        Set<NFAState> nextStates = getToState(s, 'e');
        // for each next possible state, see if there are further states which can be reached
        for (Object state : nextStates.toArray())
            // for each state returned by eClosureRecursive, add it to nextStates
            for (Object level2State : eClosureRecursive((NFAState) state, nextStates))
                nextStates.add((NFAState) level2State);
        nextStates.add(s);
        return nextStates;
    }

    /**
     * Recursively finds any potential states which can be reached on
     * an empty transition.
     *
     * @param s              starting state
     * @param previousStates set of all previously explored states
     * @return set of all possible states
     */
    private Set<NFAState> eClosureRecursive(NFAState s, Set<NFAState> previousStates) {
        // get all next states which can be reaches on an empty transition
        Set<NFAState> nextStates = getToState(s, 'e');
        Set<NFAState> output = previousStates;
        // for each next possible state, see if there are further states which can be reached
        for (Object state : nextStates.toArray())
            // if the next state has already been explored, ignore it
            if (!output.contains((NFAState) state)) {
                // otherwise, add it to output
                output.add((NFAState) state);
                // for each state returned by eClosureRecursive, add it to nextStates
                for (Object level2State : eClosureRecursive((NFAState) state, output))
                    output.add((NFAState) level2State);
            }
        return output;
    }
}
