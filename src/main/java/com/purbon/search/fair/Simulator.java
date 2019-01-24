package com.purbon.search.fair;

import com.purbon.search.fair.utils.FairScoreDoc;
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
            Random random = new Random();
            FairScoreDoc[] docs = random.doubles().limit(n).boxed().map(x -> {
                return new FairScoreDoc((int)(x*n), (int)(x*n), x <= p);
            }).collect(Collectors.toList()).toArray(new FairScoreDoc[n]);
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
                .filter(x -> !Core.checkRankingMTable(x, mtable))
                .count() * 1.0 / rankings.length;
    }
}
