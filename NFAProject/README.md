# Project 1: Deterministic Finite Automata

* Author: James Souder, Jason Egbert
* Class: CS361 Section 001
* Semester: Fall 2018

## Overview

This program simulates a non-deterministic finite automata (NFA), using modules
and classes in Java to organize the states, transitions, and 5-tuple, and then
by converting the NFA to a DFA (Deterministic Finite Automata) using the rules
of theorem 1.39 from the class text, *Introduction to the Theory of Computation*.
The generated DFA is then capable of determining whether or not a string is
accepted or rejected by the specified language. The driver class, NFADriver.java,
reads 5 or more lines from a .txt file provided by the user as a command line 
argument. The driver parses the file, and passes the data to NFA.java, where it
is used to construct the initial simulated NFA to test the final lines of the 
.txt files, which contain the test string.

## Compiling and Using

In order to compile this program, run the following command from within the top
directory:

```
$javac ./fa/dfa/NFADriver.java
```
In order to run this program, enter the following command on the command line:

```
$java fa.nfa.NFADriver [TestFile]
```
Where TestFile is the location/name of the input file to be tested. The program
will parse the input file, and return the results to the console for user
examination.

```
Line 1: Names of final states separated by whitespace.
Line 2: Name of the start state, no more than one state.
Line 3: Names of states not in the previous two lines, separated by whitespace.
Line 4: Three character substrings representing transitions separated by white space.
Line(s) 5+: The strings (one per line) to be processed by the NFA to determine if it 
	    is in the language.
```
Formatting for Line 4 is as follows: 
```
	[1][2][3] 
	
	where:
	    Character [1]: 'from' state
	    Character [2]: symbol read
	    Character [3]: 'to' state
```

The input strings provided in lines 5+ must use the valid characters of the language,
and the empty string is represented with the character 'e'. The character'e' can 
also be used as the symbol read to indicate a free transition on line 4 as character
[2].

## Discussion

This project was significantly more difficult, for one reason or another, than the 
previous project. The initial parsing of the file was relatively straight forward, as it
was not terribly different from the building of a DFA from the file. The real challenge
came in creating the DFA from our constructed NFA.

The first issue we encountered with this process was figuring out the eClosure of any
given state. We were eventually able to figure out how to parse through that data and
determine which states we could go to for free, and then the states that we could go
to from those states for free. This was one of our more easy to solve problems.

The second issue we encountered, and possibly the most challenging, was figuring out 
where any given state could go given a specified character in our sigma symbol table.
Initially we believed that this was a four part process. We believed that we first had
to find the eClosure of the from state, then the states we could get to from our state
by consuming the symbol, then we believed we needed the eClosure of each state in our
symbol transition set, and then we needed to search each state from our first eClosure
to see where we could go on the symbol. We struggled with this concept for hours, 
thinking that we were on the right track, until we eventually decided to call it a night
and come back with clear heads after some sleep.

The next day we both, independently, arrived at the conclusion that we had overcomplicated
the issue. We didn't need  to find so many things, and we definitely didn't need to add
all of them to the set that we were returning. We concluded that we needed to find the 
eClosure of the state, but not add it to our return set. We then needed to take the states
in our eClosure, and see where we could go on the symbol for each of those states, and add
those states to our return set. Then we needed to go through the states in our return set
and determine the eClosure of each of those states and add that to our return state. Once
we had this revelation the code to fix our issue was relatively simple to write.

Our next issue was a little bit more tricky to solve. Our code seemed to do what it was 
supposed to do, but we weren't getting all of the proper states in our DFA state set. 
After some time debugging (all hail IntelliJ's debugging tools!), we were able to determine
that our problem came down to the fact that we weren't sorting our list of NFAStates in
a natural order before  we were creating DFAStates out of their names. We relatively quickly
applied a fix for that issue, and found that we wer now returning the correct information.

Once we had confirmed that we were getting the right output for all of our test 
scenarios, we felt confident in our solution to the NFA problem.

## Testing

We tested our projects as we went and included some further testing that we came up with,
in addition to the tests provided by the instructors for the purpose of this project. We
also added, and later removed, additional test strings to the provided files, to try and 
make sure we were getting all of the nuances of the languages we were testing. We are 
comfortable with our level of testing and we went through each test string by hand on the 
board to compare each step with our program execution.

## Extra Credit

No opportunity for extra credit provided.

## Sources Used

- Java Collections API Documentation: <https://docs.oracle.com/javase/8/docs/technotes/guides/collections/reference.html>
