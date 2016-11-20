# Project: Web Crawler  
For the purposes of this project, we define the Internet as the test data in this document, and a web crawler as software that requests pages from the Internet, parses the content to extract all the links in the page, and visits the links to crawl those pages, to an infinite depth.

# Structural Notes
The project is built in eclipse and uses the gradle wrapper to automate compilation and running of tests.  It makes use of Java's Fork/Join framework for concurrent task management as well and Google's org.simple.json for JSON parsing.

# How to Run It
Discounting running through eclipse, there are two primary methods to run the project.

## Executing On a File Path
The gradle wrapper has a task which executes the main class in WebCrawler on a given file path.  The syntax is:
gradlew -Pmyargs='{path to file}' execute

## Running All Tests
Using the test task on the gradle wrapper runs both internet test cases and reports the crawler output followed by the expected outputs for each file.  The syntax is: gradlew test.  Note that to build and run the tests again you can run the cleanTest task.

# Design Choices
