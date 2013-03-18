/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.impl;

import java.util.ArrayList;
import java.util.List;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.AbbreviationExtractor;

/**
 *
 * @author Sergey Serebryakov
 * A naive implementation of the abbreviation extractor component.
 * Return tokens that ends with period and are located in the middle of the sentence.
 * Do not modify it. Rather, make a new class with meaningful name and some logic into it.
 */
public class AbbreviationExtractor_impl implements AbbreviationExtractor {
    
    public List<Abbreviation> extract(final String sentence) {
        String [] tokens = sentence.split("[ \t\n]+");
        List<Abbreviation> abbreviations = new ArrayList<Abbreviation> ();
        for (int i=0; i<tokens.length - 1; i++) {
            if (tokens[i].endsWith(".")) {
                Abbreviation abbrev = new Abbreviation(tokens[i]);
                if ( i == 0 && tokens.length >= 1 ) {
                    abbrev.addAbbrevContext(String.format("%s %s", tokens[i], tokens[1]));
                } else if (i >=1 && tokens.length >= 2 ) {
                    abbrev.addAbbrevContext(String.format("%s %s %s", tokens[i-1], tokens[i], tokens[i+1]));
                } 
                    
                abbreviations.add(abbrev);
            }
        }
        return abbreviations;
    }
    
}
