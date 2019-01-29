package com.fairsearch.fair;

import com.fairsearch.fair.utils.FairScoreDoc;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.lucene.search.TopDocs;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * This class serves as a wrapper for simulator functionalities
 */
public class Simulator {

    /**
     * Generates M rankings of n elements using Yang-Stoyanovich process
     * @param M     how many rankings to generate
     * @param n     how many elements should each ranking have
     * @param p     what is the probabilty
     * @return      the generated rankings
     */
    public static TopDocs[] generateRankings(int M, int n, double p) {
        TopDocs[] result = new TopDocs[M];
        for(int i=0;i<M; i++) {
            MersenneTwister mt = new MersenneTwister();
            FairScoreDoc[] docs = new FairScoreDoc[n];
            for(int j=0; j<n; j++) {
                docs[j] = new FairScoreDoc(n-j, n-j, mt.nextDouble() <= p);
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
