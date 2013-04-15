package org.csclub.abbrev;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.csclub.abbrev.evaluation.AbbreviationEvaluator;
import org.csclub.abbrev.evaluation.ConfusionMatrix;
import org.csclub.abbrev.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.impl.Delimiter;
import org.csclub.abbrev.impl.TrieAbbreviationCounter;

/**
 * Hello world!
 *
 */
public class AbbreviationExtractorApp  {
    
    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    public AbbreviationExtractorApp () {
        abbrevCounter = new TrieAbbreviationCounter();
        abrbevExtractor = new AbbreviationExtractor_impl();
    }
    
    public void extract(String fileName, String encoding) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), encoding);
        BufferedReader br = new BufferedReader(isr);
        
        String strLine;
        while ((strLine = br.readLine()) != null) {
            List<Abbreviation> abbreviations = abrbevExtractor.extract(strLine);
            abbrevCounter.onNewAbbreviations(abbreviations);
        }
        
        abbrevCounter.corpusProcessComplete();
        abbrevCounter.print(System.out);
    }
    
    public ConfusionMatrix delimiterAnalysis(final List<Abbreviation> goldAbbreviations) {
        Delimiter.setShareOfAbbreviations(goldAbbreviations);
        
        List<Abbreviation> trueAbbreviations = new ArrayList();
        
        for (int i = 0; i < goldAbbreviations.size(); ++i) {
            Abbreviation cur = goldAbbreviations.get(i);
            if (i <= Delimiter.shareOfAbbreviations * goldAbbreviations.size()) {
                System.out.println(i + 1 + "\tOK\t" + cur.toString(-1));
                
            }
            else {
                System.out.println(i + 1 + "\tNOT\t" + cur.toString(-1));
            }
            
            if (goldAbbreviations.get(i).getAbbrevState() == Abbreviation.AbbrevState.True) {
                trueAbbreviations.add(goldAbbreviations.get(i));
            }
          
        }
        System.out.println();
        
        AbbreviationEvaluator evaluator = new AbbreviationEvaluator(trueAbbreviations);
        return evaluator.evaluate(Delimiter.probablyAbbreviations(goldAbbreviations));
    }
    
    public ConfusionMatrix constLimitAnalysis(final List<Abbreviation> goldAbbreviations, int lim) {
        List<Abbreviation> trueAbbreviations = new ArrayList();
        List<Abbreviation> positiveAbbreviations = new ArrayList();
        
        
        for (int i = 0; i < goldAbbreviations.size(); ++i) {
            Abbreviation cur = goldAbbreviations.get(i);
            if (cur.getAbbrevCount() >= lim) {
                System.out.println(i + 1 + "\tOK\t" + cur.toString(-1));
                positiveAbbreviations.add(cur);
            }
            else {
                System.out.println(i + 1 + "\tNOT\t" + cur.toString(-1));
            }
            
            if (goldAbbreviations.get(i).getAbbrevState() == Abbreviation.AbbrevState.True) {
                trueAbbreviations.add(goldAbbreviations.get(i));
            }
        }
        System.out.println();
        
        AbbreviationEvaluator evaluator = new AbbreviationEvaluator(trueAbbreviations);
        return evaluator.evaluate(positiveAbbreviations);
    }
    
    public void collate(String fileName, String encoding) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        List<Abbreviation> goldAbbreviations = new Serializer().fromTextFile(fileName, encoding);
        
        abbrevCounter.onNewAbbreviations(goldAbbreviations);
        abbrevCounter.corpusProcessComplete();
        
        delimiterAnalysis(goldAbbreviations).print("Delimiter algorithm:");
        constLimitAnalysis(goldAbbreviations, 2).print("Const limit algorithm:");
        constLimitAnalysis(goldAbbreviations, 0).print("Zero limit algorithm:");
    }
    
    public static void main( String[] args ) {
        
//        String path = String.format("%s/../../datasets/opencorpora/opencorpora.sent.train.ru", System.getProperty("user.dir"));
//        try {
//            AbbreviationExtractorApp extractor = new AbbreviationExtractorApp();
//            extractor.extract(path, "UTF-8");
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
        
        String path = String.format("%s/../../resources/abbreviations/abbrev-gold.txt", System.getProperty("user.dir"));
        try {
            AbbreviationExtractorApp extractor = new AbbreviationExtractorApp();
            extractor.collate(path, "UTF-8");
        } catch(Exception e) {
        }
    }
}
