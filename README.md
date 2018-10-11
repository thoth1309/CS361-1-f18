# Project 1: Deterministic Finite Automata

* Author: James Souder, Jason Egbert
* Class: CS361 Section 001
* Semester: Fall 2018

## Overview

This program simulates a deterministic finite automata (DFA), using modules and 
classes in Java to organize the states, transitions, and 5-tuple. The driver class,
DFADriver.java, reads 5 or more lines from a .txt file provided by the user as a 
command line argument. The driver parses the file, and passes the data to DFA.java
where it is used to construct a simulated DFA to test the final lines of the .txt
file.

## Compiling and Using

In order to compile this program, run the following command from within the top
directory:

```
$javac ./fa/dfa/DFADriver.java
```
In order to run this program, enter the following command on the command line:

```
$java fa.dfa.DFADriver [TestFile]
```
Where TestFile is the location/name of the input file to be tested. The program
will parse the input file, and return the results to the console for user
examination.

```
Line 1: Names of final states separated by whitespace.
Line 2: Name of the start state.
Line 3: Names of states not in the previous two lines, separated by whitespace.
Line 4: Three character substrings representing transitions separated by white space.
ine(s) 5+: The strings (one per line) to be processed by the DFA to determine if it 
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
and the empty string is represented with the character 'e'.

## Discussion

In general, our project went very smoothly and required about 4 hours of our time
from start to finish. We decided to work together in person using a pair programming 
style. Our approach was to familiarize ourselves with the project desription, and then 
work out a solution on the board while determining which data structures we would have 
to use. During this time, we read documentation on the data structure to help us make 
our decisions and make sure we understood their usage, and how they applied to our 
project.

Having worked out a solution, our project was fairly straightforward to code. The only
problem we ran into was trying to make sure we were using the same object that
we originally instantiated throughout our code. We were able to accomplish this
by using a for each loop over our LinkedHashSet to find and access our states.

We felt prepared from the lectures to understand and tackle this programming project. 
Also, the project served to reinforce many of the concepts discussed in the lectures.

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

- Java Collections API Documentation: (https://docs.oracle.com/javase/8/docs/technotes/guides/collections/reference.html)[https://docs.oracle.com/javase/8/docs/technotes/guides/collections/reference.html]
