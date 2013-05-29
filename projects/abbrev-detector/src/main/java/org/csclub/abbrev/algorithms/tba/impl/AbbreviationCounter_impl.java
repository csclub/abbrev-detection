/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.algorithms.tba.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.csclub.abbrev.algorithms.tba.AbbreviationCounter;
import org.csclub.abbrev.algorithms.tba.CorpusAbbreviation;

/**
 *
 * @author Sergey Serebryakov
 * A naive implementation of the abbreviation counter component.
 * Do not modify it. Rather, make a new class with meaningful name and some logic into it.
 */
public class AbbreviationCounter_impl implements AbbreviationCounter <CorpusAbbreviation> {

    /** for fast access, a mapping from abbreviation covered text into abbreviation structure */
    private Map<String, CorpusAbbreviation> abbrevsStatistics;
    /** list of sorted abbreviations according to occurrence statistics */
    private List<CorpusAbbreviation> sortedAbbrevs;
    /***/
    public static enum SortStrategy {
        ByLength,
        ByFrequency,
        ByLenghAndFrequency,
        None
    }
    SortStrategy sortStrategy = SortStrategy.ByFrequency;
    
    public void setSortStrategy(SortStrategy sortStrategy) {
        this.sortStrategy = sortStrategy;
        
    }
    
    public AbbreviationCounter_impl() {
        abbrevsStatistics = new HashMap<>();
    }
    
    @Override
    public void onNewAbbreviations(final List<CorpusAbbreviation> abbreviations) {
        for(CorpusAbbreviation abbreviation : abbreviations) {
            String abbrevText = abbreviation.getAbbrevText();
            if (false == abbrevsStatistics.containsKey(abbrevText)) {
                abbrevsStatistics.put(abbrevText, abbreviation);
            } else {
                CorpusAbbreviation existingAbbrev = abbrevsStatistics.get(abbrevText);
                existingAbbrev.incrementCounter();
                for(String abbrevContext : abbreviation.getAbbrevContexts()) {
                    existingAbbrev.addAbbrevContext(abbrevContext);
                }
            }
        }
    }
    
    @Override
    public void corpusProcessComplete() {
        sortedAbbrevs = new ArrayList<> ();
        for (Entry<String, CorpusAbbreviation> entry : abbrevsStatistics.entrySet()) {
            sortedAbbrevs.add(entry.getValue());
        }
        if ( sortStrategy != SortStrategy.None) {
            Collections.sort(sortedAbbrevs, new Comparator<CorpusAbbreviation>() {
                @Override
                public int compare(CorpusAbbreviation o1, CorpusAbbreviation o2) {

                    switch (sortStrategy) {
                        case ByLength:
                            if (o1.getAbbrevText().length() > o2.getAbbrevText().length()) {
                                return 1;
                            }
                            if (o1.getAbbrevText().length() < o2.getAbbrevText().length()) {
                                return -1;
                            }
                            return 0;

                        case ByFrequency:
                            if (o1.getAbbrevCount() < o2.getAbbrevCount() ) {
                                return 1;
                            }
                            if (o1.getAbbrevCount() > o2.getAbbrevCount() ) {
                                return -1;
                            }
                            return 0;
                        case ByLenghAndFrequency:
                            if (o1.getAbbrevText().length() > o2.getAbbrevText().length()) {
                                return 1;
                            }
                            if (o1.getAbbrevText().length() < o2.getAbbrevText().length()) {
                                return -1;
                            }
                            if (o1.getAbbrevCount() < o2.getAbbrevCount() ) {
                                return 1;
                            }
                            if (o1.getAbbrevCount() > o2.getAbbrevCount() ) {
                                return -1;
                            }
                            return 0;
                        case None:
                        default:
                            return 0;
                    }
                }
            });
        }
    }
    
    @Override
    public void print(PrintStream ps) {
        for (CorpusAbbreviation abbreviation : sortedAbbrevs) {
            ps.println(abbreviation);
        }
        System.out.println("-----");
        System.out.println("Total number of unique abbreviations: " + sortedAbbrevs.size());
        
    }
    
    @Override
    public List<CorpusAbbreviation> getSortedAbbreviations() {
        if (null == sortedAbbrevs) {
            corpusProcessComplete();
        }
        return sortedAbbrevs;
    }
}
