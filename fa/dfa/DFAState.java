package fa.dfa;

import fa.State;

/**
 * DFAState creates a state object for a Deterministic Finite Automata.
 * The state object consists of a name, and that is it.
 * 
 * @author James Souder and Jason Egbert
 *
 */
public class DFAState extends State{
	private String name;	// the name of the state
	
	/**
	 * Creates an FA state, with a name
	 * 
	 * @param name
	 */
	public DFAState(String name ) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see fa.State#toString()
	 */
	public String toString() {
		return name;
	}	
}
