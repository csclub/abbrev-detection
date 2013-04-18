/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.connectors;

import java.util.List;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Serializer;
import org.csclub.abbrev.impl.ConfigurationParameter;
import org.csclub.abbrev.impl.Configuration;

/**
 *
 * @author Sergey Serebryakov
 * 
 */
public class AbbreviationsCorpusReader extends CorpusReader {
    
     /** full path to a file */
    @ConfigurationParameter(name="FileName", mandatory = true)
    private String fileName;
    /** file encoding */
    @ConfigurationParameter(name="FileEncoding", defaultValue="UTF-8")
    private String fileEncoding;
    
    @Override
    public void init(Configuration config) throws Exception {
        super.init(config);
        
    }
    
    @Override
    public Corpus read() throws Exception {
        List<Abbreviation> goldAbbreviations = Serializer.fromTextFile(fileName, fileEncoding);
        return new Corpus(null, goldAbbreviations);
    }
}
