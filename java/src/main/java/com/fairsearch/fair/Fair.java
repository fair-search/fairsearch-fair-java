package com.fairsearch.fair;

import com.fairsearch.fair.lib.FailprobabilityCalculator;
import com.fairsearch.fair.lib.MTableFailProbPair;
import com.fairsearch.fair.lib.MTableGenerator;
import com.fairsearch.fair.lib.RecursiveNumericFailprobabilityCalculator;
import com.fairsearch.fair.utils.FairScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 * This class serves as a wrapper around the utilities we have created for FA*IR ranking
 */
public class Fair {

    /**
     * Creates an mtable using alpha unadjusted.
     * @param n           Total number of elements
     * @param p           The proportion of protected candidates in the top-k ranking
     * @param alpha       The significance level
     * @return            The generated mtable, which is a int[]
     */
    public static int[] createUnadjustedMTable(int n, double p, double alpha) {
        MTableGenerator generator = new MTableGenerator(n, p, alpha, false);
        return generator.getMTable();
    }

    /**
     * Creates an mtable using alpha adjusted.
     * @param n           Total number of elements
     * @param p           The proportion of protected candidates in the top-k ranking
     * @param alpha       The significance level
     * @return            The generated mtable, which is a int[]
     */
    public static int[] createAdjustedMTable(int n, double p, double alpha) {
        MTableGenerator generator = new MTableGenerator(n, p, alpha, true);
        return generator.getMTable();
    }

    /**
     * Computes the alpha adjusted for the given set of parameters
     * @param n           Total number of elements
     * @param p           The proportion of protected candidates in the top-k ranking
     * @param alpha       The significance level
     * @return            The adjusted alpha
     */
    public static double adjustAlpha(int n, double p, double alpha) {
        RecursiveNumericFailprobabilityCalculator adjuster = new RecursiveNumericFailprobabilityCalculator(n, p, alpha);
        MTableFailProbPair failProbPair = adjuster.adjustAlpha();
        return failProbPair.getAlpha();
    }

    /**
     * Computes analytically the probability that a ranking created with the simulator will fail to pass the mtable
     * @param n           Total number of elements
     * @param p           The proportion of protected candidates in the top-k ranking
     * @param alpha       The significance level
     * @return            The adjusted alpha
     */
    public static double computeFailureProbability(int n, double p, double alpha) {
        FailprobabilityCalculator calculator = new RecursiveNumericFailprobabilityCalculator(n, p, alpha);
        return calculator.calculateFailprobability(n, p, alpha);
    }

    /**
     * Checks if the ranking is fair for the given parameters
     * @param docs        The ranking to be checked
     * @param mtable      The mtable against to check
     * @return            Returns whether the rankings statisfies the mtable
     */
    public static boolean checkRankingMTable(TopDocs docs, int[] mtable) {
        int countProtected = 0;

        //if the mtable has more elements than there are in the top docs return false
        if(docs.scoreDocs.length + 1 < mtable.length)
            return false;

        //check number of protected element at each ranking
        for(int i=0; i < docs.scoreDocs.length; i++) {
            countProtected += ((FairScoreDoc)docs.scoreDocs[i]).isProtected ? 1 : 0;
            if(countProtected < mtable[i+1])
                return false;
        }
        return true;
    }

    /**
     * Checks if the ranking is fair for the given parameters
     * @param docs        The ranking to be checked
     * @param n           Total number of elements
     * @param p           The proportion of protected candidates in the top-k ranking
     * @param alpha       The significance level
     * @return            Returns a boolean which specifies whether the ranking is fair
     */
    public static boolean isFair(TopDocs docs, int n, double p, double alpha) {
        MTableGenerator generator = new MTableGenerator(n, p, alpha, true);
        return checkRankingMTable(docs, generator.getMTable());
    }

    /**
     * Main method doing not much
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(System.currentTimeMillis());
    }
}
