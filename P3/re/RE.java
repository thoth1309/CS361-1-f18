package re;

import fa.State;
import fa.nfa.NFA;
import fa.nfa.NFAState;

import java.util.Set;

/**
 * RE takes a String to parse as a Regular Expression. This class assumes that
 * input will be in a correct format (i.e., a regular expression using '|' for
 * or, '*' for star, '(' and ')' for grouping, and placing two characters next
 * to each other to indicate concatenation. All other characters are assumed
 * to be alphabet characters for the language.
 *
 * The input string will be parsed into an NFA which recognizes the language
 * of the Regular Expression upon calling the method getNFA(). All other
 * methods in the class are helper methods, and therefore are private to RE.
 *
 * This class implements the REInterface interface class.
 *
 * @author James Souder and Jason Egbert
 *
 */
public class RE implements REInterface {
    private String regex;   // the string to be used as a regular expression
    private int stateNamer; // the integer to be used to name states uniquely

    /**
     * Constructor for the Regular Expression object. Takes in a string
     * representing a regular expression, and initializes all class
     * objects.
     *
     * @param regex - the regular expression to be parsed
     *
     */
    public RE(String regex){
        this.regex = regex; // save the regular expression
        stateNamer = 0; // a unique name generator for NFA states
    }

    @Override
    public NFA getNFA() {   // parse regex, turn it into NFA
        NFA nfa; // create an empty NFA

        nfa = regex();  // parse the regular expression into an NFA

        return nfa;
    }

    /**
     * Parses the regular expression into an NFA by first passing off the regex string
     * to term to be parsed for terms, and then by checking for the or symbol.
     * If the or symbol is present, a union is performed between the first NFA and the
     * second NFA to create the return NFA.
     *
     * @return - an NFA based on a regular expression
     *
     */
    private NFA regex(){
        NFA termNfa = parseTerm();  // create an NFA from the next term

        if(more() && peek() == '|') {
            eat ('|');  // consume the or character
            NFA secondNfa = regex();   // create an NFA from the next regex after the or

            termNfa = unionNFA(termNfa,secondNfa);   // make termNfa equal the union of itself and subNfa
          }

        return termNfa;
    }

    /**
     * Parses the terms in the regular expression into an NFA by separating out the factors
     * and passing them to the factor function. If the function has already created an NFA,
     * then any new NFAs will be concatenated to it through the concatenation funciton.
     *
     * @return - an NFA based on a regex term
     *
     */
    private NFA parseTerm() {
        NFA factorNfa = null;   // create an empty NFA, ensure it's null(fact used later)

        // while we're still in our same term, keep parsing
        while(more() && peek() != ')' && peek() != '|') {

            // if factorNfa hasn't been touched, create it and parse the next factor
            if(factorNfa == null) {
                factorNfa = parseFactor();  // create an NFA from the factor
            } else {    // if factorNfa exists, we need to concatenate
                NFA appendNfa = parseFactor(); // what we're concatenating to factorNfa
                NFA joinedNfa; // where we're storing it all (i.e. a temporary NFA)
                joinedNfa = concatNFA(factorNfa,appendNfa); // concatenate our two NFAs

                factorNfa = joinedNfa;  // set NFA equal to the return NFA
            }
        }

        return factorNfa;
    }

    /**
     * Parses the factors of the regular expression into an NFA by separating out the
     * bases and turning them into single character NFAs. After it's parsed a base, it
     * checks for the * operator and if found it links the final states back to the start
     * state via empty transitions.
     *
     * @return - an NFA built based on a regex factor
     *
     */
    private NFA parseFactor() {
        NFA baseNFA = parseBase();  // create an NFA by parsing the base of the factor

        // while we have a '*' operator at the end of our factor, make sure it loops
        while (more() && peek() == '*') {
            eat('*');   // get rid of the '*' so we can keep parsing
            Set<State> stateSet = baseNFA.getFinalStates(); // pull the set of final states from baseNFA

            // for every final state, make a transition back to the start state
            for(State s: stateSet) {
                baseNFA.addTransition(s.getName(), 'e', baseNFA.getStartState().getName());
            }

            NFAState newStart = new NFAState(stateNamer++ + "");    // create a new state (to replace start)
            baseNFA.addState(newStart.getName());   // add the state to baseNFA
            baseNFA.addTransition(newStart.getName(),'e',baseNFA.getStartState().getName());    // add trans. to prev. start state
            baseNFA.addStartState(newStart.getName());  // make new state the start state
            baseNFA.addFinalState(newStart.getName());  // make the new state a final state also
        }

        return baseNFA;
    }

