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
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.algorithms.tba.AbbreviationCounter;

/**
 *
 * @author Sergey Serebryakov
 * A naive implementation of the abbreviation counter component.
 * Do not modify it. Rather, make a new class with meaningful name and some logic into it.
 */
public class AbbreviationCounter_impl implements AbbreviationCounter {

    private Map<String, Abbreviation> abbrevCounters;
    private List<Pair<String, Abbreviation>> sortedAbbreviations;
    
    public AbbreviationCounter_impl() {
        abbrevCounters = new HashMap<String, Abbreviation>();
    }
    
    @Override
    public void onNewAbbreviations(final List<Abbreviation> abbreviations) {
        for(Abbreviation abbreviation : abbreviations) {
            String abbrevText = abbreviation.getAbbrevText();
            if (false == abbrevCounters.containsKey(abbrevText)) {
                abbrevCounters.put(abbrevText, abbreviation);
            } else {
                Abbreviation existingAbbrev = abbrevCounters.get(abbrevText);
                existingAbbrev.incrementCounter();
                for(String abbrevContext : abbreviation.getAbbrevContexts()) {
                    existingAbbrev.addAbbrevContext(abbrevContext);
                }
            }
        }
    }
    
    @Override
    public void corpusProcessComplete() {
        sortedAbbreviations = new ArrayList<Pair<String, Abbreviation>> ();
        for (Entry<String, Abbreviation> entry : abbrevCounters.entrySet()) {
            sortedAbbreviations.add(new ImmutablePair<String, Abbreviation> (entry.getKey(), entry.getValue()));
        }
        Collections.sort(sortedAbbreviations, new Comparator<Pair<String, Abbreviation>>() {
            public int compare(Pair<String, Abbreviation> o1, Pair<String, Abbreviation> o2) {
                if (o1.getValue().getAbbrevCount() < o2.getValue().getAbbrevCount() ) {
                    return 1;
                } if (o1.getValue().getAbbrevCount() > o2.getValue().getAbbrevCount() ) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }
    
    @Override
    public void print(PrintStream ps) {
        for (Pair<String, Abbreviation> abbreviation : sortedAbbreviations) {
            ps.println(abbreviation.getValue().toString(-1));
        }
        System.out.println("-----");
        System.out.println("Total number of unique abbreviations: " + sortedAbbreviations.size());
        
    }
    
    @Override
    public List<Abbreviation> getSortedAbbreviations() {
        if (sortedAbbreviations == null) {
            corpusProcessComplete();
        }
        
        List<Abbreviation> result = new ArrayList<Abbreviation>();
        
        for (Pair<String, Abbreviation> abbreviation : sortedAbbreviations) {
            result.add(abbreviation.getValue());
        }
        
        return result;
    }
}
