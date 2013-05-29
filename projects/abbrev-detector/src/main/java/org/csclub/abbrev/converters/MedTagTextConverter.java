/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.converters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

/**
 *
 * @author Sergey Serebryakov
 */
public class MedTagTextConverter {
    
    public final static String TAG_ANNOTATION = ">>ANNOTATION";
    public final static String TAG_ANNOTATION_TYPE = "ANNOTATION:";
    public final static String TAG_TEXT = "TEXT:";
    
    public final static String ANNOTATION_SENTENCE = "S";
    
    List<String> medTagCorpus;
    List<String> medTagSents = new ArrayList<> ();
    
    List<String> getSentences() { return medTagSents; }
    
    public MedTagTextConverter() {
        
    }
    
    public void convert(String inputFile) throws FileNotFoundException, IOException {
        medTagSents = new ArrayList<> ();
        LineIterator iterator = IOUtils.lineIterator(new FileReader(inputFile));
        while (iterator.hasNext()) {
            String line = iterator.nextLine();
            if (line.startsWith(TAG_ANNOTATION)) {
                String type = getValue(iterator, TAG_ANNOTATION_TYPE);
                if (false == type.equals(ANNOTATION_SENTENCE)) {
                    continue;
                }
                String sent = getValue(iterator, TAG_TEXT);
                medTagSents.add(sent);
            }
        }
    }
    
    public String getValue(LineIterator iterator, String key) {
        while (iterator.hasNext()) {
            String line = iterator.nextLine().trim();
            if (line.equals("")) {
                return "";
            } else if (line.startsWith(key)) {
                return line.substring(key.length()).trim();
            }
        }
        return "";
    }
    
    public static void main(String [] args) throws FileNotFoundException, IOException {
        String inputFile = "C:/archive/storage/datasets/medical/medtag/data/medtag.txt";
        String outputFile = "C:/archive/storage/datasets/medical/medtag/data/medtag-sents.txt";
        
        MedTagTextConverter converter = new MedTagTextConverter ();
        converter.convert(inputFile);
        
        List<String> sents = converter.getSentences();
        /*
        for (String sent : sents) {
            System.out.println(sent);
        }
        */
        IOUtils.writeLines(sents, "\n", new FileOutputStream(outputFile), "UTF-8");
        
    }
            
}
