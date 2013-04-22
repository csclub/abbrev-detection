package org.csclub.abbrev.algorithms.tba;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Sentence;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.algorithms.tba.impl.TrieAbbreviationCounter;
import org.csclub.abbrev.impl.ConfigurationParameter;

/**
 *
 * @author fedor
 */
public class TTestBasedAlgorithm extends Algorithm {
    
    @ConfigurationParameter(name = "Threshold", defaultValue = "0.1")
    private double threshold;

    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<Abbreviation> abbreviations = new ArrayList();
    
    
    public TTestBasedAlgorithm() {
        abbrevCounter = new TrieAbbreviationCounter();
        abrbevExtractor = new AbbreviationExtractor_impl();
    }
    
    @Override
    public void run(Corpus corpus) {
        
        for (Sentence sentence : corpus.getSentences()) {
            List<Abbreviation> sentenceAbbreviations = abrbevExtractor.extract(sentence);
            abbrevCounter.onNewAbbreviations(sentenceAbbreviations);
        }
        
        List<String> neibTokens = new ArrayList();
        for (Sentence sentence : corpus.getSentences()) {
            for (String token : sentence.getTokens()) { 
                token = token.replaceAll("\\.+", ".");
                if (token.endsWith(".")) {
                    if (token.length() > 1) {
                        neibTokens.add(token.substring(0, token.length() - 1));
                    }
                    neibTokens.add(".");
                } else {
                    neibTokens.add(token);
                }
            }
        }
        
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
        
        for (Abbreviation abbrev : abbrevCounter.getSortedAbbreviations()) {
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
    public List<Abbreviation> getAbbreviations() {
        return abbreviations;
    }
}
