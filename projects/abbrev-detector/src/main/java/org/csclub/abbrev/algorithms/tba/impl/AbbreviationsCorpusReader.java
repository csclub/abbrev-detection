/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.algorithms.tba.impl;

import java.util.List;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.Serializer;
import org.csclub.abbrev.algorithms.tba.CorpusAbbreviation;
import org.csclub.abbrev.connectors.CorpusReader;
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
        List<CorpusAbbreviation> goldAbbreviations = Serializer.fromTextFile(fileName, fileEncoding, CorpusAbbreviation.class);
        return new Corpus(null, new AbbreviationsMetadata(goldAbbreviations));
    }
}
