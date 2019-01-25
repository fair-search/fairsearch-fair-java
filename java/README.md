# Fair search core for Java

This is the Java library of the core algorithms used to do fair search. 

## Instalation

1. Clone this repository `git clone https://github.com/fair-search/fairsearch-core.git`
2. Change directory to the directory where you cloned the repository `cd WHERE_ITS_DOWNLOADED/fairsearch-core/java`
3. Use any IDE to work with the code

## Development

If you want to make your own builds you can do that with the Gradle wrapper:
- To make a JAR without the external dependencies: 
    `./gradlew clean jar`
- To make a JAR with all external dependencies included:
    `./gradlew clean farJar`

The output will go under `build/libs`.

# Using it in your code

Add the JAR file to the build path of your project and you are *set*. The main functionalities are exposed as static methods in the classes:
- `com.purbon.search.fair.Core`
- `com.purbon.search.fair.Simulator`

The library contains sufficient Java doc for each functions.

# Builds

- [JAR without dependencies](https://fair-search.github.io/fairsearch-core/java/fairsearch-core-0.1.jar)
- [JAR with dependncies](https://fair-search.github.io/fairsearch-core/java/fairsearch-core-all-0.1.jar)