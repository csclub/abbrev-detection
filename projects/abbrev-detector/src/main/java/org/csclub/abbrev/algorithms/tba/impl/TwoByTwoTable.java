package org.csclub.abbrev.algorithms.tba.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.csclub.abbrev.Abbreviation;

/**
 *
 * Standart 2-by-2 table with adaptaton to abbreviations. Time of abbreviation 
 * tables counting is linear in size of corpus.
 * 
 * @author Fedor Amosov
 */
public class TwoByTwoTable {
    
    private static final String PERIOD = ".";
    
    private String w1, w2;
    private int[][] count;
    
    public TwoByTwoTable(String w1, String w2) {
        this.w1 = w1;
        this.w2 = w2;
        count = new int[2][2];
    }
    
    public TwoByTwoTable add(int i, int j) {
        return add(i, j, 1);
    }
    
    public TwoByTwoTable add(int i, int j, int count) {
        if (1 <= i && i <= 2 && 1 <= j && j <= 2) {
            this.count[i - 1][j - 1] += count;
        }
        return this;
    }
    
    public int get(int i, int j) {
        if (1 <= i && i <= 2 && 1 <= j && j <= 2) {
            return count[i - 1][j - 1];
        }
        return 0;
    }
    
    public String getFirst() {
        return w1;
    }
    
    public String getSecond() {
        return w2;
    }
    
    @Override
    public String toString() {
        return "\t" + this.w2 + "\t!" + this.w2 + "\n" 
                + this.w1 + "\t" + count[0][0] + "\t" + count[0][1] + "\n"
                + "!" + this.w1 + "\t" + count[1][0] + "\t" + count[1][1] + "\n"; 
    }
    
    
    /**
     *
     * Counts all 2-by-2 tabels, which first token is abbreviation, second token 
     * is point.
     * 
     * @param corpus is list of neibhoor tokens from big corpus.
     * 
     * @param abbreviations is counted earlier list of abbreviations from 
     * argument corpus.
     * 
     * @return list of abbreviation tables.
     * 
     * @author Fedor Amosov
     */
    public static List<TwoByTwoTable> getAbbreviationTables(List<String> corpus, 
            List<Abbreviation> abbreviations) {
        
        Map<String, TwoByTwoTable> tables = new HashMap();
        Map<String, TwoByTwoTable> substract = new HashMap();
        for (Abbreviation abbreviation : abbreviations) {
            String w = abbreviation.getAbbrevText();
            w = w.substring(0, w.length() - 1);
            
            tables.put(w, new TwoByTwoTable(w, "."));
            substract.put(w, new TwoByTwoTable(w, "."));
        }
        
        int add21 = 0;
        int add22 = 0;     
        for (int i = 0; i < corpus.size() - 1; ++i) {      
            String w1 = corpus.get(i);
            String w2 = corpus.get(i + 1);
            
            if (!w2.equals(PERIOD)) {
                ++add22;
                if (tables.containsKey(w1)) {
                    tables.get(w1).add(1, 2);
                    substract.get(w1).add(2, 2);
                }
            } else {
                ++add21;
                if (tables.containsKey(w1)) {
                    tables.get(w1).add(1, 1);
                    substract.get(w1).add(2, 1);
                }
            }
            
        }
        
        List<TwoByTwoTable> result = new ArrayList();
        for (Entry<String, TwoByTwoTable> entry : tables.entrySet()) {
            add21 -= substract.get(entry.getKey()).get(2, 1);
            add22 -= substract.get(entry.getKey()).get(2, 2);
            result.add(entry.getValue().add(2, 1, add21).add(2, 2, add22));
        }
        
        return result;
    }
}
