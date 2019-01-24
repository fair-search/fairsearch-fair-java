package com.purbon.search.fair;

import com.purbon.search.fair.lib.FailprobabilityCalculator;
import com.purbon.search.fair.lib.MTableGenerator;
import com.purbon.search.fair.lib.RecursiveNumericFailprobabilityCalculator;
import com.purbon.search.fair.utils.FairScoreDoc;
import org.apache.lucene.search.TopDocs;

/**
 * This class serves as a wrapper around the utilities we have created for FA*IR ranking
 */
public class Core {

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
        MTableGenerator generator = new MTableGenerator(n, p, alpha, true);
        return generator.getAdjustedAlpha();
    }

    /**
     * Computes analytically the probability that a ranking created with the simulator will fail to pass the mtable
     * @param mtable      The mtable to check against
     * @param n           Total number of elements
     * @param p           The proportion of protected candidates in the top-k ranking
     * @param alpha       The significance level
     * @return            The adjusted alpha
     */
    public static double computeFailureProbability(int[] mtable, int n, double p, double alpha) {
        FailprobabilityCalculator calculator = new RecursiveNumericFailprobabilityCalculator(n, p, alpha);
        return calculator.calculateFailprobability(mtable, n, p, alpha);
    }

    /**
     * Checks if the ranking is fair for the given parameters
     * @param docs        The ranking to be checked
     * @param mtable      The mtable against to check
     * @return
     */
    public static boolean checkRankingMTable(TopDocs docs, int[] mtable) {
        int countProtected = 0;

        //if the mtable has more elements than there are in the top docs return false
        if(docs.scoreDocs.length + 1 < mtable.length)
            return false;

        //check number of protected element at each ranking
        for(int i=0; i < docs.scoreDocs.length; i++) {
            countProtected += ((FairScoreDoc)docs.scoreDocs[i]).isProtected ? 1 : 0;
            if(countProtected < mtable[i])
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
     * @return
     */
    public static boolean isFair(TopDocs docs, int n, double p, double alpha) {
        MTableGenerator generator = new MTableGenerator(n, p, alpha, true);
        return checkRankingMTable(docs, generator.getMTable());
    }
}
