package org.csclub.abbrev.algorithms.tba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.AbbreviationUtils;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Sentence;
import org.csclub.abbrev.WeightedAbbreviation;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationCounter_impl;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.impl.ConfigurationParameter;

/**
 *
 * This is realisation of t-test. It is discussed in the article of Christopher 
 * Manning & Hinrich Schütze "Foundations of Statistical Natural Language 
 * Processing", section 5.3.1
 * 
 * @author fedor
 */
public class TTestBasedAlgorithm extends Algorithm  {
    
    @ConfigurationParameter(name = "Threshold", defaultValue = "2.78")
    private Double threshold;

    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<WeightedAbbreviation> abbreviations = new ArrayList();
    
    
    public TTestBasedAlgorithm() {
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
        
        List<String> tokens = AbbreviationUtils.tokenize(corpus, false);
        
        Map<String, Integer> unigrams = new HashMap();
        for (String token : tokens) {
            if (!unigrams.containsKey(token)) {
                unigrams.put(token, 1);
            } else {
                unigrams.put(token, unigrams.get(token) + 1);
            }
        }
        
        Map<String, Integer> bigrams = new HashMap();
        for (int i = 0; i < tokens.size() - 1; ++i) {
            String bigram = tokens.get(i).concat( tokens.get(i + 1) );
            if (!bigrams.containsKey(bigram)) {
                bigrams.put(bigram, 1);
            } else {
                bigrams.put(bigram, bigrams.get(bigram) + 1);
            }
        }
        
        int nBigrams = bigrams.size() - 1;
        int nUnigrams = unigrams.size();
        double periodProb = (1.0 * unigrams.get(AbbreviationUtils.PERIOD)) / nUnigrams;
        
        List<? extends Abbreviation> candidateAbbreviations = abbrevCounter.getSortedAbbreviations();
        for (Abbreviation abbrev : candidateAbbreviations) {
            String abbrevText = abbrev.getAbbrevText();
            if (bigrams.containsKey(abbrevText)) {
                // actual probability of a bigram (candidate abbreviation)
                double freqWP = (1.0 * bigrams.get(abbrevText)) / nBigrams;
                
                String abbrevTextPart = abbrev.getAbbrevText().substring(0, abbrev.getAbbrevText().length() - 1);
                if (unigrams.containsKey(abbrevTextPart)) {
                    // probability of a null hypothesis that the occurence of a text with a period is just a chance
                    // it is a product of a period probability and a probability of a term 'abbrevTextPart'
                    double pWP = periodProb * unigrams.get(abbrevTextPart) / nUnigrams;
                    // apply t-test to compute t-value
                    double t = (freqWP - pWP) / Math.sqrt(freqWP / nBigrams);
                    // if this t-value is larger than threshold we can reject the null hypothesis that the text and a 
                    // period in a candidate abrbeviation occure close to each other just by chance.
                    if (threshold == null || t > threshold) {
                        abbreviations.add(new WeightedAbbreviation(abbrev.getAbbrevText(), t));
                    }
                }
            }
        }
    }
    
    @Override
    public List<WeightedAbbreviation> getAbbreviations() {
        return abbreviations;
    }
}
