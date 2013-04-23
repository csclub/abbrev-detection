package org.csclub.abbrev.algorithms.tba;

import java.util.ArrayList;
import java.util.List;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Sentence;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.algorithms.tba.impl.TrieAbbreviationCounter;
import org.csclub.abbrev.impl.ConfigurationParameter;

/**
 *
 * @author Sergey Serebryakov
 * A naive implementation that uses constant threshold to extract abbreviations.
 * Everything is stored in the memory.
 * 
 * 
 */
public class ThresholdBasedAlgorithm extends Algorithm {
    
    @ConfigurationParameter(name="Threshold", defaultValue="1")
    private int threshold;

    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<Abbreviation> abbreviations;
    
    public void setThreshold(int threshold) { this.threshold = threshold; }
    public int getThreshold() { return threshold; }
    
    public ThresholdBasedAlgorithm() {
        abbrevCounter = new TrieAbbreviationCounter();
        abrbevExtractor = new AbbreviationExtractor_impl();
    }
    
    @Override
    public void run(Corpus corpus) {
        
        for (Sentence sentence : corpus.getSentences()) {
            List<CorpusAbbreviation> sentenceAbbreviations = abrbevExtractor.extract(sentence);
            abbrevCounter.onNewAbbreviations(sentenceAbbreviations);
        }
        
        abbrevCounter.corpusProcessComplete();
        List<CorpusAbbreviation> allAbbreviations = abbrevCounter.getSortedAbbreviations();
        
        abbreviations = new ArrayList<> ();
        for (CorpusAbbreviation abbrev : allAbbreviations) {
            if (abbrev.getAbbrevCount() >= threshold) {
                abbreviations.add(abbrev);
            } else {
                break;
            }
        }
        
    }

    @Override
    public List<Abbreviation> getAbbreviations() { return abbreviations; }
    
}
