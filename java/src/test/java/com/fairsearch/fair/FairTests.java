package com.fairsearch.fair;

import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.LuceneTestCase;

public class FairTests extends LuceneTestCase {

    public void testAdjustAlpha() {
        int k = 20;
        double p = 0.25;
        double alpha = 0.1;
        Fair fair = new Fair(k, p, alpha);

        double res = fair.adjustAlpha();

        assertNotEquals(alpha, res);
    }

    public void testCreateMTables() {
        int k = 20;
        double p = 0.25;
        double alpha = 0.1;
        Fair fair = new Fair(k, p, alpha);

        int[] adjustedMTable = fair.createAdjustedMTable();
        int[] unadjustedMTable = fair.createUnadjustedMTable();

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
        Fair fair = new Fair(n, p, alpha);

        int[] adjustedMTable = fair.createAdjustedMTable();

        double res = fair.computeFailureProbability(adjustedMTable);

        assertEquals(true, res > 0 && res < 1);
    }


    public void testIsFair() {
        int k = 20;
        double p = 0.25;
        double alpha = 0.1;
        Fair fair = new Fair(k, p, alpha);

        TopDocs[] rankings = Simulator.generateRankings(1, k, p);

        assertEquals(1, rankings.length);

        assertEquals(true, fair.isFair(rankings[0]));
    }
}
