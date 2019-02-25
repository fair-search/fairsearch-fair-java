package com.fairsearch.fair;

import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.LuceneTestCase;

import java.io.FileNotFoundException;

public class SimulatorTests extends LuceneTestCase {

    public void testGenerateRankings() {
        int M = 5;
        int k = 20;
        double p = 0.25;

        TopDocs[] rankings = Simulator.generateRankings(M, k, p);

        assertEquals(M, rankings.length);

        for(int i=0; i < M; i++) {
            assertEquals(k, rankings[i].scoreDocs.length);
        }
    }

    public void testComputeFailureProbability() {
        int M = 10000 ;
        int k = 20;
        double p = 0.25;
        double alpha = 0.1;
        Fair fair = new Fair(k, p, alpha);

        TopDocs[] rankings = Simulator.generateRankings(M, k, p);

        int[] mtable = fair.createAdjustedMTable();

        double ratio = Simulator.computeFailureProbability(mtable, rankings);

        System.out.println(ratio);

        assertTrue(ratio >= 0 && ratio <= 0.5);
    }


    public void testFailProbabilityCalculatorAnalyticallyVSExperimental() throws FileNotFoundException {
        int[] Ms = {5000, 10000};
        int[] ks = {10, 20, 50, 100, 200};
        double[] ps = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
        double[] alphas = {0.01, 0.05, 0.1, 0.15};

        double allowedOffset = 0.02; // we tolerate an absolute difference in probability of 0.02

        for(int M: Ms) {
            for(int k: ks) {
                for(double p : ps) {
                    for(double alpha : alphas) {
                        Fair fair = new Fair(k, p, alpha);
                        TopDocs[] rankings = Simulator.generateRankings(M, k, p);
                        int[] mtable = fair.createAdjustedMTable();
                        double experimental = Simulator.computeFailureProbability(mtable, rankings);
                        double analytical = fair.computeFailureProbability(mtable);
                        double actualOffset = Math.abs(analytical - experimental);
                        assertTrue(actualOffset <= allowedOffset);
                    }
                }
            }
        }
    }
}
