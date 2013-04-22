package org.csclub.abbrev;

import java.util.List;

/**
 *
 * @author Sergey Serebryakov
 */
public class Sentence {
    
    private String sentence;
    private List<String> tokens;
    
    public Sentence(String sentence) {
        this.sentence = sentence;
        this.tokens = AbbreviationUtils.tokenize(sentence);
    }
    
    public Sentence(String sentence, List<String> tokens) {
        this(sentence);
        this.tokens = tokens;
    }
 
    public String getSentence() { return sentence; }
    public List<String> getTokens() { return tokens; }
}
