package re;

import fa.State;
import fa.nfa.NFA;
import fa.nfa.NFAState;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class RE implements REInterface {
//    private NFA nfa;
    private String regex;
    private Set<Character> sigma;
    private Set<NFAState> delta;
    private int stateNamer;
    private Set<String> transitions;
    //private enum validChars {A, B, OR, STAR, OPEN, CLOSE;}

    /**
     * Constructor for the Regular Expression object. Takes in a string
     * representing a regular expression, and initializes all class
     * objects.
     *
     * @param regex
     */
    public RE(String regex){
//        nfa = new NFA();    // Initialize our NFA
        this.regex = regex; // save the regular expression
        sigma = new HashSet<>();
        delta = new HashSet<>();
        stateNamer = 0;
        transitions = new LinkedHashSet<>();
    }

    @Override
    public NFA getNFA() {   // parse regex, turn it into NFA
        NFA regNfa;

        regNfa = regex();

        return regNfa;
    }

    /**
     *
     * @return
     */
    private NFA regex(){
        //NFA nfa = new NFA();

        NFA termNfa = parseTerm();

        if(more() && peek() == '|') {
            eat ('|');
            NFA subNfa = regex();
            NFA newNfa = new NFA();

            // TODO: take final states from termNfa, add to new NFA
            Set<State> states = termNfa.getFinalStates();
            for(State s : states) {
                newNfa.addFinalState(s.getName());
            }
            // TODO: take final states from subNfa, add to new NFA
            states = subNfa.getFinalStates();
            for(State s : states) {
                newNfa.addFinalState(s.getName());
            }
            // TODO: create a new Start State for the new NFA
            NFAState startState = new NFAState(stateNamer++ + "");
            newNfa.addStartState(startState.getName());
            // TODO: add all states from termNfa to new NFA
            states = termNfa.getStates();
            for(State s : states){
                newNfa.addState(s.getName());
            }
            // TODO: add all states from subNfa to new NFA
            states = subNfa.getStates();
            for(State s : states) {
                newNfa.addState(s.getName());
            }
            // TODO: empty transition start of New to start of TermNfa AND start of subNfa
            newNfa.addTransition(startState.getName(),'e', termNfa.getStartState().getName());
            newNfa.addTransition(startState.getName(),'e', subNfa.getStartState().getName());
            // TODO: add all transitions from termNfa to newNfa
            states = termNfa.getStates();
            for(State s : states) {
                NFAState state = (NFAState)s;
                for(char c : termNfa.getABC()){
                    Set<NFAState> toStates = termNfa.getToState(state,c);
                    for(NFAState n : toStates) {
                        newNfa.addTransition(s.getName(), c, n.getName());
                    }
                }
                Set<NFAState> toStates = termNfa.getToState(state, 'e');
                for(NFAState n : toStates) {
                    newNfa.addTransition(s.getName(), 'e', n.getName());
                }
            }

            // TODO: add all transitions from subNfa to newNfa
            states = subNfa.getStates();
            for(State s : states) {
                NFAState state = (NFAState)s;
                for(char c : subNfa.getABC()){
                    Set<NFAState> toStates = subNfa.getToState(state,c);
                    for(NFAState n : toStates) {
                        newNfa.addTransition(s.getName(), c, n.getName());
                    }
                }
                Set<NFAState> toStates = subNfa.getToState(state, 'e');
                for(NFAState n : toStates) {
                    newNfa.addTransition(s.getName(), 'e', n.getName());
                }
            }

            return newNfa;
            /*
            for(State s: termNfa.getFinalStates()){
                nfa.addFinalState(s.getName());
            }
            for(State s: subNfa.getFinalStates()) {
                nfa.addFinalState(s.getName());
            }
            nfa.addStartState(startState.getName());
            for(State s: termNfa.getStates()){
                nfa.addState(s.getName());

            }
            for(State s: subNfa.getStates()){
                nfa.addState(s.getName());
            }
            nfa.addTransition(startState.getName(),'e',termNfa.getStartState().getName());
            nfa.addTransition(startState.getName(),'e',subNfa.getStartState().getName());

            return nfa;
            */
          }

        return termNfa;
    }

    /**
     *
     * @return
     */
    private NFA parseTerm() {
        NFA factorNfa = null;
        while(more() && peek() != ')' && peek() != '|') {
            //NFA factNfa = parseFactor();
            if(factorNfa == null) {
                factorNfa = parseFactor();
            } else {    // need to concatenate
                NFA appendNfa = parseFactor(); // what we're adding on to factorNfa
                NFA joinedNfa; // where we're storing it all
                joinedNfa = concatNFA(factorNfa,appendNfa);

                factorNfa = joinedNfa;
            }
            // TODO: connect nextFactor with factorNfa via e's
            // put factorNFA together with this?
        }

        return factorNfa;
    }

    /**
     *
     * @return
     */
    private NFA parseFactor() {

        NFA baseNFA = parseBase();

        while (more() && peek() == '*') {
            eat('*');
            Set<State> stateSet = baseNFA.getFinalStates();
                for(State s: stateSet) {
                    baseNFA.addTransition(s.getName(), 'e', baseNFA.getStartState().getName());
//                    transitions.add(s.getName()+'e'+baseNFA.getStartState().getName());
                    //nfa.addTransition(s.getName(), 'e', baseNFA.getStartState().getName());
                }
                NFAState newStart = new NFAState(stateNamer++ + "");
                baseNFA.addState(newStart.getName());
                baseNFA.addTransition(newStart.getName(),'e',baseNFA.getStartState().getName());
                baseNFA.addStartState(newStart.getName());
                baseNFA.addFinalState(newStart.getName());
        }
        return baseNFA;
    }

    /**
     * Parses through a base to determine what transitions we're going to
     * make. Also sends individual characters to parseChar, to ensure that
     * they are not inadvertently forgotten in our alphabet.
     *
     * @return nfa - the nfa to be returned
     */
    private NFA parseBase() {
        NFA baseNfa = new NFA();

        switch(peek()) {
            case '(':   // we have a regex, not a char
                eat('(');
                NFA rNfa = regex();
                eat(')');
                baseNfa = rNfa;
                break;

//            case '*':
//                System.out.println("You hit a " + peek());
//                break;

            default:
                // TODO: create an NFA for this symbol
                NFAState fromState = new NFAState(stateNamer++ + "");
                NFAState toState = new NFAState(stateNamer++ + "");

                baseNfa.addFinalState(toState.getName());
                //nfa.addFinalState(toState.getName());
                baseNfa.addStartState(fromState.getName());
                //nfa.addState(fromState.getName());
                char c = next();
                baseNfa.addTransition(fromState.getName(), c, toState.getName());
                //nfa.addTransition(fromState.getName(), c, toState.getName());

                //return nfa;
        }

        return baseNfa;
    }

    /**
     * Parses an individual character, i.e., it adds the character to
     * our alphabet sigma, assuming that the character is not 'e'. Ensures
     * that our alphabet is no larger than it needs to be (i.e., doesn't
     * contain 'a' if the language only accepts strings of 'b', and vice-versa)
     *
     * @param letter - the character to be added to the alphabet
     */
    private void parseChar(Character letter) {
        if(letter != 'e') {
            sigma.add(letter);
        }
    } // maybe not necessary...

    /**
     * Private method to peek ahead in the regular expression, to see what's
     * coming next
     *
     * @return - the next character in the regular expression
     */
    private char peek() {
        return regex.charAt(0);
    }

    /**
     * consume the next character in the regular expression, if the next
     * character is the character passed in. If it is not, an exception
     * will be thrown to alert the user
     *
     * @param c - the character to be consumed, if it is the next character in regex
     */
    private void eat(char c) {
        if(peek() == c)
            regex = regex.substring(1);
        else
            throw new RuntimeException("Expected: " + c + "; got: " + peek());
    }

    /**
     * returns and consumes the next character in the regular expression
     *
     * @return - the next character in the regular expression
     */
    private char next() {
        char c = peek();
        eat(c);
        return c;
    }

    /**
     * Returns true or false to determine whether or not there is more to
     * come in the regular expression
     *
     * @return - true or false, whether or not there is still more to go
     */
    private boolean more() {
        return regex.length() > 0;
    }

    private NFA concatNFA(NFA startNFA, NFA endNFA){
        System.out.println("SIGMA IS THIS!! " + sigma.toString());
        NFA concatNFA = new NFA();
        // TODO: find final states from endNFA, add to concatNFA
        Set<State> states = endNFA.getFinalStates();
        for(State s :  states) {
//            NFAState state = (NFAState)s;
            concatNFA.addFinalState(s.getName());
        }
        // TODO: find start state from startNFA, add to concatNFA
        concatNFA.addStartState(startNFA.getStartState().getName());
        // TODO: find rest states from endNFA, add to concatNFA
        states = endNFA.getStates();
        for(State s : states) {
            concatNFA.addState(s.getName());
        }
        // TODO: find rest states from startNFA, add to concatNFA
        states = startNFA.getStates();
        for(State s : states) {
            concatNFA.addState(s.getName());
        }
        // TODO: add empty transitions from final states of startNFA to start state of endNFA
        states = startNFA.getFinalStates();

        for(State s : states) {
            NFAState state = (NFAState)s;
//            if(!s.getName().equals(concatNFA.getStartState().getName())) {
                concatNFA.addTransition(s.getName(), 'e', endNFA.getStartState().getName());
//            }
        }
        // TODO: find all transitions from endNFA, add to concatNFA
        states = endNFA.getStates();
        for(State s : states) {
            NFAState state = (NFAState)s;
            for(char c : endNFA.getABC()){
                Set<NFAState> toStates = endNFA.getToState(state,c);
                for(NFAState n : toStates) {
                    concatNFA.addTransition(s.getName(), c, n.getName());
                }
            }
            Set<NFAState> toStates = endNFA.getToState(state, 'e');
            for(NFAState n : toStates) {
                concatNFA.addTransition(s.getName(), 'e', n.getName());
            }
        }
        // TODO: find all transitions from startNFA, add to concatNFA
        states = startNFA.getStates();
        for(State s : states) {
            NFAState state = (NFAState)s;
            for(char c : startNFA.getABC()){
                Set<NFAState> toStates = startNFA.getToState(state,c);
                for(NFAState n : toStates) {
                    concatNFA.addTransition(s.getName(), c, n.getName());
                }
            }
            Set<NFAState> toStates = startNFA.getToState(state, 'e');
            for(NFAState n : toStates) {
                concatNFA.addTransition(s.getName(), 'e', n.getName());
            }
        }


        return concatNFA;
    }
}
