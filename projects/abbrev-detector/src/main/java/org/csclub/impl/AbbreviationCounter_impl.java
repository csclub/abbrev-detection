/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.impl;

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
import org.csclub.AbbreviationCounter;

/**
 *
 * @author Sergey Serebryakov
 * A naive implementation of the abbreviation counter component.
 * Do not modify it. Rather, make a new class with meaningful name and some logic into it.
 */
public class AbbreviationCounter_impl implements AbbreviationCounter {

    private Map<String, Integer> abbrevCounters;
    private List<Pair<String, Integer>> sortedAbbreviations;
    
    public AbbreviationCounter_impl() {
        abbrevCounters = new HashMap<String, Integer>();
    }
    
    public void onNewAbbreviations(final List<String> abbreviations) {
        for(String abbreviation : abbreviations) {
            if (abbrevCounters.containsKey(abbreviation)) {
                abbrevCounters.put(abbreviation, abbrevCounters.get(abbreviation) + 1);
            } else {
                abbrevCounters.put(abbreviation, 1);
            }
        }
    }
    
    public void corpusProcessComplete() {
        sortedAbbreviations = new ArrayList<Pair<String, Integer>> ();
        for (Entry<String, Integer> entry : abbrevCounters.entrySet()) {
            sortedAbbreviations.add(new ImmutablePair<String, Integer> (entry.getKey(), entry.getValue()));
        }
        Collections.sort(sortedAbbreviations, new Comparator<Pair<String, Integer>>() {
            public int compare(Pair<String, Integer> o1, Pair<String, Integer> o2) {
                if (o1.getValue() < o2.getValue() ) {
                    return 1;
                } if (o1.getValue() > o2.getValue() ) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
    }
    
     public void print(PrintStream ps) {
         for (Pair<String, Integer> abbreviation : sortedAbbreviations) {
             ps.println(String.format("%s\t%d", abbreviation.getKey(), abbreviation.getValue()));
         }
         
     }
}
