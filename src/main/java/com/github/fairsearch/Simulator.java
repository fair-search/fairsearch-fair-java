package com.github.fairsearch;

import com.github.fairsearch.utils.FairScoreDoc;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.lucene.search.TopDocs;

import java.util.Arrays;

/**
 * This class serves as a wrapper for simulator functionalities
 */
public class Simulator {

    /**
     * Generates M rankings of n elements using Yang-Stoyanovich process
     * @param M     how many rankings to generate
     * @param k     how many elements should each ranking have
     * @param p     what is the probability that a candidate is protected
     * @return      the generated rankings
     */
    public static TopDocs[] generateRankings(int M, int k, double p) {
        TopDocs[] result = new TopDocs[M];
        MersenneTwister mt = new MersenneTwister();
        for(int i=0; i<M; i++) {
            FairScoreDoc[] docs = new FairScoreDoc[k];
            for(int j=0; j<k; j++) {
                docs[j] = new FairScoreDoc(k-j, k-j, mt.nextDouble() <= p);
            }
            result[i] = new TopDocs(docs.length, docs, Float.NaN);
        }
        return result;
    }

    /**
     * This computes experimentally how many of the M rankings fail to satisfy the MTable
     * @param mtable        an mtable to check against
     * @param rankings      rankings that are checked
     * @return              the ratio of failed rankings
     */
    public static double computeFailureProbability(int[] mtable, TopDocs[] rankings) {
        return  Arrays.stream(rankings)
                .filter(x -> !Fair.checkRankingMTable(x, mtable))
                .count() * 1.0 / rankings.length;
    }
}
