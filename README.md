# Project: Web Crawler  
For the purposes of this project, we define the Internet as the test data in this document, and a web crawler as software that requests pages from the Internet, parses the content to extract all the links in the page, and visits the links to crawl those pages, to an infinite depth.

# Structural Notes
The project is built in eclipse and uses the gradle wrapper and JUnit 4 to automate compilation and running of tests.  It makes use of Java's Fork/Join framework for concurrent task management as well and Google's org.simple.json for JSON parsing.

# How to Run It
Discounting running through eclipse, there are two primary methods to run the project.

## Executing On a File Path
The gradle wrapper has a task which executes the main class in WebCrawler on a given file path.  The syntax is:
gradlew -Pmyargs='{path to file}' execute

## Running All Tests
Using the test task on the gradle wrapper runs both internet test cases and reports the crawler output followed by the expected outputs for each file.  The syntax is: gradlew test.  Note that to build and run the tests again you can run the cleanTest task.

# Design Choices
The crawler specified in the project description attemps to find a minimum spanning tree for the graph of valid sites described by each internet where the root of the tree is the first site in the json "pages" array.  To this end the Internet object restructures the data provided via JSON into a sort of presumed adjacency list which contains the real set of adjacent verticies as well as a set of non-existent edges.  It also tracks the first processed vertex as a root for the spanning tree.

The task of finding the spanning tree is handled using a concurrent breadth-first search.  Using the Fork/Join framework the crawler forks a number of threads necessary to process each address in the presumed adjacency list concurrently at each vertex it reaches. It's likely that for an internet of this simplicity this is actually slower than a standard BFS due to the overhead introduced by forking and joining threads; however, in a real world setting the efficiency bottleneck would be retriving page content from remote servers.  This justifies splitting off a child process for each link being visited so as to minimize this latency.

For the test cases, I made no assertions as the default methods of string comparison were inadequate for asserting the correctness of the acutal vs expected outputs.  Developing an effective comparison algorithm for testing was outside the scope of the project, but I considered two methods for doing so.  One would be to parse out the address strings in each list, then sort lexicographically and assert the lists are identical.  The second method involves building a histogram of the characters in each list and asserting that some meaningful subset of those characters is identical.
