package fa.nfa;

import fa.State;

/**
 * Contains an NFA state.
 */
public class NFAState extends State {
    private final boolean isFinalState;

    public NFAState(String name, boolean isFinalState) {
        this.name = name;
        this.isFinalState = isFinalState;
    }

    public boolean isFinalState() {
        return isFinalState;
    }
}
