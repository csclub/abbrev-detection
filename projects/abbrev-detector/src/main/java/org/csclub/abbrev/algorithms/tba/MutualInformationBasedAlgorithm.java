/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.algorithms.tba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.AbbreviationUtils;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Sentence;
import org.csclub.abbrev.WeightedAbbreviation;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationCounter_impl;
import org.csclub.abbrev.algorithms.tba.impl.TwoByTwoTable;
import org.csclub.abbrev.impl.ConfigurationParameter;

/**
 *
 * @author Fedor Amosov
 */
public class MutualInformationBasedAlgorithm extends Algorithm {
    
    @ConfigurationParameter(name = "Threshold", defaultValue = "0.55")
    private Double threshold;

    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<WeightedAbbreviation> abbreviations = new ArrayList();
    
    public MutualInformationBasedAlgorithm() {
        abbrevCounter = new AbbreviationCounter_impl();
        abrbevExtractor = new AbbreviationExtractor_impl();
        
        AbbreviationCounter_impl abbrevCounterImpl = (AbbreviationCounter_impl)abbrevCounter;
        abbrevCounterImpl.setSortStrategy(AbbreviationCounter_impl.SortStrategy.None);
    }
    
    @Override
    public void run(Corpus corpus) {
        
        for (Sentence sentence : corpus.getSentences()) {
            List<CorpusAbbreviation> sentenceAbbreviations = abrbevExtractor.extract(sentence);
            abbrevCounter.onNewAbbreviations(sentenceAbbreviations);
        }
        
        List<String> neibTokens = AbbreviationUtils.tokenize(corpus, false);
        List<Abbreviation> sortedAbbreviations = abbrevCounter.getSortedAbbreviations();
        
        List<TwoByTwoTable> tables = TwoByTwoTable.getAbbreviationTables(
            neibTokens, 
            sortedAbbreviations
        );
        
        Set<String> abbrevTexts = new HashSet();
        Map<String, Double> abbrevWeights = new HashMap<> ();
        for (TwoByTwoTable table : tables) {
            int cWP = table.get(1, 1);   
            int cW = table.getFirstWordCount();
            int cP = table.getSecondWordCount();
            int n = neibTokens.size();
            
            if (cWP == 0 || cP == 0) {
                continue;
            }
            
            double iWP = Math.log(((double)cWP / (cW * cP)) * n);
            
            double s1 = cWP;
            double s2 = Math.exp(-table.getFirstWord().length());
            
            double mi = s1 * s2 * iWP; 
            
            if (Double.isNaN(mi)) {
                continue;
            } else if (mi == Double.POSITIVE_INFINITY) {
                mi = Double.MAX_VALUE;
            } else if (mi == Double.NEGATIVE_INFINITY){
                mi = Double.MIN_VALUE;
            }

            if (threshold == null || mi > threshold) {
                String abbrevText = table.getFirstWord() + AbbreviationUtils.PERIOD;
                abbrevTexts.add(abbrevText);
                abbrevWeights.put(abbrevText, mi);
            }
        }
        
        for (Abbreviation abbrev : sortedAbbreviations) {
            if (abbrevTexts.contains(abbrev.getAbbrevText())) {
                abbreviations.add(new WeightedAbbreviation(abbrev.getAbbrevText(), abbrevWeights.get(abbrev.getAbbrevText())));
            }
        }
    }
    
    @Override
    public List<WeightedAbbreviation> getAbbreviations() {
        return abbreviations;
    }
}
