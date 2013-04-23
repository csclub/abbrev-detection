/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.algorithms.tba;

import java.util.List;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.Sentence;

/**
 *
 * @author serebrya
 */
public interface AbbreviationExtractor<E extends Abbreviation> {
    public List<E> extract(Sentence sentence);
}
