package org.csclub.abbrev.connectors;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.CorpusMetadata;
import org.csclub.abbrev.Sentence;
import org.csclub.abbrev.impl.ConfigurationParameter;
import org.csclub.abbrev.impl.Configuration;
import java.util.Set;
import java.util.HashSet;
import java.util.Map.Entry;

/**
 *
 * @author Sergey Serebryakov
 * 
 * Reads textual file line by line. Each line should contain only one sentence.
 * Can be initialized as follows:
 */
public class SentPerLineCorpusReader extends CorpusReader {
    /** full path to a file */
    @ConfigurationParameter(name="FileName")
    private String fileName;
    /** file encoding */
    @ConfigurationParameter(name="FileEncoding", defaultValue="UTF-8")
    private String fileEncoding;
    
    public void setFileName(String fileName) { this.fileName = fileName; }
    public void setFileEncoding(String fileEncoding) { this.fileEncoding = fileEncoding; }
    
    @Override
    public void init(Configuration config) throws Exception {
        super.init(config);
    }
    
    @Override
    public Corpus read() throws Exception {
        LineIterator lineIterator = IOUtils.lineIterator(new FileInputStream(fileName), fileEncoding);
        
        Set<String> lines = new HashSet();
        while (lineIterator.hasNext()) {
            lines.add(lineIterator.nextLine());
        }
        
        List<Sentence> sentences = new ArrayList();
        for (String line : lines) {
            sentences.add(new Sentence(line));
        }
        
        return new Corpus(sentences, (CorpusMetadata)null);
    }
     
}