    /**
     * Parses through a base to determine what transitions we're going to
     * make. Also sends individual characters to parseChar, to ensure that
     * they are not inadvertently forgotten in our alphabet.
     *
     * @return - an NFA made from a base term in the regex
     *
     */
    private NFA parseBase() {
        NFA baseNfa;    // create an NFA
        baseNfa = new NFA();    // initialize

        // see what's next!
        // we have a regex, not a char
        // it's literally any other possible character
        if (peek() == '(') {
            eat('(');   // get rid of the opening parenthesis
            NFA rNfa = regex(); // parse the regex into an NFA
            eat(')');   // upon return, get rid of the closing parenthesis
            baseNfa = rNfa; // set return NFA equal to rNfa
        } else {
            NFAState fromState = new NFAState(stateNamer++ + "");   // create a new start state
            NFAState toState = new NFAState(stateNamer++ + ""); // create a new end state

            baseNfa.addFinalState(toState.getName());   // make the end state final
            baseNfa.addStartState(fromState.getName()); // make the start state a start state
            baseNfa.addTransition(fromState.getName(), next(), toState.getName());  // transition between the two on the base character
        }

        return baseNfa;
    }

    /**
     * Private method to peek ahead in the regular expression, to see what's
     * coming next
     *
     * @return - the next character in the regular expression
     *
     */
    private char peek() {
        return regex.charAt(0); // see what the first character is!
    }

    /**
     * consume the next character in the regular expression, if the next
     * character is the character passed in. If it is not, an exception
     * will be thrown to alert the user
     *
     * @param c - the character to be consumed, if it is the next character in regex
     *
     */
    private void eat(char c) {
        if(peek() == c) // if the first character is what we're looking for
            regex = regex.substring(1); // toss it and keep the rest
        else    // otherwise, throw a runtime exception
            throw new RuntimeException("Expected: " + c + "; got: " + peek());
    }

    /**
     * returns and consumes the next character in the regular expression
     *
     * @return - the next character in the regular expression
     *
     */
    private char next() {
        char c = peek();    // see what's next, and save it
        eat(c); // get rid of whatever it was from regex
        return c;   // return whatever it was
    }

    /**
     * Returns true or false to determine whether or not there is more to
     * come in the regular expression
     *
     * @return - true or false, whether or not there is still more to go
     *
     */
    private boolean more() {
        return regex.length() > 0;  // is there more left?
    }

    /**
     * Function to concatenate two NFAs together in a user specified order.
     *
     * @param startNFA - the first NFA, to which we will concatenate the second
     * @param endNFA - the second NFA, which will be concatenated onto the first
     *
     * @return - the concatenated NFA
     *
     */
    private NFA concatNFA(NFA startNFA, NFA endNFA){
        NFA concatNFA;  // create a new NFA, in which we will store our data
        concatNFA = new NFA();  // initialize

        Set<State> states = endNFA.getFinalStates();    // get the final states from the endNFA

        // go through them, and add them all to our concatenation
        for(State s :  states) {
            concatNFA.addFinalState(s.getName());
        }

        concatNFA.addStartState(startNFA.getStartState().getName());    // get the start state from our first NFA, add it to our concat
        states = endNFA.getStates();    // get our list of states from endNFA

        // for every state in our list, add it to our concatenation
        for(State s : states) {
            concatNFA.addState(s.getName());
        }

        states = startNFA.getStates();  // get our list of states from startNFA

        // for every state in our list, add it to our concatenation
        for(State s : states) {
            concatNFA.addState(s.getName());
        }

        states = startNFA.getFinalStates(); // get our final states from startNFA

        // for every state in our list, add a transition between it, and the start state of endNFA, inside
        // of the concatenation (these start and end states are no longer start and end states here)
        for(State s : states) {
                concatNFA.addTransition(s.getName(), 'e', endNFA.getStartState().getName());
        }

        states = endNFA.getStates();    // get our list of states from endNFA

        // for every state in our list, get its transitions for every character in the alphabet,
        // and add them to our concatenation. Make sure to include the empty transition
        for(State s : states) {
            NFAState state = (NFAState)s;
            for(char c : endNFA.getABC()){
                Set<NFAState> toStates = endNFA.getToState(state,c);
                for(NFAState n : toStates) concatNFA.addTransition(s.getName(), c, n.getName());
            }

            // empty transition
            Set<NFAState> toStates = endNFA.getToState(state, 'e');
            for(NFAState n : toStates) concatNFA.addTransition(s.getName(), 'e', n.getName());

        }

        states = startNFA.getStates();  // get our list of states from startNFA

        // for every state in our list, get its transitions for every character in the alphabet,
        // and add them to our concatenation. Make sure to include the empty transitions
        for(State s : states) {
            NFAState state = (NFAState)s;
            for(char c : startNFA.getABC()){
                Set<NFAState> toStates = startNFA.getToState(state,c);
                for(NFAState n : toStates) concatNFA.addTransition(s.getName(), c, n.getName());
            }

            // empty transitions
            Set<NFAState> toStates = startNFA.getToState(state, 'e');
            for(NFAState n : toStates) concatNFA.addTransition(s.getName(), 'e', n.getName());
        }

        return concatNFA;
    }

