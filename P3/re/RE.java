package re;

import fa.nfa.NFA;
import fa.nfa.NFAState;

import java.util.HashSet;
import java.util.Set;

public class RE implements REInterface {
    private NFA nfa;
    private String regex;
    private Set<Character> sigma;
    private Set<NFAState> delta;
    private int stateNamer;
    //private enum validChars {A, B, OR, STAR, OPEN, CLOSE;}

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
        sigma = new HashSet<>();
        delta = new HashSet<>();
        stateNamer = 0;
    }

    @Override
    public NFA getNFA() {   // parse regex, turn it into NFA
        //NFA nfa = new NFA();
        NFAState start = new NFAState(stateNamer++ + "");
        delta.add(start);

        // TODO: Pseudocode for what we're actually going to be doing here
        int count = 0;
        int i = 0;
        StringBuilder subString = new StringBuilder();

        parseBase(start, regex);

        /*
        GRAMMAR FOR REGEX
        <regex>  ::= <term> '|' <regex>  |  <term>

        <term>   ::= { <factor> }

        <factor> ::= <base> { '*' }

        <base>   ::= <char>  |  '(' <regex> ')'

        <char>   ::= 'a'  |  'b'  |  'e'
         */
//        if(regex.charAt(i) == '('){
//            i++;
//            while(regex.charAt(i) != ')' && count == 0){    // check this... maybe need to rearrange this to find what you're looking for
//                subString.append(regex.charAt(i));
//                if(regex.charAt(i) == '(')
//                    count++;
//                if(regex.charAt(i) == ')' && count > 0)
//                    count--;
//                i++;
//            }
//        }

        // TODO: create a recursive descent (but not a parse tree)
        // TODO: that will allow you to find your states and transitions
        // TODO: you don't so much care about how you get to the states,
        // TODO: just THAT you get to them, and recognize them correctly.

        // TODO: start with this line

        return null;
    }

    private void regex(String regex){}
    private void parseTerm(NFA nfa, String term) {}
    private void parseFactor(NFA nfa, String Factor) {}

    /**
     * Parses through a base to determine what transitions we're going to
     * make. Also sends individual characters to parseChar, to ensure that
     * they are not inadvertently forgotten in our alphabet.
     *
     * @param fromState - the state we're moving from
     * @param base - the string which we will parse to determine our transitions
     */
    private void parseBase(NFAState fromState, String base) {
        if(base.length() == 1){
            parseChar(base.charAt(0));
            NFAState toState = new NFAState(stateNamer++ + "");
            delta.add(toState);
            fromState.addTransition(base.charAt(0), toState);
        } else if (base.charAt(0) == '(') {
            regex(base);
        } else {
            if(base.charAt(1) == '*') {
                parseChar(base.charAt(0));
                fromState.addTransition(base.charAt(0), fromState);
                if(base.length() > 2){
                    if(base.charAt(2) == '|') {
                       parseBase(fromState, base.substring(3));
                    } else {
                        parseBase(fromState, base.substring(2));
                    }
                }
            } else if (base.charAt(1) == '|') {
                parseChar(base.charAt(0));
                NFAState toState = new NFAState(stateNamer++ + "");
                delta.add(toState);
                fromState.addTransition(base.charAt(0), toState);
                parseBase(fromState, base.substring(2));
            } else if (base.charAt(1) == 'a' || base.charAt(1) == 'b' || base.charAt(1) == 'e') {
                parseBase(fromState, base.substring(1));
            }
        }
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
}
