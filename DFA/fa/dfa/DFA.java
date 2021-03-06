package fa.dfa;

import fa.dfa.DFAInterface;
import fa.FAInterface;
import fa.State;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DFA.java generates a deterministic finite automata based on text input from 
 * DFADriver.java. The class creates a 5-tuple for the DFA containing a complete
 * list of states, the valid alphabet for the machine, a map of all possible 
 * transitions between states, the starting state for the machine, and the set
 * of all possible final states in the form (Q, sigma, delta, q0, F).
 * 
 * Once a DFA has been initialized, the user is allowed to print the 5-tuple
 * to the screen with each element on its own line to inspect the tuple, and 
 * test strings can be run through the automata using the accepts() method, which
 * will return the boolean true or false to let the user know whether or not the 
 * string is accepted.
 * 
 * There are also a handful of private methods designed to assist in building the
 * string for the toString() method.
 * 
 * @author James Souder and Jason Egbert
 *
 */
public class DFA implements DFAInterface, FAInterface {

	private LinkedHashSet<DFAState> Q;			// set of DFA states
	private LinkedHashSet<Character> sigma;		// alphabet for DFA
	private HashMap<String, DFAState> delta;	// map of all transitions for DFA
	private DFAState q0;						// start state for the DFA
	private LinkedHashSet<DFAState> F;			// Set of final states for the DFA

	/**
	 * Constructor for DFA object, initializes and creates the
	 * 5-tuple for the desired automata, readying the construction of
	 * the automata when further information is acquired.
	 * 
	 */
	public DFA() {
		Q = new LinkedHashSet<DFAState>();
		sigma = new LinkedHashSet<Character>();
		delta = new HashMap<String, DFAState>();
		q0 = null;
		F = new LinkedHashSet<DFAState>();
	}

	@Override
	public void addStartState(String name) {
		// second line of file, may already be in Q if it's also a
		// final state, we have to check first
		for(DFAState s: Q) {
			if(s.toString().equals(name)) {
				q0 = s;
				break;	// only run until you find it, or determine it's not there
			}
		}
		// if it's not in Q already, make it and put it there
		if(q0 == null) {
			q0 = new DFAState(name);
			Q.add(q0);
		}
	}

	@Override
	public void addState(String name) {
		// adding intermediate states, these should all be new
		Q.add(new DFAState(name));
	}

	@Override
	public void addFinalState(String name) {
		// first state or states given in file, always new state, 
		// always added to Q
		DFAState newState = new DFAState(name);
		
		F.add(newState);	// add to final state set F
		Q.add(newState);	// add to set of all states Q
	}

	@Override
	public void addTransition(String fromState, char onSymb, String toState) {
		String keyString = fromState + onSymb;
		DFAState transState = null;

		// finding toState in set Q
		for(DFAState state: Q) {
			if(state.toString().equals(toState)) {
				transState = state;
			}
		}

		// adding new transition to map
		delta.put(keyString, transState);

		// if onSymb is not already in alphabet, we put it there
		if(!sigma.contains(onSymb)) {
			sigma.add(onSymb);
		}
	}

	@Override
	public Set<? extends State> getStates() {
		return Q;
	}

	@Override
	public Set<? extends State> getFinalStates() {
		return F;
	}

	@Override
	public State getStartState() {
		return q0;
	}

	@Override
	public Set<Character> getABC() {
		return sigma;
	}

	@Override
	public boolean accepts(String s) {
		boolean accepts = true;
		DFAState currState = q0;

		// run through the string to see if it is valid
		for(int i = 0; i < s.length(); i++) {
			// if the character is in the alphabet
			if(sigma.contains(s.charAt(i))){
				// get the state it takes us to from current state
				currState = getToState(currState, s.charAt(i));
			} else if(s.charAt(i) == 'e') {
				accepts = true;
				break;
			} else {
				// otherwise, we don't accept the string & break the loop
				accepts = false;
				break;
			}
		}

		// if we made it through the loop, and the current state is a final state
		if(accepts && F.contains(currState)) {
			// we accept the string
			accepts = true;
		} else {
			// we reject the string
			accepts = false;
		}

		return accepts;
	}

	@Override
	public DFAState getToState(DFAState from, char onSymb) {
		// retrieves the next state from the transition table
		DFAState toState = delta.get(from.toString()+onSymb);

		return toState;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {

		// builds the return string
		String retString = getQ();
		retString += getSigma();
		retString += getDelta();
		retString += getQ0();
		retString += getF();

		return retString;	
	}

	/**
	 * Creates a string from the set of states in Q,
	 * in order to modularly build a string.
	 * 
	 * @return retStates - the string containing the list of states in set Q
	 */
	private String getQ() {
		// initial string info to get started
		String retStates = "Q = { ";
		
		// digs around in Q for the names of all the states, and formats them
		for(DFAState s: Q) {
			retStates += s.toString() + " ";
		}
		
		// end of string info
		retStates += "}\n";
		
		return retStates;
	}

	/**
	 * Creates a string from the set of alphabet characters, Sigma, in
	 * order to modularly construct a string to return to the user.
	 * 
	 * @return retAlphabet - the bracketed list of characters in the alphabet
	 */
	private String getSigma() {
		// initial string info
		String retAlphabet = "Sigma = { ";
		
		// digs in sigma to find all characters in the alphabet
		for(Character c: sigma) {
			retAlphabet += c + " ";
		}
		
		// end of string info
		retAlphabet += "}\n";
		
		return retAlphabet;
	}

	/**
	 * Creates a string table from the hashmap of transitions, delta, in
	 * order to modularly construct a string to return to the user
	 * 
	 * @return retDeltaTable - a table containing all of the transitions in the FA
	 */
	private String getDelta() {
		// important start and formatting infor for string
		String retDeltaTable = "delta =\n";
		retDeltaTable += "\t\t";

		// dig through sigma to first print alphabet options
		for(Character c: sigma) {
			retDeltaTable += c + "\t";
		}

		// next line
		retDeltaTable += "\n";

		//for every state in our set Q
		for(DFAState s: Q) {
			// print the state name
			retDeltaTable += "\t" + s.toString();

			// then print where the state goes on each character
			for(Character c: sigma) {
				retDeltaTable += "\t" + delta.get(s.toString()+c).toString();
			}

			// final newline, just to make sure everything is where it belongs
			retDeltaTable += "\n";
		}

		return retDeltaTable;
	}

	/**
	 * Creates a string from the start state, q0, in order to more
	 * modularly construct a string to return to the user.
	 * 
	 * @return q0 - as a string consisting of the state name
	 */
	private String getQ0() {
		return "q0 = " + q0.toString() + "\n";
	}

	/**
	 * Creates a string from the set of final states, F, in order to
	 * more modularly construct a string to return to the user.
	 * 
	 * @return retFinalStates - the bracketed set of final states for the FA
	 */
	private String getF() {
		// Initial string setup
		String retFinalStates = "F = { ";
		
		// dig through set F to find the names of all final states
		for(DFAState s: F) {
			retFinalStates += s.toString() + " ";
		}
		
		// end of string
		retFinalStates += "}\n";
		
		return retFinalStates;
	}
}
