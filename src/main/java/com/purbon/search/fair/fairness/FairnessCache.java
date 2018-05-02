package com.purbon.search.fair.fairness;

import java.util.concurrent.ConcurrentHashMap;

public abstract class FairnessCache {

    private ConcurrentHashMap<String, int[]> map;

    FairnessCache() {
        map = new ConcurrentHashMap<>();
    }

    public int[] get(String docId) {

        if (map.containsKey(docId)) {
            return map.get(docId);
        } else {
            int[] mtable = getById(docId);
            map.put(docId, mtable);
            return mtable;
        }
    }

    protected abstract int[] getById(String docId);

}
