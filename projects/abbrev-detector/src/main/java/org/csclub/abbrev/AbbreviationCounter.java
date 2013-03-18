/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev;

import java.io.PrintStream;
import java.util.List;


/**
 *
 * @author serebrya
 */
public interface AbbreviationCounter {
    public void onNewAbbreviations(final List<Abbreviation> abbreviations);
    public void corpusProcessComplete();
    public void print(PrintStream ps);
}
