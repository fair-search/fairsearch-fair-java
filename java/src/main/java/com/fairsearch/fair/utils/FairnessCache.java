package com.fairsearch.fair.utils;

import com.fairsearch.fair.lib.MTableGenerator;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class FairnessCache {

    private ConcurrentHashMap<String, int[]> map;

    FairnessCache() {
        map = new ConcurrentHashMap<>();
    }

    public int[] get(int k, float p, float a) {
        String id = generateId(p,a,k);
        if (map.containsKey(id)) {
            return map.get(id);
        } else {
            int[] mtable = generateMtable(k, p ,a, id);
            map.put(id, mtable);
            return mtable;
        }
    }

    private int[] generateMtable(int k, float p, float a, String id) {
        MTableGenerator gen = new MTableGenerator(k, p, a, true);
        int[] mtable = gen.getMTable();
        return Arrays.copyOfRange(mtable,1,mtable.length);
    }

    private static String generateId(float proportion, float alpha, int k) {
        return new StringBuilder()
                .append("mtable(")
                .append(proportion).append(",")
                .append(alpha).append(",")
                .append(k)
                .append(")").toString();
    }
}
