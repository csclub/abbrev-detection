package org.csclub.abbrev.algorithms.tba;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.csclub.abbrev.Abbreviation;
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
 * This is realisation of Likelihood Ratios. It is discussed in the 
 * article of Christopher Manning & Hinrich Schütze "Foundations of Statistical 
 * Natural Language Processing", section 5.3.4
 * 
 * @author Fedor Amosov
 */
public class LikelihoodRatiosBasedAlgorithm extends Algorithm {
    
    @ConfigurationParameter(name = "Threshold", defaultValue = "0.095")
    private double threshold;

    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<CorpusAbbreviation> abbreviations = new ArrayList();
    
    private static double EPS = 0.000001;
    
    private double f(int k, int n, double x) { 
        if (Math.abs(1 - x) < EPS) {
            // (n - k) * Math.log(1 - x) = 0, because in this case, n = k, and 
            // lim_{x->0} x ln(x) = 0
            return k * Math.log(x); 
        }
        return k * Math.log(x) + (n - k) * Math.log(1 - x);
    }
    
    public LikelihoodRatiosBasedAlgorithm() {
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
        List<Abbreviation> sortedAbbreviations = abbrevCounter.getSortedAbbreviations();
        
        List<TwoByTwoTable> tables = TwoByTwoTable.getAbbreviationTables(
            neibTokens, 
            sortedAbbreviations
        );
        
        Set<String> abbrevTexts = new HashSet();
        for (TwoByTwoTable table : tables) {
            int cWP = table.get(1, 1);
            int cWnotP = table.get(1, 2);
            
            if (cWP == 0) {
                continue;
            }
            
            int n = neibTokens.size();    
            int cW = table.getFirstWordCount();
            int cP = table.getSecondWordCount();
            
            double p = (double)cP / n;
            double p1 = (double)cWP / cW;
            double p2 = (double)(cP - cWP) / (n - cW);
            
            double logLambda = f(cWP, cW, p)  + f(cP - cWP, n - cW, p)
                             - f(cWP, cW, p1) - f(cP - cWP, n - cW, p2);
            
            double s1 = Math.exp(cWP);
            if (cWnotP > 0) {
                s1 = Math.exp((double)cWP / cWnotP);
            }
            
            double s2 = (double)(cWP - cWnotP) / (cWP + cWnotP);
            
            double s3 = Math.exp(-table.getFirstWord().length());
            
            double logLambdaScaled = -2  * s1 * s2 * s3 * logLambda;

            if (logLambdaScaled > threshold) {
                abbrevTexts.add(table.getFirstWord() + AbbreviationUtils.PERIOD);
            }
        }
        
        for (Abbreviation abbrev : sortedAbbreviations) {
            if (abbrevTexts.contains(abbrev.getAbbrevText())) {
                abbreviations.add((CorpusAbbreviation)abbrev);
            }
        }
    }
    
    @Override
    public List<CorpusAbbreviation> getAbbreviations() {
        return abbreviations;
    }
}
