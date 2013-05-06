package org.csclub.abbrev;

import java.util.List;

/**
 *
 * @author Sergey Serebryakov
 * 
 * If (sentence is not null), initial text of the sentence is available.
 * If (tokens is not null), some tokenizer (probably, human) tokenized the sentence.
 * 
 * Generally, following three possibilities are valid:
 *  (1) sentence is null, tokens array is not null;
 *  (2) sentence is not null, tokens array is null;
 *  (3) sentence is not null, tokens array is not null;
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
