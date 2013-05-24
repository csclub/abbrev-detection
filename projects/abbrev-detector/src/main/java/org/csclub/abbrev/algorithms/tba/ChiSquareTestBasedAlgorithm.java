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
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationCounter_impl;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.algorithms.tba.impl.TwoByTwoTable;
import org.csclub.abbrev.impl.ConfigurationParameter;

/**
 *
 * This is realisation of Pearson’s chi-square test. It is discussed in the 
 * article of Christopher Manning & Hinrich Schütze "Foundations of Statistical 
 * Natural Language Processing", section 5.3.3
 * 
 * @author Fedor Amosov 
 */
public class ChiSquareTestBasedAlgorithm extends Algorithm {
    
    @ConfigurationParameter(name = "Threshold", defaultValue = "62.5")
    private Double threshold;

    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<WeightedAbbreviation> abbreviations = new ArrayList();
    
    
    public ChiSquareTestBasedAlgorithm() {
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
        
        List<String> neibTokens = AbbreviationUtils.tokenize(corpus);
        List<Abbreviation> sortedAbbreviations = abbrevCounter.getSortedAbbreviations();
        
        List<TwoByTwoTable> tables = TwoByTwoTable.getAbbreviationTables(
            neibTokens, 
            sortedAbbreviations
        );
        
        Set<String> abbrevTexts = new HashSet();
        Map<String, Double> abbrevWeights = new HashMap<> ();
        for (TwoByTwoTable table : tables) {
            int c11 = table.get(1, 1);
            int c12 = table.get(1, 2);
            int c21 = table.get(2, 1);
            int c22 = table.get(2, 2);
            
            double chi2 = neibTokens.size();
            int sum_c12_c22 = c12 + c22;
            int sum_c11_c12 = c11 + c12;
            int sum_c21_c22 = c21 + c22;
            int sum_c11_c21 = c11 + c21;
            if (sum_c12_c22 != 0 && sum_c11_c12 != 0 && sum_c21_c22 != 0 && sum_c11_c21 != 0) {
                chi2 /= sum_c12_c22;
                chi2 /= sum_c11_c12;
                chi2 *= (c11 * c22 - c12 * c21);
                chi2 /= sum_c21_c22;
                chi2 *= (c11 * c22 - c12 * c21);
                chi2 /= sum_c11_c21;

                if (threshold == null || chi2 > threshold) {
                    String abbrevText = table.getFirstWord() + AbbreviationUtils.PERIOD;
                    abbrevTexts.add(abbrevText);
                    abbrevWeights.put(abbrevText, chi2);
                }
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
