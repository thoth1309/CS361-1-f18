package fa.nfa;

import java.util.*;

import fa.State;
import fa.dfa.DFA;
import fa.dfa.DFAState;

/**
 * The NFA class implements a non-deterministic finite automata from
 * a series of user inputs intended to be read from a file and parsed by
 * NFADriver.java. NFA then allows the user to convert the NFA into a
 * Deterministic Finite Automata of equivalent power, and for the same
 * language.
 *
 * @author James Souder and Jason Egbert
 */
public class NFA implements NFAInterface, fa.FAInterface{

    // Instance variables for NFA class
    private LinkedHashSet <NFAState> F;             // set of final states for NFA
    private NFAState q0;                            // start state for NFA
    private LinkedHashSet<NFAState> Q;              // set of NFA states
    private LinkedHashSet<Character> sigma;         // alphabet of the NFA
    private HashMap<String, Set<NFAState>> delta;   // transitions for the NFA states
    private static char EMPTYSTRING = 'e';          // Static variable representing the empty string

    /**
     * Constructor for NFA class. Initializes all instance variables
     * and constructs an NFA object. Allows for instance variables
     * to be implemented as they are discovered by NFADriver, or as
     * the user decides to implement them.
     *
     */
    public NFA() {
        // Construct NFA
        this.F = new LinkedHashSet<NFAState>();
        this.q0 = null;
        this.Q = new LinkedHashSet<NFAState>();
        this.sigma = new LinkedHashSet<Character>();
        this.delta = new HashMap<String, Set<NFAState>>();

    }

    @Override
    public void addStartState(String name) {
        // checks for state in set Q
        NFAState state = getStateInQ(name);

        // Adds state to Q only if it isn't already there
        if(state == null) {
            state = new NFAState(name);
            this.Q.add(state);
        }

        // Sets Start state, q0, to the current state
        this.q0 = state;
    }

    @Override
    public void addState(String name) {
        // adds the specified state to the set Q for the NFA
        this.Q.add(new NFAState(name));
    }

    @Override
    public void addFinalState(String name) {
        // Creates a new state from the provided state name
        NFAState state = new NFAState(name);
        // adds the state to the final state set, F
        F.add(state);
        // Also adds the new state to the list of NFA states Q
        Q.add(state);
    }

    @Override
    public void addTransition(String fromState, char onSymb, String toState) {
        // creates a key using the state name and the transition symbol
        String key = fromState + onSymb;

        // Checks to see if the key already exists. If it does, the new
        // transition is added to the set already at that key. Otherwise
        // a new set is generated and added using the specified key for
        // later retrieval
        NFAState actualState;
        if (!delta.containsKey(key)) {
            LinkedHashSet <NFAState> set = new LinkedHashSet<>();

            // finds the state in Q with the name toState, and adds it to
            // our set so we can put it in delta
            actualState = getStateInQ(toState);
            set.add(actualState);
            delta.put(key, set);
        } else {
            // adds the state to an existing set in delta
            actualState = getStateInQ(toState);
            delta.get(key).add(actualState);
        }

        // adds all transition characters to sigma, except the 'e', which is not a symbol
        if(!sigma.contains(onSymb)) {
            if(onSymb != EMPTYSTRING)
                sigma.add(onSymb);
        }
    }

    @Override
    public Set<? extends State> getStates() {
        return this.Q;
    }

    @Override
    public Set<? extends State> getFinalStates() {
        return this.F;
    }

    @Override
    public NFAState getStartState() {
        return this.q0;
    }

    @Override
    public Set<Character> getABC() {
        return this.sigma;
    }

