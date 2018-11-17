package fa.nfa;
import fa.State;
public class NFAState extends State{
	private boolean isFinal;

    /**
     * Constructs an NFAState object from a string, which will serve
     * as the name of the state.
     *
     * @param name
     */
	public NFAState(String name) {
		this.name = name;
		this.isFinal = false;
	}

    /**
     * Is the state final?
     */
	public void setFinal(){
		isFinal = true;
	}

    /**
     * Whether or not the state is final
     *
     * @return the finality of the state
     */
	public boolean isFinal(){
		return isFinal;
	}
	
	@Override
	public String toString() {
		return this.name;
	}

}
