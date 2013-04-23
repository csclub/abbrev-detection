/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.algorithms.tba;

import java.io.PrintStream;
import java.util.List;
import org.csclub.abbrev.Abbreviation;


/**
 *
 * @author serebrya
 */
public interface AbbreviationCounter <E extends Abbreviation> {
    public void onNewAbbreviations(final List<E> abbreviations);
    public void corpusProcessComplete();
    public void print(PrintStream ps);
    public List<E> getSortedAbbreviations();
}
