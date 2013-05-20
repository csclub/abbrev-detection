package org.csclub.abbrev.algorithms.tba;

import java.util.ArrayList;
import java.util.List;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Sentence;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.algorithms.tba.impl.TrieAbbreviationCounter;

/**
 *
 * @author Fedor Amosov
 */
public class NextSymbolBasedAlgorithm extends Algorithm {
    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<CorpusAbbreviation> abbreviations = new ArrayList();
    
    public NextSymbolBasedAlgorithm() {
        abbrevCounter = new TrieAbbreviationCounter();
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
            String text = abbrev.getAbbrevText();
            int upper = 0, lower = 0;
            for (String context : abbrev.getAbbrevContexts()) {
                if (context.indexOf(text) + text.length() + 1 < context.length()) { 
                    char nextSymbol = context.charAt(context.indexOf(text) + text.length() + 1);  
                    if (Character.isUpperCase(nextSymbol)) {
                        ++upper;
                    } else {
                        ++lower;
                    }
                }
            }
            if (lower == 0 || upper == 0) {
                abbreviations.add(abbrev);
            }   
        }
    }
    
    @Override
    public List<CorpusAbbreviation> getAbbreviations() {
        return abbreviations;
    }
}
