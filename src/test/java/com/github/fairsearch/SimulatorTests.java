package com.github.fairsearch;

import com.github.fairsearch.utils.FairScoreDoc;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

@RunWith(JUnitParamsRunner.class)
public class SimulatorTests {

    private static final double OFFSET = 0.02; // result tolerance for SimulatorTests

    @Test
    @Parameters({"100, 10, 0.25",
            "1000, 10, 0.2",
            "5000, 30, 0.5",
            "10000, 20, 0.3"})
    public void testGenerateRankings(int M, int k, double p) {
        //generate the rankings
        TopDocs[] rankings = Simulator.generateRankings(M, k, p);

        //check if the size is right
        assertEquals(M, rankings.length);

        //check if the number of protected elements is right
        int total = M * k;
        long numberOfProtected = Arrays.stream(rankings).map(x -> x.scoreDocs).flatMap(x -> Arrays.stream(x))
                .filter(x -> ((FairScoreDoc)x).isProtected).count();
        assertEquals(p, numberOfProtected * 1.0 / total, OFFSET);
    }

    @Test
    public void testFailProbabilityCalculatorAnalyticallyVSExperimental() {
        int[] Ms = {5000, 10000};
        int[] ks = {10, 20, 50, 100, 200};
        double[] ps = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
        double[] alphas = {0.01, 0.05, 0.1, 0.15};

        for(int M: Ms) {
            for(int k: ks) {
                for(double p : ps) {
                    for(double alpha : alphas) {
                        Fair fair = new Fair(k, p, alpha);
                        TopDocs[] rankings = Simulator.generateRankings(M, k, p);
                        int[] mtable = fair.createAdjustedMTable();
                        double experimental = Simulator.computeFailureProbability(mtable, rankings);
                        double analytical = fair.computeFailureProbability(mtable);
                        assertEquals(experimental, analytical, OFFSET);
                    }
                }
            }
        }
    }
}
