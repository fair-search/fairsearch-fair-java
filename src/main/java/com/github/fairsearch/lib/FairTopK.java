package com.github.fairsearch.lib;

import com.github.fairsearch.utils.FairnessTableLookup;
import com.github.fairsearch.utils.InternalFairnessTableLookup;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import java.util.Arrays;
import java.util.List;

public class FairTopK {

    private FairnessTableLookup fairnessLookup;

    public FairTopK()
    {
        fairnessLookup = new InternalFairnessTableLookup();
    }

    public FairTopK(FairnessTableLookup fairnessLookup) {
        this.fairnessLookup = fairnessLookup;
    }

    public TopDocs fairTopK(List<ScoreDoc> npQueue, List<ScoreDoc> pQueue, int k, double p, double alpha) {

        // we'll re-order the documents manually, but ES wanted a descending "score" for each element
        // so, we'll just take k as the highest score and assign a decreased number to the following
        long scorer = k;

        // get the mtable
        int [] m = fairnessLookup.fairnessAsTable(k, (float)p, (float)alpha);

        int npSize = npQueue.size();
        int pSize = pQueue.size();

        ScoreDoc[] t = new ScoreDoc[npSize+pSize];

        int tp = 0;
        int tn = 0;
        int i = 0;
        int countProtected = 0;
        float maxScore = 0.0f;
        while ( ((tp+tn) < k) && !(tp >= pSize && tn >= npSize)) {
            ScoreDoc doc;
            if (tp  >= pSize) { // no more protected candidates available, take non protected
                doc = npQueue.get(tn);
                doc.score = scorer--;
                t[i] = doc;
                i = i + 1;
                tn = tn + 1;
            } else if (tn >= npSize) { // no more non protected candidates, take protected instead.
                doc = pQueue.get(tp);
                doc.score = scorer--;
                t[i] = doc;
                i = i + 1;
                tp = tp + 1;
                countProtected = countProtected + 1;
            } else if (countProtected < m[tp+tn+1]) { // protected candidates
                doc = pQueue.get(tp);
                doc.score = scorer--;
                t[i] = doc;
                i = i + 1;
                tp = tp + 1;
                countProtected = countProtected + 1;
            } else { // Non protected candidates
                if (pQueue.get(tp).score >= npQueue.get(tn).score) {
                    doc = pQueue.get(tp);
                    doc.score = scorer--;
                    t[i] = doc;
                    i = i + 1;
                    tp = tp + 1;
                    countProtected = countProtected + 1;
                } else {
                    doc = npQueue.get(tn);
                    doc.score = scorer--;
                    t[i] = doc;
                    i = i + 1;
                    tn = tn + 1;
                }
            }
            if (doc != null) {
                if (doc.score > maxScore) {
                    maxScore = doc.score;
                }
            }
        }

        while(tp < pQueue.size()) {
            ScoreDoc doc = pQueue.get(tp);
            doc.score = 0;
            t[i] = doc;
            i = i + 1;
            tp = tp + 1;
        }

        while(tn < npQueue.size()) {
            ScoreDoc doc = npQueue.get(tn);
            doc.score = 0;
            t[i] = doc;
            i = i + 1;
            tn = tn + 1;
        }

        TopDocs docs = new TopDocs(t.length, t, k);
        Arrays.sort(docs.scoreDocs, (a, b) -> {
            if (a.score > b.score) {
                return -1;
            }
            if (a.score < b.score) {
                return 1;
            }
            // Safe because doc ids >= 0
            return a.doc - b.doc;
        });

        return docs;
    }
}
