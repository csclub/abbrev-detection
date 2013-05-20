package org.csclub.abbrev.algorithms.tba;

import java.util.ArrayList;
import java.util.List;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Sentence;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationCounter_impl;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.impl.ConfigurationParameter;

/**
 *
 * @author Fedor Amosov
 */
public class LengthBasedAlgorithm extends Algorithm {
    
    @ConfigurationParameter(name = "Threshold", defaultValue = "3")
    private double threshold;

    
    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<CorpusAbbreviation> abbreviations = new ArrayList();
    
    public LengthBasedAlgorithm() {
        abbrevCounter = new AbbreviationCounter_impl();
        abrbevExtractor = new AbbreviationExtractor_impl();
    }
    
    @Override
    public void run(Corpus corpus) {
        
        for (Sentence sentence : corpus.getSentences()) {
            List<CorpusAbbreviation> sentenceAbbreviations = 
                    abrbevExtractor.extract(sentence);
            abbrevCounter.onNewAbbreviations(sentenceAbbreviations);
        }
        
        List<CorpusAbbreviation> sortedAbbreviations = abbrevCounter.getSortedAbbreviations();
        
        for (CorpusAbbreviation abbrev : sortedAbbreviations) {
            if (abbrev.getAbbrevText().length() <= threshold) {
                abbreviations.add(abbrev);
            }    
        }
    }
    
    @Override
    public List<CorpusAbbreviation> getAbbreviations() {
        return abbreviations;
    }
}
