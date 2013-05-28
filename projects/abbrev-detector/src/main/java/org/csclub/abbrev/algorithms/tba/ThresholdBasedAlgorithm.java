package org.csclub.abbrev.algorithms.tba;

import java.util.ArrayList;
import java.util.List;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Sentence;
import org.csclub.abbrev.WeightedAbbreviation;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationCounter_impl;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.impl.ConfigurationParameter;

/**
 *
 * @author Sergey Serebryakov
 * A naive implementation that uses constant threshold to extract abbreviations.
 * Everything is stored in the memory.
 */
public class ThresholdBasedAlgorithm extends Algorithm {
    
    @ConfigurationParameter(name = "Threshold", defaultValue = "32")
    private Integer threshold;

    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<WeightedAbbreviation> abbreviations;
    
    public void setThreshold(Integer threshold) { this.threshold = threshold; }
    public Integer getThreshold() { return threshold; }
    
    public ThresholdBasedAlgorithm() {
        abbrevCounter = new AbbreviationCounter_impl();
        abrbevExtractor = new AbbreviationExtractor_impl();
        
        AbbreviationCounter_impl abbrevCounterImpl = (AbbreviationCounter_impl)abbrevCounter;
        abbrevCounterImpl.setSortStrategy(AbbreviationCounter_impl.SortStrategy.ByFrequency);
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
            if (threshold == null || abbrev.getAbbrevCount() >= threshold) {
                abbreviations.add(new WeightedAbbreviation(abbrev.getAbbrevText(), abbrev.getAbbrevCount()));
            }
        }
    }

    @Override
    public List<WeightedAbbreviation> getAbbreviations() { 
        return abbreviations; 
    }
    
}
