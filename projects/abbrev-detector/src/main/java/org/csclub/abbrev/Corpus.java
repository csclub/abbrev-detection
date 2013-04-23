package org.csclub.abbrev;

import java.util.List;

/**
 *
 * @author Sergey Serebryakov
 */
public class Corpus {
    
    private List<Sentence> sentences;
    private CorpusMetadata metadata;
 
    
    public Corpus(List<Sentence> sentences, CorpusMetadata metadata) {
        setSentences(sentences);
        setMetadata(metadata);
    }
    
    public List<Sentence> getSentences() { return sentences; }
    public CorpusMetadata getMetadata() { return metadata; }
    
    public final void setSentences(List<Sentence> sentences) { this.sentences = sentences; }
    public final void setMetadata(CorpusMetadata metadata) { this.metadata = metadata; }
}
