package com.github.fairsearch.lib;

import com.github.fairsearch.utils.DataFrame;
import com.github.fairsearch.utils.BinomDistKey;
import org.apache.commons.math3.distribution.BinomialDistribution;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class FailProbabilityCalculator {

    int k;
    double p;
    double alpha;
    DataFrame auxMTable;
    HashMap<BinomDistKey, Double> pmfCache;

    public FailProbabilityCalculator(int k, double p, double alpha) {

        this.k = k;
        this.p = p;
        this.alpha = alpha;
        this.pmfCache = new HashMap<>();
    }

    public static void main(String[] args) {
        BinomialDistribution binomialDistribution = new BinomialDistribution(9, 0.2);

        double sum = 0;
        for(int i=1; i<9; i++) {
            double probability = binomialDistribution.probability(i);
            System.out.println(probability);
            sum += probability;
        }
        System.out.println("******");
        System.out.println(sum);
        System.out.println("******");
        System.out.println(1 - sum);
    }

    public abstract double calculateFailProbability(int[] mtable);

    double getFromPmfCache(int trials, int successes){
        BinomDistKey key = new BinomDistKey(trials,successes);
        if(pmfCache.containsKey(key)){
            return pmfCache.get(key);
        }else{
            BinomialDistribution binomialDistribution = new BinomialDistribution(key.getTrials(), p);
            double probability = binomialDistribution.probability(key.getSuccesses());
            pmfCache.put(key,probability);
            return probability;
        }
    }

    ArrayList<Integer> sublist(ArrayList<Integer> array, int startIndex, int endIndex) {
        ArrayList<Integer> sublist = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            sublist.add(array.get(i));
        }
        return sublist;
    }

    int sum(ArrayList<Integer> array) {
        int sum = 0;
        for (int i : array) {
            sum += i;
        }
        return sum;
    }
}
