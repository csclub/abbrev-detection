package org.csclub.abbrev;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.csclub.abbrev.impl.AbbExtractor;
import org.csclub.abbrev.impl.AbbreviationCounter_impl;
import org.csclub.abbrev.impl.AbbreviationExtractor_impl;
import org.csclub.abbrev.impl.TrieAbbreviationCounter;

/**
 * Hello world!
 *
 */
public class AbbreviationExtractorApp  {
    
    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    public AbbreviationExtractorApp () {
        abbrevCounter = new /*TrieAbbreviationCounter()*/ AbbreviationCounter_impl();
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
    
    public static void main( String[] args ) {
        final String path = String.format("%s/../../datasets/opencorpora/opencorpora.sent.train.ru", System.getProperty("user.dir"));
        try {
            AbbreviationExtractorApp extractor = new AbbreviationExtractorApp ();
            extractor.extract(path, "UTF-8");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
