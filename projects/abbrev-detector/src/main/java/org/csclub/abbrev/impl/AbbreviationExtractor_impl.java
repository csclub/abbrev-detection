/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    
    private  Pattern simpleAbbrevPattern;
    
    
    public AbbreviationExtractor_impl() {
        simpleAbbrevPattern = Pattern.compile("(\\p{L}|[.-])*\\p{L}+[.]$");
    }
    
    /**
     *
     * @param sentence The sentence from which this method extracts candidate abbreviations
     * @return List of candidate abbreviations
     */
    @Override
    public List<Abbreviation> extract(final String sentence) {
        String[] tokens = sentence.split("[\\s()\"«»\\[\\]]");
        List<Abbreviation> abbreviations = new ArrayList<>();
        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].endsWith(".")) {
                // get abbreviation
                String abbrevText = getTokenAbbreviation(tokens[i]);
                if (null == abbrevText) {
                    continue;
                }
                Abbreviation abbrev = new Abbreviation(tokens[i]);
                // get abbreviation context
                if (i == 0 && tokens.length >= 1) {
                    abbrev.addAbbrevContext(String.format("%s %s", tokens[i], tokens[1]));
                } else if (i >= 1 && tokens.length >= 2) {
                    abbrev.addAbbrevContext(String.format("%s %s %s", tokens[i - 1], tokens[i], tokens[i + 1]));
                } 
                    
                abbreviations.add(abbrev);
            }
        }
        return abbreviations;
    }
    //
    public String getTokenAbbreviation(String token) {
        // the token might end with multiple periods (like etc...)
        String _token = token;
        if (_token.endsWith("..")) {
            _token = _token.replaceAll("[.]+$", ".");
        }
        if (token.equals(".")) {
            return null;
        }
        // extract
        Matcher matcher = simpleAbbrevPattern.matcher(_token);
        if (matcher.find()) {
            _token = matcher.group();
        } else {
            _token = null;
        }
        return _token;
    }
}
