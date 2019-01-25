package com.purbon.search.fair;

import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.LuceneTestCase;

public class CoreTests extends LuceneTestCase {

    public void testAdjustAlpha() {
        int n = 20;
        double p = 0.25;
        double alpha = 0.1;

        double res = Core.adjustAlpha(n, p, alpha);

        assertNotEquals(alpha, res);
    }

    public void testCreateMTables() {
        int n = 20;
        double p = 0.25;
        double alpha = 0.1;

        int[] adjustedMTable = Core.createAdjustedMTable(n, p, alpha);
        int[] unadjustedMTable = Core.createUnadjustedMTable(n, p, alpha);

        assertEquals(adjustedMTable.length, unadjustedMTable.length);

        boolean areSame = true;

        for(int i=0; i < adjustedMTable.length; i++) {
            if(adjustedMTable[i] != unadjustedMTable[i]) {
                areSame = false;
                break;
            }
        }

        assertEquals(false, areSame);
    }

    public void testComputeFailureProbability() {
        int n = 20;
        double p = 0.25;
        double alpha = 0.1;

        int[] mtable = Core.createUnadjustedMTable(n, p, alpha);

        double res = Core.computeFailureProbability(mtable, n, p, alpha);

        System.out.println(res);

        assertEquals(true, res > 0 && res < 1);
    }


    public void testIsFair() {
        int n = 20;
        double p = 0.25;
        double alpha = 0.1;

        TopDocs[] rankings = Simulator.generateRankings(1, n, p);

        assertEquals(1, rankings.length);

        assertEquals(true, Core.isFair(rankings[0], n, p, alpha));
    }
}
