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
 * @author Fedor Amosov
 */
public class LengthBasedAlgorithm extends Algorithm {
    
    @ConfigurationParameter(name = "Threshold", defaultValue = "3")
    private Double threshold;

    
    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<WeightedAbbreviation> abbreviations = new ArrayList();
    
    public LengthBasedAlgorithm() {
        abbrevCounter = new AbbreviationCounter_impl();
        abrbevExtractor = new AbbreviationExtractor_impl();
        
        AbbreviationCounter_impl abbrevCounterImpl = (AbbreviationCounter_impl)abbrevCounter;
        abbrevCounterImpl.setSortStrategy(AbbreviationCounter_impl.SortStrategy.ByLenghAndFrequency);
    }
    
    @Override
    public void run(Corpus corpus) {
        
        for (Sentence sentence : corpus.getSentences()) {
            List<CorpusAbbreviation> sentenceAbbreviations = abrbevExtractor.extract(sentence);
            abbrevCounter.onNewAbbreviations(sentenceAbbreviations);
        }
        
        List<CorpusAbbreviation> sortedAbbreviations = abbrevCounter.getSortedAbbreviations();
        
        for (CorpusAbbreviation abbrev : sortedAbbreviations) {
            if (threshold == null || abbrev.getAbbrevText().length() <= threshold) {
                abbreviations.add(new WeightedAbbreviation(abbrev.getAbbrevText(), abbrev.getAbbrevText().length()));
            }    
        }
    }
    
    @Override
    public List<WeightedAbbreviation> getAbbreviations() {
        return abbreviations;
    }
}
