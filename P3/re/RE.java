package re;

import fa.nfa.NFA;

import java.util.ArrayList;

public class RE implements REInterface {
    private NFA nfa;
    String regex;

    /**
     * Constructor for the Regular Expression object. Takes in a string
     * representing a regular expression, and initializes all class
     * objects.
     *
     * @param regex
     */
    public RE(String regex){
        nfa = new NFA();    // Initialize our NFA
        this.regex = regex; // save the regular expression
    }

    @Override
    public NFA getNFA() {
        ArrayList<String> terms = new ArrayList();
        // TODO: create a recursive decent (but not a parse tree)
        // TODO: that will allow you to find your states and transitions
        // TODO: you don't so much care about how you get to the states,
        // TODO: just THAT you get to them, and recognize them correctly.



        return null;
    }
}
