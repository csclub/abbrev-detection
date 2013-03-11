/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.impl;

import java.util.ArrayList;
import java.util.List;
import org.csclub.AbbreviationExtractor;

/**
 *
 * @author Sergey Serebryakov
 * A naive implementation of the abbreviation extractor component.
 * Return tokens that ends with period and are located in the middle of the sentence.
 * Do not modify it. Rather, make a new class with meaningful name and some logic into it.
 */
public class AbbreviationExtractor_impl implements AbbreviationExtractor {
    
    public List<String> extract(final String sentence) {
        String [] tokens = sentence.split("[ \t\n]+");
        List<String> abbreviations = new ArrayList<String> ();
        for (int i=0; i<tokens.length - 1; i++) {
            if (tokens[i].endsWith(".")) {
                abbreviations.add(tokens[i]);
            }
        }
        return abbreviations;
    }
    
}
