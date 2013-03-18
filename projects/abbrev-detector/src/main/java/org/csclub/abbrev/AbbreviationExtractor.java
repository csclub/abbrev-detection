/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev;

import java.util.List;

/**
 *
 * @author serebrya
 */
public interface AbbreviationExtractor {
    public List<Abbreviation> extract(final String sentence);
}