    /**
     * Unions two NFAs into a single NFA, where order isn't particularly important.
     *
     * @param firstNFA - the first of the two NFAs to be unioned
     * @param secondNFA - the second of the two NFAs to be unioned
     *
     * @return - the unioned NFA
     *
     */
    private NFA unionNFA(NFA firstNFA, NFA secondNFA) {
        NFA unionNFA;   // create a new NFA, into which we will place our union
        unionNFA = new NFA();   // initialize
        Set<State> states = firstNFA.getFinalStates();  // get our set of final states from firstNFA

        // for every state in our list, add it as a final state in our union
        for(State s : states) unionNFA.addFinalState(s.getName());

        states = secondNFA.getFinalStates();    // get our set of final states from secondNFA

        // for every state in our list, add it as a final state in our union
        for(State s : states) unionNFA.addFinalState(s.getName());

        NFAState startState = new NFAState(stateNamer++ + "");  // create a new state to serve as our start state

        unionNFA.addStartState(startState.getName());   // add the new state as our union start state

        states = firstNFA.getStates();  // get the list of states from firstNFA

        // for each state in our list, add it to the union
        for(State s : states) unionNFA.addState(s.getName());

        states = secondNFA.getStates(); // get the list of states from secondNFA

        // for each state in our list, add it to the union
        for(State s : states) unionNFA.addState(s.getName());

        // add an empty transition from our new start state to both of our old start states
        unionNFA.addTransition(startState.getName(),'e', firstNFA.getStartState().getName());
        unionNFA.addTransition(startState.getName(),'e', secondNFA.getStartState().getName());

        states = firstNFA.getStates();  // get the list of states from firstNFA again

        // for every state in our list, find all transitions on each character, and add them to our
        // union. Don't forget to add the empty transitions
        for(State s : states) {
            NFAState state = (NFAState)s;
            for(char c : firstNFA.getABC()){
                Set<NFAState> toStates = firstNFA.getToState(state,c);
                for(NFAState n : toStates) unionNFA.addTransition(s.getName(), c, n.getName());
            }

            // adding the empty transitions
            Set<NFAState> toStates = firstNFA.getToState(state, 'e');
            for(NFAState n : toStates) unionNFA.addTransition(s.getName(), 'e', n.getName());
        }

        states = secondNFA.getStates(); // get the list of states from firstNFA again

        // for every state in our list, find all transitions on each character, and add them to our
        // union. Don't forget to add the empty transitions
        for(State s : states) {
            NFAState state = (NFAState)s;
            for(char c : secondNFA.getABC()){
                Set<NFAState> toStates = secondNFA.getToState(state,c);
                for(NFAState n : toStates) unionNFA.addTransition(s.getName(), c, n.getName());
            }

            // adding the empty transitions
            Set<NFAState> toStates = secondNFA.getToState(state, 'e');
            for(NFAState n : toStates) unionNFA.addTransition(s.getName(), 'e', n.getName());
        }

        return unionNFA;
    }
}