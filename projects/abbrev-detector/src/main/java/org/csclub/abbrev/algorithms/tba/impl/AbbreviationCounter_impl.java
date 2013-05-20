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
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.csclub.abbrev.algorithms.tba.AbbreviationCounter;
import org.csclub.abbrev.algorithms.tba.CorpusAbbreviation;

/**
 *
 * @author Sergey Serebryakov
 * A naive implementation of the abbreviation counter component.
 * Do not modify it. Rather, make a new class with meaningful name and some logic into it.
 */
public class AbbreviationCounter_impl implements AbbreviationCounter <CorpusAbbreviation> {

    private Map<String, CorpusAbbreviation> abbrevCounters;
    private List<Pair<String, CorpusAbbreviation>> sortedAbbreviations;
    
    public AbbreviationCounter_impl() {
        abbrevCounters = new HashMap<>();
    }
    
    @Override
    public void onNewAbbreviations(final List<CorpusAbbreviation> abbreviations) {
        for(CorpusAbbreviation abbreviation : abbreviations) {
            String abbrevText = abbreviation.getAbbrevText();
            if (false == abbrevCounters.containsKey(abbrevText)) {
                abbrevCounters.put(abbrevText, abbreviation);
            } else {
                CorpusAbbreviation existingAbbrev = abbrevCounters.get(abbrevText);
                existingAbbrev.incrementCounter();
                for(String abbrevContext : abbreviation.getAbbrevContexts()) {
                    existingAbbrev.addAbbrevContext(abbrevContext);
                }
            }
        }
    }
    
    @Override
    public void corpusProcessComplete() {
        sortedAbbreviations = new ArrayList<> ();
        for (Entry<String, CorpusAbbreviation> entry : abbrevCounters.entrySet()) {
            sortedAbbreviations.add(new ImmutablePair<> (entry.getKey(), entry.getValue()));
        }
        Collections.sort(sortedAbbreviations, new Comparator<Pair<String, CorpusAbbreviation>>() {
            @Override
            public int compare(Pair<String, CorpusAbbreviation> o1, Pair<String, CorpusAbbreviation> o2) {
                
                if (o1.getKey().length() > o2.getKey().length()) {
                    return 1;
                }
                if (o1.getKey().length() < o2.getKey().length()) {
                    return -1;
                }
                
                if (o1.getValue().getAbbrevCount() < o2.getValue().getAbbrevCount() ) {
                    return 1;
                }
                if (o1.getValue().getAbbrevCount() > o2.getValue().getAbbrevCount() ) {
                    return -1;
                }
                
                return 0;
            }
        });
    }
    
    @Override
    public void print(PrintStream ps) {
        for (Pair<String, CorpusAbbreviation> abbreviation : sortedAbbreviations) {
            ps.println(abbreviation.getValue().toString());
        }
        System.out.println("-----");
        System.out.println("Total number of unique abbreviations: " + sortedAbbreviations.size());
        
    }
    
    @Override
    public List<CorpusAbbreviation> getSortedAbbreviations() {
        if (sortedAbbreviations == null) {
            corpusProcessComplete();
        }
        
        List<CorpusAbbreviation> result = new ArrayList<>();
        
        for (Pair<String, CorpusAbbreviation> abbreviation : sortedAbbreviations) {
            result.add(abbreviation.getValue());
        }
        
        return result;
    }
}
