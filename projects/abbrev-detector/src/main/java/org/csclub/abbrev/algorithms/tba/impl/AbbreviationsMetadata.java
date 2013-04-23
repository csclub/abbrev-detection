/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.algorithms.tba.impl;

import java.util.List;
import org.csclub.abbrev.CorpusMetadata;
import org.csclub.abbrev.algorithms.tba.CorpusAbbreviation;

/**
 *
 * @author Sergey Serebryakov
 */
public class AbbreviationsMetadata implements CorpusMetadata {
    
    private List<CorpusAbbreviation> abbreviations;
    
    public AbbreviationsMetadata(List<CorpusAbbreviation> abbreviations) {
        this.abbreviations = abbreviations;
    }
    
    public List<CorpusAbbreviation> getAbbreviations() { return abbreviations; }
}
