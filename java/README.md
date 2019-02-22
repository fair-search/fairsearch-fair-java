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

Add the JAR file to the build path of your project and you are *set*. The key methods are exposed throughs the following classes:
- `com.fairsearch.fair.Fair`
- `com.fairsearch.fair.Simulator`

The library contains sufficient Java doc for each of the functions.

## Sample usage
```
int k = 10; // number of topK elements returned (value should be between 10 and 400)
double p = 0.2; // proportion of protected candidates in the topK elements (value shuld be between 0.02 and 0.98) 
double alpha = 0.1; // significance level (value should be between 0.01 and 0.15)

//create the Fair object 
Fair fiar = new Fair(k, p, alpha);

//create an mtable using alpha adjusted
int[] mtable = fair.createAdjustedMTable();

//analytically calculate the fail probability
double analytical = fair.computeFailureProbability(mtable);
 
int M = 10000; // number of rankings you want to generate

//Generate rankings using the simulator
TopDocs[] rankings = Simulator.generateRankings(M, k, p);

//experimentally calculate the fail probability
double experimental = Simulator.computeFailureProbability(mtable, rankings);
```
 

# Builds

- [JAR with dependncies](https://fair-search.github.io/fairsearch-core/java/fairsearch-core-all-0.1.jar)
- [JAR without dependencies](https://fair-search.github.io/fairsearch-core/java/fairsearch-core-0.1.jar)
