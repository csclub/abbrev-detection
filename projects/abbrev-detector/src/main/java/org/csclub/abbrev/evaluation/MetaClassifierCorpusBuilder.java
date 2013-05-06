/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.evaluation;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.csclub.abbrev.Abbreviation;

/**
 *
 * @author Sergey Serebryakov
 */
public class MetaClassifierCorpusBuilder { 
    
    private Set<String> goldAbbreviations;
    private Set<String> allAbbreviations;
    private List<String> classifiersNames;
    private Map<String, Set<String>> classifiers;
    
    public MetaClassifierCorpusBuilder(List<Abbreviation> goldAbbreviations) {
        this.classifiers = new HashMap<> ();
        this.classifiersNames = new ArrayList<> ();
        this.goldAbbreviations = new HashSet<> ();
        this.allAbbreviations = new HashSet<> ();
        for (Abbreviation abbreviation : goldAbbreviations) {
            this.goldAbbreviations.add(abbreviation.getAbbrevText());
        }
        allAbbreviations.addAll(this.goldAbbreviations);
    }
    
    public void addClassifier(String classifierName, List<Abbreviation> abbreviations) {
       
        classifiersNames.add(classifierName);
        Set<String> classifierDecisions = new HashSet<> ();
        for (Abbreviation abbreviation : abbreviations) {
            classifierDecisions.add(abbreviation.getAbbrevText());
        }
        classifiers.put(classifierName, classifierDecisions);
        allAbbreviations.addAll(classifierDecisions);
    }
    
    public void buildARFF(String fileName, String encoding) throws FileNotFoundException, UnsupportedEncodingException, IOException {
        FileOutputStream fos = new FileOutputStream( fileName );
        OutputStreamWriter osw = new OutputStreamWriter (fos, encoding);
        //
        osw.write("@RELATION abbrev-extractors\n\n\n");
        //
        for (String classifierName : classifiersNames) {
            osw.write(String.format("@ATTRIBUTE %s {0, 1}\n", classifierName));
        }
        osw.write("@ATTRIBUTE class {0, 1}\n\n\n");
        //
        osw.write("@DATA\n");
        for (String abbreviation : allAbbreviations) {
            for (String classifierName : classifiersNames) {
                if (classifiers.get(classifierName).contains(abbreviation)) {
                    osw.write("1");
                } else {
                    osw.write("0");
                }
                osw.write(",");
            }
            if (goldAbbreviations.contains(abbreviation)) {
                 osw.write("1");
            } else {
                 osw.write("0");
            }
            osw.write("\n");
        }
        //
        osw.close();
    }
            
}
