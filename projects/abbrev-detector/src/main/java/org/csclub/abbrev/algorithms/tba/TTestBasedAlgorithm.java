package org.csclub.abbrev.algorithms.tba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.csclub.abbrev.AbbreviationUtils;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Sentence;
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
public class TTestBasedAlgorithm extends Algorithm <CorpusAbbreviation> {
    
    @ConfigurationParameter(name = "Threshold", defaultValue = "2.78")
    private double threshold;

    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<CorpusAbbreviation> abbreviations = new ArrayList();
    
    
    public TTestBasedAlgorithm() {
        abbrevCounter = new AbbreviationCounter_impl();
        abrbevExtractor = new AbbreviationExtractor_impl();
    }
    
    @Override
    public void run(Corpus corpus) {
        
        for (Sentence sentence : corpus.getSentences()) {
            List<CorpusAbbreviation> sentenceAbbreviations = abrbevExtractor.extract(sentence);
            abbrevCounter.onNewAbbreviations(sentenceAbbreviations);
        }
        
        List<String> neibTokens = AbbreviationUtils.tokenize(corpus);
        
        Map<String, Integer> unigramCount = new HashMap();
        for (String token : neibTokens) {
            if (!unigramCount.containsKey(token)) {
                unigramCount.put(token, 1);
            } else {
                unigramCount.put(token, unigramCount.get(token) + 1);
            }
        }
        
        Map<String, Integer> bigramCount = new HashMap();
        for (int i = 0; i < neibTokens.size() - 1; ++i) {
            String bigram = neibTokens.get(i) + neibTokens.get(i + 1);
            if (!bigramCount.containsKey(bigram)) {
                bigramCount.put(bigram, 1);
            } else {
                bigramCount.put(bigram, bigramCount.get(bigram) + 1);
            }
        }
        
        int nBigrams = neibTokens.size() - 1;
        int nUnigrams = neibTokens.size();
        double periodProb = 1.0 * unigramCount.get(".") / nUnigrams;
        
        for (CorpusAbbreviation abbrev : (List<CorpusAbbreviation>)abbrevCounter.getSortedAbbreviations()) {
            String w = abbrev.getAbbrevText().substring(0, abbrev.getAbbrevText().length() - 1);

            if (bigramCount.containsKey(w + ".")) {
                double freqWP = 1.0 * bigramCount.get(w + ".") / nBigrams;
                double pWP = periodProb * unigramCount.get(w) / nUnigrams;
                
                double t = (freqWP - pWP) / Math.sqrt(freqWP / nBigrams);

                if (t > threshold) {
                    abbreviations.add(abbrev);
                }
            }
        }
    }
    
    @Override
    public List<CorpusAbbreviation> getAbbreviations() {
        return abbreviations;
    }
}
