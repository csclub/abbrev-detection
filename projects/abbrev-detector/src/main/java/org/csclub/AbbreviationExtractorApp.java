package org.csclub;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.csclub.impl.AbbExtractor;
import org.csclub.impl.TrieAbbreviationCounter;

/**
 * Hello world!
 *
 */
public class AbbreviationExtractorApp  {
    
    private AbbreviationCounter abbrevCounter;
    private AbbreviationExtractor abrbevExtractor;
    
    public AbbreviationExtractorApp () {
        abbrevCounter = new TrieAbbreviationCounter();
        abrbevExtractor = new AbbExtractor();
    }
    
    public void extract(String fileName, String encoding) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), encoding);
        BufferedReader br = new BufferedReader(isr);
        
        String strLine;
        while ((strLine = br.readLine()) != null)   {
            List<String> abbreviations = abrbevExtractor.extract(strLine);
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
