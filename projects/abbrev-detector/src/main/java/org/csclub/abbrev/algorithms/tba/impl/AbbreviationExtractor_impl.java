package org.csclub.abbrev.algorithms.tba.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.AbbreviationUtils;
import org.csclub.abbrev.Sentence;
import org.csclub.abbrev.algorithms.tba.AbbreviationExtractor;

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
    public List<Abbreviation> extract(Sentence sentence) {
        List<String> tokens;
        if (null != sentence.getTokens()) {
            tokens = sentence.getTokens();
        } else {
            tokens = AbbreviationUtils.tokenize(sentence.getSentence());
        }
        
        List<Abbreviation> abbreviations = new ArrayList();
        for (int i = 0; i < tokens.size() - 1; i++) {
            if (tokens.get(i).endsWith(".")) {
                // get abbreviation
                String abbrevText = getTokenAbbreviation(tokens.get(i));
                if (null == abbrevText) {
                    continue;
                }
                Abbreviation abbrev = new Abbreviation(tokens.get(i));
                // get abbreviation context
                if (i == 0 && tokens.size() >= 1) {
                    abbrev.addAbbrevContext(String.format("%s %s", tokens.get(i), tokens.get(1)));
                } else if (i >= 1 && tokens.size() >= 2) {
                    abbrev.addAbbrevContext(String.format("%s %s %s", tokens.get(i - 1), tokens.get(i), tokens.get(i + 1)));
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
    //
}