    @Override
    public DFA getDFA() {
        // dfa, the DFA to be returned by this function, created using
        // the NFA and it's details.
        DFA dfa = new DFA();

        // retrieve the complete list of substates and a complete
        // list of all possible transitions to/from those states.
        // This data will be winnowed down later to send only the
        // relevant states and transitions to the DFA. qPrime is
        // the powerset of all states in the NFA
        LinkedHashSet<DFAState> qPrime = getPowerSet();

        // Initialize the set of final states and the start state, to be
        // determined in the next few lines.
        // also initializes the set of states Q to be passed in to the DFA
        LinkedHashSet<DFAState> finalStates = new LinkedHashSet<>();
        LinkedHashSet<DFAState> dfaQ = new LinkedHashSet<>();

        // sets up the dead state, so it's easier to find later
        DFAState deadState = getFromSet(qPrime, "");
        dfaQ.add(deadState);

        // add all transitions for every state in qPrime
        addDFATransitions(qPrime);

        // uses getToState function to establish the closure of q0,
        // thus allowing us to create our new start state.
        Set<NFAState> closure = eClosure(q0);

        // takes the set of states in closure, and turns the name into a
        // string to be used to create the DFA start state later.
        String startStateName = "";
        for(NFAState state: closure){
            startStateName = startStateName + state.getName();
        }

        // sort the string naturally so the states are named appropriately
        char[] chars = startStateName.toCharArray();
        Arrays.sort(chars);
        String sorted = new String(chars);

        // finds the start state in qPrime
        DFAState dfaStart = getFromSet(qPrime, sorted);

        // queue to search through and find all valid states from start to finish
        Queue<DFAState> searchQueue = new LinkedList<>();
        HashSet<DFAState> searched = new HashSet<>();

        // plug the start state into a queue for searching, and a queue for keeping track of it
        searchQueue.add(dfaStart);
        searched.add(dfaStart);

        // run through the queue, adding new states as we find them, and
        // saving the transitions while checking for final states. This
        // information will be passed in to the DFA later
        while(!searchQueue.isEmpty()) {
            DFAState tmpState = searchQueue.remove();

            // for every element of the alphabet, see where it goes from the
            // state we just removed from the queue, and add the new state to our dfaQ
            // If we haven't seen the state, we add it to our searchQueue, so we can
            // search it later. Otherwise, we add a transition to the dead state so
            // we can make sure we reject the string when necessary.
            for (char element : sigma) {
                DFAState nextState = tmpState.getTo(element);
                if(nextState != null) {
                    if(!searched.contains(nextState)) {
                        searchQueue.add(nextState);
                    }
                    dfaQ.add(nextState);
                    searched.add(nextState);
                } else {
                    tmpState.addTransition(element, deadState);
                }
            }

            // if dfaQ doesn't already have our current state, add it to the list
            if (!dfaQ.contains(tmpState)){
                dfaQ.add(tmpState);
            }

            // for every state in the list of final states, check to see if the name
            // of our temporary state has that character. If so, add it to the list
            // of final states and set it as final.
            for(NFAState finalState: F) {
                if (tmpState.getName().contains(finalState.getName())) {
                    tmpState.isFinal();
                    if(!finalStates.contains(tmpState)) {
                        finalStates.add(tmpState);
                    }
                    break;  // we found a final state, we don't need to keep looking
                }
            }
        }

        // add final states to dfa
        for(DFAState tmpState: finalStates){
            dfa.addFinalState(tmpState.getName());
        }

        // add start state to dfa
        dfa.addStartState(dfaStart.getName());

        // add remainder of states to dfa
        for(DFAState tmpState: dfaQ){
            Set<DFAState> checkStates = dfa.getStates();
            Set<String> stateNames = new LinkedHashSet<>();

            // for every state int he set checkStates, add its name to our stateNames set
            for(DFAState state: checkStates) {
                stateNames.add(state.getName());
            }

            // if the name of tmpstate isn't in our stateNames queue, add it now
            if(!stateNames.contains(tmpState.getName())) {
                dfa.addState(tmpState.getName());
            }
        }

        // add transitions based on states in dfaQ
        for(DFAState tmpState: dfaQ){
            for(char symbol: sigma){
                DFAState testState = tmpState.getTo(symbol);

                if(testState != null) {
                    dfa.addTransition(tmpState.getName(), symbol, testState.getName());
                }
            }
        }

        return dfa;
    }

    @Override
    public Set<NFAState> getToState(NFAState from, char onSymb) {
        return delta.get(from.getName() + onSymb);
    }

