package com.github.fairsearch.utils;

public class InternalFairnessTableLookup implements FairnessTableLookup {

    private FairnessCache cache;

    public InternalFairnessTableLookup() {
        this.cache = new FairnessCache();
    }

    public int fairness(int k, float proportion, float significance) {
        throw new NotImplementedException();
    }

    @Override
    public int[] fairnessAsTable(int k, float p, float a) {
        return cache.get(k, p, a);
    }

}
