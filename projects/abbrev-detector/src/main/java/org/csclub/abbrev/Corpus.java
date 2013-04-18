package org.csclub.abbrev;

import java.util.List;

/**
 *
 * @author Sergey Serebryakov
 */
public class Corpus {
    
    private List<Sentence> sentences;
    private List<Abbreviation> abbreviations;
    
    public Corpus(List<Sentence> sentences, List<Abbreviation> abbreviations) {
        setSentences(sentences);
        setAbbreviations(abbreviations);
    }
    
    public List<Sentence> getSentences() { return sentences; }
    public List<Abbreviation> getAbbreviations() { return abbreviations; }
    
    public final void setSentences(List<Sentence> sentences) { this.sentences = sentences; }
    public final void setAbbreviations(List<Abbreviation> abbreviations) { this.abbreviations = abbreviations; }
}