    @Override
    public Set<NFAState> eClosure(NFAState s) {
        // The closure of the state to be returned
        LinkedHashSet<NFAState> closure = new LinkedHashSet<>();

        // adds current state to the closure (first itsself)
        closure.add(s);

        // queue to keep track of where we can go from each
        // state for free
        Queue<NFAState> closureQueue = new LinkedList<>();

        // set of states we can get to from s for free
        Set<NFAState> stateSet = delta.get(s.getName() + EMPTYSTRING);
        HashSet<NFAState> stateTracker = new HashSet<>();

        // add all states from stateSet to the queue so we can add them
        // to the closure, and check to see if we can go anywhere from
        // them for free
        if(stateSet != null) {
            for (NFAState tmpState : stateSet) {
                closureQueue.add(tmpState);
                stateTracker.add(tmpState);
            }
        }

        // creates a copy of the closure
        Queue<NFAState> closureCopy = new LinkedList<>();

        for(NFAState element: closureQueue){
            closureCopy.add(element);
        }

        Set<NFAState> copySet;
        HashSet<NFAState> tracker = new HashSet<>();

        // looks for empty transitions from each of the elements in closurecopy,
        // and adds them to the closure queue
        while(!closureCopy.isEmpty()){
            NFAState tmp = closureCopy.remove();
            tracker.add(tmp);
            tracker.add(s);

            copySet = delta.get(tmp.getName()+EMPTYSTRING);

            if(copySet != null){
                for(NFAState tmpState : copySet)
                    if(!tracker.contains(tmpState)){
                        closureQueue.add(tmpState);
                        tracker.add(tmpState);
                        closureCopy.add(tmpState);
                    }
            }

        }

        // add all elements in closureQueue to the closure
        for(NFAState element : closureQueue){
            closure.add(element);
        }

        return closure;
    }

    /**
     * private function allowing easy traversal of the set Q to find
     * specific states. Avoids code duplication for a commonly used
     * set commands
     *
     * @param name - the name of the state you're trying to find.
     * @return returns the state from Q with the user specified name
     */
    private NFAState getStateInQ(String name) {
        NFAState tmp = null;

        // searches Q for the state with the name you want
        for(NFAState state : this.Q) {
            // if the name matches, save the state!
            if(state.toString().equals(name)) {
                tmp = state;
                break;  // we don't need to keep searching
            }
        }

        return tmp;
    }

    /**
     * Private function that uses string builder to create names for
     * NFA states from a the states in a set passed in by the user.
     *
     * @param states - the set of states from which we will glean a new name
     * @return the string representing the new state name
     */
    private String createStateName(Set<NFAState> states) {
        StringBuilder builder = new StringBuilder();

        for (NFAState state: states) {
            builder.append(state.toString());
        }

        // sorts the array naturally, before returning it to a string
        char[] chars = builder.toString().toCharArray();
        Arrays.sort(chars);
        String sorted = new String(chars);
        return sorted;
    }


    /**
     * getPowerSet takes the states from Q and combines them into the complete
     * set of substates forming qPrime, which will be used to generate an NFA,
     * including the empty set, or dead state.
     *
     * @return qPrime - the list of all subsets of states from Q
     */
    private LinkedHashSet<DFAState> getPowerSet() {
        // the return set of all possible states.
        LinkedHashSet<DFAState> qPrime= new LinkedHashSet<>();

        // Create an array from the states in Q
        NFAState[] element = new NFAState[Q.size()];

        int i=0;
        for(NFAState state: Q){
            element[i] = state;
            i++;
        }

        // Set a length of binary bits with length equal to the length
        // of Q
        final int SET_LENGTH = 1 << element.length;

        // Create a set of sets to store the powerset data in
        Set<Set<NFAState>> powerSet = new HashSet<>();

        // cycle through the sets, masking bits to get the
        // elements in the array element that we want, and place
        // them in our powerset, until we have the complete powerset
        for(int binarySet = 0; binarySet < SET_LENGTH; binarySet++){
            Set<NFAState> subset = new HashSet<>();
            for(int bit = 0; bit < element.length; bit++){
                int mask = 1 << bit;
                if((binarySet&mask) != 0){
                    subset.add(element[bit]);
                }
            }
            powerSet.add(subset);
        }

        // cycle through the sets in the powerset
        for(Set<NFAState> set: powerSet){
            String stateName = "";

            // take names of each NFAState in the set, and
            // concatenate them together to create a new
            // DFAState to add to qPrime
            for(Object thing: set){
                stateName = stateName + thing.toString();
            }

            char[] chars = stateName.toCharArray();
            Arrays.sort(chars);
            String sorted = new String(chars);

            DFAState newState = new DFAState(sorted);
            qPrime.add(newState);
        }

        return qPrime;
    }

