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
        nfa = new NFA();
        this.regex = regex;
    }

    @Override
    public NFA getNFA() {
        ArrayList<String> terms = new ArrayList();

        return null;
    }
}
