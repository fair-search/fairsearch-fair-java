package com.purbon.search.fair;

import com.purbon.search.fair.lib.MTableGenerator;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.LuceneTestCase;

import java.util.Arrays;

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
}
