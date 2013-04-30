package org.csclub.abbrev.algorithms.tba;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.csclub.abbrev.AbbreviationUtils;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Sentence;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.algorithms.tba.impl.TrieAbbreviationCounter;
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
public class ChiSquareTestBasedAlgorithm extends Algorithm<CorpusAbbreviation> {
    
    @ConfigurationParameter(name = "Threshold", defaultValue = "5.475")
    private double threshold;

    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<CorpusAbbreviation> abbreviations = new ArrayList();
    
    
    public ChiSquareTestBasedAlgorithm() {
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
        
        List<String> neibTokens = AbbreviationUtils.tokenize(corpus);
        
        List<TwoByTwoTable> tables = TwoByTwoTable.getAbbreviationTables(
            neibTokens, 
            abbrevCounter.getSortedAbbreviations()
        );
        
        Set<String> abbrevTexts = new HashSet();
        for (TwoByTwoTable table : tables) {
            int c11 = table.get(1, 1);
            int c12 = table.get(1, 2);
            int c21 = table.get(2, 1);
            int c22 = table.get(2, 2);
            
            double chi2 = neibTokens.size();
            chi2 /= (c12 + c22);
            chi2 /= (c11 + c12);
            chi2 *= (c11 * c22 - c12 * c21);
            chi2 /= (c21 + c22);
            chi2 *= (c11 * c22 - c12 * c21);
            chi2 /= (c11 + c21);
            
            if (chi2 > threshold) {
                abbrevTexts.add(table.getFirst() + ".");
            }
        }
        
        for (CorpusAbbreviation abbrev : (List<CorpusAbbreviation>)abbrevCounter.getSortedAbbreviations()) {
            if (abbrevTexts.contains(abbrev.getAbbrevText())) {
                abbreviations.add(abbrev);
            }
        }
    }
    
    @Override
    public List<CorpusAbbreviation> getAbbreviations() {
        return abbreviations;
    }
}
