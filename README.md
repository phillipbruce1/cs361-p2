# Project 2: Nondeterministic Finite Automata

Authors: Devyn Roth (Section 3) / Phillip Bruce (Section 2)

Class: CS361 Section 2, 3

Semester: Spring 2021

## Overview:

The purpose of this project was to model an instance of an NFA in a .txt form as well as compute an equivalent DFA from the given NFA. It tests the given input strings in the text file and prints the modeled NFA.

## Compiling and Using:

To compile the code run this command from the top directory
javac fa/nfa/NFADriver.java

To run the driver class run this command:
```
java fa.nfa.NFADriver ./tests/p2/path/to/test/file.txt
```

## Discussion:

When we started this project we were sailing pretty quickly as we modified the code from Project 1 and adjusted it to fit the rules of Project 2. Then we hit the horrifying monsters that were the getDFA and eClosure methods. While we were trying to code these two methods I will be honest there was a point in time when we considered dropping out and starting our own Etsy shop selling crocheted sweaters. We both don't know how to sew, but that wasn't going to stop us. 

Eventually though we managed to write out all the support methods we needed to finish the getDFA method, and we began to work through the process of converting the NFA to the DFA. 

We built a Queue to hold the states from the NFA, a Hashmap to hold the DFA transitions after conversion, and a Set to hold the converted DFA states. It took a while to figure out how to write the proper logic for the conversion, we both understood the idea conceptually, but it was the implementation that was the issue. However, we eventually got the whole concept written out.

Overall, this was an interesting and very difficult project to get finished. There were dark times when we both definitely both considered dropping out. But we both made it and we both can safely say we feel more comfortable with the idea of coding NFA's as well as coding the equivalent DFA.

## Testing:
To test our program, we ran the provided test cases. Once we got those to come back clear, we began trying to break our code with our own test cases. After we both exhausted every combination we could think of we called the project as finished.

[Link to original repo](https://github.com/devynroth/CS361-Project2)
