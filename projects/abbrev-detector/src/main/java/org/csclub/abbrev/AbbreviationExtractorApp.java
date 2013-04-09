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
        abbrevCounter = new TrieAbbreviationCounter() /*AbbreviationCounter_impl()*/;
        abrbevExtractor = new /*AbbExtractor()*/ AbbreviationExtractor_impl();
    }
    
    public void extract(String fileName, String encoding) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), encoding);
        BufferedReader br = new BufferedReader(isr);
        
        String strLine;
        while ((strLine = br.readLine()) != null)   {
            List<Abbreviation> abbreviations = abrbevExtractor.extract(strLine);
            abbrevCounter.onNewAbbreviations(abbreviations);
        }
        abbrevCounter.corpusProcessComplete();
        abbrevCounter.print(System.out);
    }
    
    public void collate(String fileName, String encoding) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        List<Abbreviation> goldAbbreviations = new Serializer().fromTextFile(fileName, encoding);
        
        abbrevCounter.onNewAbbreviations(goldAbbreviations);
        abbrevCounter.corpusProcessComplete();
        
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
        ConfusionMatrix matrix = evaluator.evaluate(Delimiter.probablyAbbreviations(goldAbbreviations));
        
        System.out.println("Precision:\t" + matrix.getPrecision());
        System.out.println("Recall:\t\t" + matrix.getRecall());
        System.out.println("F1 Measure:\t" + matrix.getF1Measure());
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
