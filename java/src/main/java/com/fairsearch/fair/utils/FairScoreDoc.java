package com.fairsearch.fair.utils;

import org.apache.lucene.search.ScoreDoc;

public class FairScoreDoc extends ScoreDoc {

    /** Specifies if the document is protected or not */
    public boolean isProtected;

    public FairScoreDoc(int doc, float score) {
        super(doc, score);
    }

    public FairScoreDoc(int doc, float score, int shardIndex) {
        super(doc, score, shardIndex);
    }

    public FairScoreDoc(int doc, float score, boolean isProtected) {
        super(doc, score);
        this.isProtected = isProtected;
    }

    public FairScoreDoc(int doc, float score, int shardIndex, boolean isProtected) {
        super(doc, score, shardIndex);
        this.isProtected = isProtected;
    }

    @Override
    public String toString() {
        return "doc=" + doc + " score=" + score + " isProtected=" + isProtected;
    }
}
