/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.algorithms.tba;

import java.util.ArrayList;
import java.util.List;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationsMetadata;
import org.csclub.abbrev.algorithms.tba.impl.Delimiter;

/**
 *
 * @author Fedor Amosov
 */
public class FractionBasedAlgorithm extends Algorithm <CorpusAbbreviation> {
    
    private List<CorpusAbbreviation> goldAbbreviations;
    private List<CorpusAbbreviation> abbreviations;

    @Override
    public void run(Corpus corpus) {
        AbbreviationsMetadata metadata = (AbbreviationsMetadata)corpus.getMetadata();
        goldAbbreviations = metadata.getAbbreviations();
        
        Delimiter.setShareOfAbbreviations(goldAbbreviations);
        Delimiter.shareOfAbbreviations = Math.min(Delimiter.shareOfAbbreviations, 1.0);
        abbreviations = new ArrayList<> ();
        int thresholdIndex = (int)(Delimiter.shareOfAbbreviations * goldAbbreviations.size());
        for (int i = 0; i < thresholdIndex; ++i) {
            abbreviations.add(goldAbbreviations.get(i));
        }
    }

    @Override
    public List<CorpusAbbreviation> getAbbreviations() { return abbreviations; }
}
