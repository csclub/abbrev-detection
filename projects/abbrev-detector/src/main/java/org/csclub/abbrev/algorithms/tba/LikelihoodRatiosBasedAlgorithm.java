package org.csclub.abbrev.algorithms.tba;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.AbbreviationExtractorEngine;
import org.csclub.abbrev.AbbreviationUtils;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Sentence;
import org.csclub.abbrev.Serializer;
import org.csclub.abbrev.WeightedAbbreviation;
import org.csclub.abbrev.algorithms.Algorithm;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.algorithms.tba.impl.AbbreviationCounter_impl;
import org.csclub.abbrev.algorithms.tba.impl.TwoByTwoTable;
import org.csclub.abbrev.evaluation.ConfusionMatrix;
import org.csclub.abbrev.impl.Configuration;
import org.csclub.abbrev.impl.ConfigurationParameter;
import org.csclub.abbrev.impl.InitializationException;

/**
 *
 * This is realisation of Likelihood Ratios. It is discussed in the 
 * article of Christopher Manning & Hinrich Schütze "Foundations of Statistical 
 * Natural Language Processing", section 5.3.4
 * 
 * @author Fedor Amosov
 */
public class LikelihoodRatiosBasedAlgorithm extends Algorithm {
    
    @ConfigurationParameter(name = "Threshold", defaultValue = "2.00")
    private Double threshold;

    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    private List<WeightedAbbreviation> abbreviations = new ArrayList();
    
    private static double EPS = 0.000001;
    //
    
    private double f(int k, int n, double x) { 
        if (Math.abs(1 - x) < EPS) {
            // (n - k) * Math.log(1 - x) = 0, because in this case, n = k, and 
            // lim_{x->0} x ln(x) = 0
            return k * Math.log(x); 
        }
        return k * Math.log(x) + (n - k) * Math.log(1 - x);
    }
    
    public LikelihoodRatiosBasedAlgorithm() {
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
            
            if (Double.isNaN(logLambdaScaled)) {
                continue;
            } else if (logLambdaScaled == Double.POSITIVE_INFINITY) {
                logLambdaScaled = Double.MAX_VALUE;
            } else if (logLambdaScaled == Double.NEGATIVE_INFINITY){
                logLambdaScaled = Double.MIN_VALUE;
            }

            if (threshold == null || logLambdaScaled > threshold) {
                //System.out.println()
                String abbrevText = table.getFirstWord() + AbbreviationUtils.PERIOD;
                abbrevTexts.add(abbrevText);
                abbrevWeights.put(abbrevText, logLambdaScaled);
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
    
    public static void main(String [] args) throws InitializationException, Exception {
        String corpusFile = "C:\\archive\\storage\\datasets\\medical\\medtag\\data\\medtag-sents.txt";
        Configuration config = new Configuration(new String [] 
                                                {   
                                                    "ConnectorClass", "org.csclub.abbrev.connectors.SentPerLineCorpusReader",
                                                    "Connector.FileName", corpusFile,
                                                    "Connector.FileEncoding", "UTF-8",
                                                    "AlgorithmClass", "org.csclub.abbrev.algorithms.tba.LikelihoodRatiosBasedAlgorithm",
                                                    "Algorithm.Threshold", ConfigurationParameter.NULL,
                                                    "OutputFileName", "C:\\work\\projects\\collaboration\\csclub\\abbrev-detection\\datasets\\medtag\\experiments\\FreeLing\\results\\likelihood.txt",
                                                    "OutputFileEncoding", "UTF-8"
                                                } 
                                             );
        AbbreviationExtractorEngine app = new AbbreviationExtractorEngine();
        app.init(config);
        app.run();
    }
}
