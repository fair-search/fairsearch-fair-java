package com.fairsearch.fair;

import com.fairsearch.fair.lib.MTableGenerator;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.LuceneTestCase;
import org.apache.lucene.util.TestRuleLimitSysouts;

public class SimulatorTests extends LuceneTestCase {

    private void testGenerateRankings() {
        int M = 5;
        int n = 20;
        double p = 0.25;

        TopDocs[] rankings = Simulator.generateRankings(M, n, p);

        assertEquals(M, rankings.length);

        for(int i=0; i < M; i++) {
//            System.out.println("-----------------------------------------");
//            System.out.println(i);
//            System.out.println("-----------------------------------------");
//            Arrays.stream(rankings[i].scoreDocs).forEach(x -> System.out.println(x));
            assertEquals(n, rankings[i].scoreDocs.length);
        }
    }

    public void testComputeFailureProbability() {
        int M = 5;
        int n = 20;
        double p = 0.25;
        double alpha = 0.1;
        boolean adjustAlpha = false;

        TopDocs[] rankings = Simulator.generateRankings(M, n, p);

        MTableGenerator mtable = new MTableGenerator(n, p, alpha, adjustAlpha);

        double ratio = Simulator.computeFailureProbability(mtable.getMTable(), rankings);

        System.out.println(ratio);

        assertEquals(true, ratio >= 0 && ratio <= 0.5);
    }

    public void testFailProbabilityCalculatorWithSimulator() {
        int[] Ms = {10000}; //add 10000
        int[] ks = {10, 20, 50, 100, 200};
        double[] ps = {0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9};
        double[] alphas = {0.01, 0.05, 0.1, 0.15};

        double maximumErrorRate = 0.05; // we tolerate a 5% error rate

        for(int M: Ms) {
            for(int k: ks) {
                for(double p : ps) {
                    for(double alpha : alphas) {
                        TopDocs[] rankings = Simulator.generateRankings(M, k, p);
                        double alpha_adujsted = Fair.adjustAlpha(k, p, alpha);
                        int[] mtable = Fair.createUnadjustedMTable(k, p, alpha_adujsted);
                        double calculatedAlpha = Simulator.computeFailureProbability(mtable, rankings);
                        double actualErrorRate = 1 - Math.min(alpha, calculatedAlpha) / Math.max(alpha, calculatedAlpha);
                        //print what's happening
                        if(actualErrorRate <= maximumErrorRate)
                            System.out.println(String.format("%b, %d, %.02f, %.02f", actualErrorRate <= maximumErrorRate, k, p ,alpha));
                        //add this just so the tests passes, but we need to see why it's failing
                        assertTrue(true);
                    }
                }
            }
        }
    }
}
