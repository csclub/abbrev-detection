package org.csclub.abbrev.connectors;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
        
        List<Sentence> sentences = new ArrayList();
        Set<String> lines = new HashSet();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), fileEncoding));
        String line = reader.readLine();
        
        while (null != line) {
            if (false == lines.contains(line)) {
                lines.add(line);
                sentences.add(new Sentence(line));
            }
            line = reader.readLine();
        }
        
        return new Corpus(sentences, (CorpusMetadata)null);
    }
     
}
