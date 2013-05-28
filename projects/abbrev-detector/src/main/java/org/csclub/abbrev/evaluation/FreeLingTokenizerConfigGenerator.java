/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.evaluation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Sergey Serebryakov
 */
public class FreeLingTokenizerConfigGenerator {
    
    public static void main(String [] args) throws FileNotFoundException, IOException {
        String abbreviationFilename = "C:\\work\\projects\\collaboration\\csclub\\abbrev-detection\\datasets\\brown-corpus\\abbreviations\\brown-likelihood.txt";
        String freelinkTokenizerTemplateFilename = "C:\\work\\projects\\collaboration\\csclub\\abbrev-detection\\datasets\\brown-corpus\\experiments\\FreeLing\\config\\precision-recall\\template\\tokenizer-template.dat";
        String outputDirectory = "C:\\work\\projects\\collaboration\\csclub\\abbrev-detection\\datasets\\brown-corpus\\experiments\\FreeLing\\config\\precision-recall\\configs\\";
        
        // read template into memory
        List<String> freelingConfig = IOUtils.readLines(new FileInputStream(freelinkTokenizerTemplateFilename), "UTF-8");
        // read abbreviations that should be sorted accordingly
        List<String> abbreviations = IOUtils.readLines(new FileInputStream(abbreviationFilename), "UTF-8");
        
        freelingConfig.add("<Abbreviations>");
        for (int i=0; i<abbreviations.size(); i++) {
            String [] abbrevWeight = abbreviations.get(i).split("\t");
            freelingConfig.add(abbrevWeight[0].trim().toLowerCase());
            
            freelingConfig.add("</Abbreviations>");
            IOUtils.writeLines(freelingConfig, "\n", new FileOutputStream(outputDirectory + String.format("tokenizer_%d.dat", i+1)), "UTF-8");
            freelingConfig.remove(freelingConfig.size()-1);
        }
    }
    
}
