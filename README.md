# Fair search core for Java

[![image](https://api.travis-ci.org/fair-search/fairsearchcore-java.svg?branch=master)](https://travis-ci.org/fair-search/fairsearchcore-java)
[![image](https://img.shields.io/pypi/l/fairsearchcore.svg)](https://pypi.org/project/fairsearchcore/)

This is the Java library of the core algorithms used to do fair search. 

## Installation

You can import the library with maven in your `pom.xml` file:
```xml
<dependency>
  <groupId>com.github.fair-search</groupId>
  <artifactId>fairsearch-core</artifactId>
  <version>1.0.2</version>
</dependency>
```
or, if you are using Gradle, in your `build.gradle` file add this in the `dependencies` block:
```gradle
compile "com.github.fair-search:fairsearch-core:1.0.2"
```

And, that's it!

## Using it in your code

Add the JAR file to the build path of your project and you are *set*. The key methods are contained in the following classes:
- `com.github.fairsearch.Fair`
- `com.github.fairsearch.Simulator`

The library contains sufficient Java doc for each of the functions.

## Sample usage
Creating and analyzing mtables:
```java
package com.github.fairsearch.examples;

import com.github.fairsearch.Fair;
import com.github.fairsearch.Simulator;

public class HelloWorld {
    public static void main(String[] args) {
        // number of topK elements returned (value should be between 10 and 400)
        int k = 20; 
        // proportion of protected candidates in the topK elements (value shuld be between 0.02 and 0.98)
        double p = 0.25;  
        // significance level (value should be between 0.01 and 0.15)
        double alpha = 0.1; 
        
        //create the Fair object 
        Fair fiar = new Fair(k, p, alpha);
        
        //create an mtable using alpha unadjusted
        int[] unadjustedMTable = fair.createUnadjustedMTable();
        //unadjustedMTable -> [0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3]
        
        //analytically calculate the fail probability
        analytical = fair.computeFailureProbability(unadjustedMTable);
        //analytical -> 0.14688718869911077
        
        //create an mtable using alpha adjusted
        int[] adjustedMTable = fair.createAdjustedMTable();
        //adjustedMTable -> [0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2]
        
        //analytically calculate the fail probability
        analytical = fair.computeFailureProbability(adjustedMTable);
        //analytical -> 0.10515247355215218
    }
}
```
Generate random rankings and analyze them:
```java
//set number of rankings you want to generate
int M = 10000; 
        
//generate rankings using the simulator
TopDocs[] rankings = Simulator.generateRankings(M, k, p);

//experimentally calculate the fail probability
double experimental = Simulator.computeFailureProbability(mtable, rankings);
//experimental -> 0.1054
```
Let's get the alpha adjusted (used to create an adjusted mtable)
```java
//get alpha adjusted
double alphaAdjusted = fair.adjustAlpha();
//alphaAdjusted -> 0.07812500000000001
```
Apply a fair re-ranking to a given ranking:
```java
//import the FairScoreDoc class at the top
import com.github.fairsearch.FairScoreDoc;
//also, import Lucene's TopDocs class
import org.apache.lucene.search.TopDocs;

//let's manually create an unfair ranking (False -> unprotected, True -> protected)
//in this example the first document (docid=20) has a score of 20, the last document (docid=1) a score of 1
FairScoreDoc[] docs = {new FairScoreDoc(20, 20, false), new FairScoreDoc(19, 19, false),
                new FairScoreDoc(18, 18, false), new FairScoreDoc(17, 17, false),
                new FairScoreDoc(16, 16, false), new FairScoreDoc(15, 15, false),
                new FairScoreDoc(14, 14, false), new FairScoreDoc(13, 13, false),
                new FairScoreDoc(12, 12, false), new FairScoreDoc(11, 11, false),
                new FairScoreDoc(10, 10, false), new FairScoreDoc(9, 9, false),
                new FairScoreDoc(8, 8, false), new FairScoreDoc(7, 7, false),
                new FairScoreDoc(6, 6, false), new FairScoreDoc(5, 5, true),
                new FairScoreDoc(4, 4, true), new FairScoreDoc(3, 3, true),
                new FairScoreDoc(2, 2, true), new FairScoreDoc(1, 1, true)};
TopDocs unfairRanking = new TopDocs(docs.length, docs1, Float.NaN);

// let's check if the ranking is fair
boolean isFair = fair.isFair(unfairRanking);
// isFair -> false

//now re-rank the unfair ranking  
TopDocs reRanked = fair.reRank(unfairRanking);

// now let's see if the ranking is fair
boolean isFair = fair.isFair(reRanked);
// isFair -> true
```
*Note*: The numbers shown here may differ slightly from run to run as there is randomness factor involved.

## Development

1. Clone this repository `git clone https://github.com/fair-search/fairsearch-core.git`
2. Change directory to the directory where you cloned the repository `cd WHERE_ITS_DOWNLOADED/fairsearch-core/java`
3. Use any IDE to work with the code

If you want to make your own builds you can do that with the Gradle wrapper:
- To make a JAR without the external dependencies: 
```
./gradlew clean jar
```
- To make a JAR with all external dependencies included:
```
./gradlew clean farJar
```

The output will go under `build/libs`.

## Testing

Just run:
```
./gradlew clean check
```
*Note*: The simulator tests take a bit *longer* time to execute. Also, because there is a *randomness* factor involved in 
the tests, it can happen that (rarely) they fail sometimes.  

## Builds by us

- [JAR with dependncies](https://fair-search.github.io/fairsearch-core/java/fairsearch-core-all-1.0.1.jar)
- [JAR without dependencies](https://fair-search.github.io/fairsearch-core/java/fairsearch-core-1.0.1.jar)

## Credits

The FA*IR algorithm is described on this paper:

* Meike Zehlike, Francesco Bonchi, Carlos Castillo, Sara Hajian, Mohamed Megahed, Ricardo Baeza-Yates: "[FA*IR: A Fair Top-k Ranking Algorithm](https://doi.org/10.1145/3132847.3132938)". Proc. of the 2017 ACM on Conference on Information and Knowledge Management (CIKM).

This code was developed by [Ivan Kitanovski](http://ivankitanovski.com/) and [Tom Sühr](https://github.com/tsuehr) based on the paper. See the [license](https://github.com/fair-search/fairsearch-fair-java/blob/master/LICENSE) file for more information.

For any questions contact [Mieke Zehlike](https://de.linkedin.com/in/meike-zehlike-366bba131)

## See also

You can also see the [FA*IR plug-in for ElasticSearch](https://github.com/fair-search/fairsearch-fair-for-elasticsearch) 
and [FA*IR search core Python library](https://github.com/fair-search/fairsearch-fair-python).
