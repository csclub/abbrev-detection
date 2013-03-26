/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.impl;

import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author serebrya
 */
public class AbbreviationExtractor_implTest extends TestCase {
    
    public AbbreviationExtractor_implTest(String name) {
        super(name);
    }
    
    public void testGetEnglishAbbreviation() {
        List<Pair<String, String>> testCases = new ArrayList<> ();
        testCases.add( new ImmutablePair<String, String> (".", null) );
        testCases.add( new ImmutablePair<String, String> ("...", null) );
        testCases.add( new ImmutablePair<> ("etc.", "etc.") );
        testCases.add( new ImmutablePair<> ("etc...", "etc.") );
        testCases.add( new ImmutablePair<> ("(etc...", "etc.") );
        testCases.add( new ImmutablePair<String, String> ("etc).", null) );
        testCases.add( new ImmutablePair<String, String> ("3434.", null) );
        testCases.add( new ImmutablePair<> ("e.g.", "e.g.") );
        testCases.add( new ImmutablePair<> ("U.S.", "U.S.") );
        testCases.add( new ImmutablePair<> ("Mr.", "Mr.") );
        testCases.add( new ImmutablePair<> ("Bros.", "Bros.") );
         
         
         AbbreviationExtractor_impl extractor = new AbbreviationExtractor_impl ();
         for (Pair<String, String> testCase : testCases) {
             assertEquals(extractor.getTokenAbbreviation(testCase.getLeft()), testCase.getRight());
         }
     }
    
     public void testGetRussianAbbreviation() {
        List<Pair<String, String>> testCases = new ArrayList<> ();
        testCases.add( new ImmutablePair<String, String> (".", null) );
        testCases.add( new ImmutablePair<String, String> ("...", null) );
        testCases.add( new ImmutablePair<> ("млн.", "млн.") );
        testCases.add( new ImmutablePair<> ("млн...", "млн.") );
        testCases.add( new ImmutablePair<> ("(млн...", "млн.") );
        testCases.add( new ImmutablePair<String, String> ("млн).", null) );
        testCases.add( new ImmutablePair<String, String> ("3434.", null) );
        testCases.add( new ImmutablePair<> ("т.д.", "т.д.") );
        testCases.add( new ImmutablePair<> ("У.К.", "У.К.") );
        testCases.add( new ImmutablePair<> ("Г-н.", "Г-н.") );
        testCases.add( new ImmutablePair<> ("Мр.", "Мр.") );
        testCases.add( new ImmutablePair<> ("(т.2л.д.", "л.д.") );
         
         
         AbbreviationExtractor_impl extractor = new AbbreviationExtractor_impl ();
         for (Pair<String, String> testCase : testCases) {
             assertEquals(testCase.getRight(), extractor.getTokenAbbreviation(testCase.getLeft()));
         }
     }
    
}