    /**
     * Private function taking in a hashset of DFA states, and returning
     * a hashmap of transitions for each of those states. Since these are
     * DFA states, there is only one transition per character in the alphabet
     * per state in the list, so there is not need to store sets in the return
     * hashmap.
     *
     * @param qPrime - the set of states over which we will iterate
     * @return the full set of all possible transitions for qPrime
     */
    private void addDFATransitions(LinkedHashSet<DFAState> qPrime) {

        for(DFAState state: qPrime) {
            //make sure not empty state or dead state
            if(state.getName().equals("")) {
                for(char symbol: sigma) {
                    state.addTransition(symbol, state);
                }
                continue;
            }

            for(char symbol : sigma) {
                // get set that this char can transition to
                Set<NFAState> states = symbolClosure(state, symbol);
                DFAState dfaState;
                StringBuilder string = new StringBuilder();
                for(NFAState nfaState : states) {
                    string.append(nfaState.getName());
                }
                char[] chars = string.toString().toCharArray();
                Arrays.sort(chars);
                String sorted = new String(chars);

                dfaState = getFromSet(qPrime, sorted);
                state.addTransition(symbol, dfaState);
            }
        }
    }


    /**
     * Private method which returns the set containing the closure of a specified
     * state on a given symbol
     *
     * @param state - the state we're moving from
     * @param symbol - the symbol we're transitioning on
     * @return the set of NFAStates that make up the closure of the DFAState
     */
    private Set<NFAState> symbolClosure(DFAState state, char symbol) {
        Set<NFAState> nfaStates = new LinkedHashSet<>();

        String dfaName = state.getName();
        // parse name
        for(int i=0; i<dfaName.length(); i++) {
            // look up state in Q (NFA)
            String nfaStateNameChar = "" + dfaName.charAt(i);
            NFAState from = getStateInQ(nfaStateNameChar);
            Queue<NFAState> cycleQueue = new LinkedList<>();

            // get the eClosure of state, to search later. Add those states
            // to a search queue
            Set<NFAState> stateClosure = eClosure(from);
            for (NFAState subState: stateClosure) {
                cycleQueue.add(subState);
            }

            // Get all transitions from every state in the eClosure of state, and figure out
            // where we can go on the symbol from each of those characters
            while(!cycleQueue.isEmpty()){
                NFAState tmpState = cycleQueue.remove();
                Set<NFAState> transitions = delta.get(tmpState.getName()+symbol);
                if(transitions == null){
                    break;
                }
                for (NFAState toState: transitions) {
                    nfaStates.add(toState);

                }
            }

            // For every state that we found on our transition, find the eclosure,
            // and add it to our list of nfaStates.
            for (NFAState fromState: nfaStates) {
                Set<NFAState> transitions = eClosure(fromState);
                if(transitions == null){
                    break;
                }
                for (NFAState toState: transitions) {
                    nfaStates.add(toState);
                }
            }
        }

        return nfaStates;
    }

    /**
     * Private method to help find a state from a set of DFAStates, namely
     * qPrime, which we do repeatedly.
     *
     * @param set - the set we're searching
     * @param name - the name of the state we're searching for
     * @return the state from the set with the name we want
     */
    private DFAState getFromSet(Set<DFAState> set, String name) {
        DFAState retState = null;
        for(DFAState state : set) {
            if(state.getName().equals(name)) {
                retState = state;
                break;
            }
        }
        return retState;
    }
}
