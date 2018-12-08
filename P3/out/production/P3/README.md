# Project 1 Part 3: Regular Expressions

* Authors: James Souder and Jason Egbert
* Class: CS361 Section 001
* Semester: Fall 2018

## Overview
This program reads in strings from an input file, representing regular expressions, and converts them to NFAs so that further input strings can be tested to determine whether or not the string is in the language of the provided regular expression. The input files take the form of a regular expression on the first line, followed by a series of strings over the alphabet of the language (one string per line) which are to be tested.

This project utilizes a jar file library to provide NFA, DFA, NFAState, DFAState, State classes, and others to enable RE to create an NFA. It utilizes packages to group the files together, and enable the system to run cohesively.

## Compiling and Using
In order to compile and use this program, you must first make certain that all necessary files are present. The required .java files are as follows:
	
	- REDriver.java - the driver class (can be replaced with a different driver, if desired)
	- RE.java - the file which will parse the regular expression into an NFA
	- CS361FA.jar	- the FA library
	- README.md - this file. Not necessary to run, only to understand.

Once the presence of these files is confirmed, the project should be compiled from the Onyx command line as follows:
```
$ javac REDriver.java
```
Once compiled, the program can be run as follows:
```
$ java REDriver [testFile.txt]
```
where testFile.txt is the file to be tested. The file MUST take the following format:
```
regular_expression
testString
testString
...
```
where regular expression is the regular expression to be tested, containing only alphabet symbols, '|' to represent or, '' to represent the star operation, and '(' and ')' to group characters together.
the testStrings must contain only letters in the alphabet of the regular expression, or the DFA class will be unable to parse through the string. There can be as many input strings as the user desires.

Once the program has run on the desired input file, output will be produced to the screen which will be a series of 'yes' or 'no' strings printed one line at a time, and corresponding to whether or not the string on the equivalent line is part of the language of the regular expression.

## Discussion


## Testing
In order to test this project, we initially utilzed the test files provided with the source code materials. We built the program to parse through a regular expression and build an NFA, utilizing the RE's in the test files, and worked to get the program to correctly build the corresponding NFAs and correctly read the provided strings to correctly determine which strings are and are not in the language.

Before we managed to get our program to complete funcionality, much of our testing was done with the debugging tool in IntelliJ. The tool allowed us to set up break points just before areas of suspected bugs and step through the code to determine whether or not there was actually a bug there, and if not to further narrow down where it might be. We found and fixed most of our bugs in this method. 

Once our code was successfully producing correct output for the provided test files, we created a number of additional test files for other regular expressions we had reviewed in class, and created custom strings to test against our generated NFAs. We tested regular expressions of various complexities, and even included non 'a' or 'b' characters, receiving correct ouput results for each of the test files. Once each of the test files was successfully passing the tests, we cleaned up our code, and continued to add more test cases and try to strain the capacities of our program. 

## Sources Used
The following sources were used to construct this program:
- http://matt.might.net/articles/parsing-regex-with-recursive-descent/
- CS361 class textbook
----------
