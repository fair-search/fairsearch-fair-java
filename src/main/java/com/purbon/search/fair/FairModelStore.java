package com.purbon.search.fair;

public class FairModelStore {

    public static String STORE_NAME = ".fs_store";

    public static final String TYPE = "store";

    public static boolean isStore(String indexName) {
        return STORE_NAME.equals(indexName);
    }

}
