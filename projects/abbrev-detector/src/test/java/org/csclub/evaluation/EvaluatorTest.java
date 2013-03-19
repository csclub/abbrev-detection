/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.evaluation;

import java.util.ArrayList;
import java.util.List;
import static junit.framework.Assert.assertEquals;
import junit.framework.TestCase;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.evaluation.ConfusionMatrix;
import org.csclub.abbrev.evaluation.AbbreviationEvaluator;

/**
 *
 * @author serebrya
 */
public class EvaluatorTest extends TestCase {
    
    public EvaluatorTest(String name) {
        super(name);
    }
    
    public void testPrecisionAndRecall() {
        List<Abbreviation> goldAbbreviations = new ArrayList<Abbreviation> ();
        goldAbbreviations.add(new Abbreviation("A"));
        goldAbbreviations.add(new Abbreviation("B"));
        goldAbbreviations.add(new Abbreviation("C"));
        goldAbbreviations.add(new Abbreviation("D"));
        goldAbbreviations.add(new Abbreviation("E"));
        goldAbbreviations.add(new Abbreviation("F"));
        goldAbbreviations.add(new Abbreviation("J"));
        goldAbbreviations.add(new Abbreviation("H"));
        
        List<Abbreviation> actualAbbreviations = new ArrayList<Abbreviation> ();
        actualAbbreviations.add(new Abbreviation("A"));
        actualAbbreviations.add(new Abbreviation("D"));
        actualAbbreviations.add(new Abbreviation("H"));
        actualAbbreviations.add(new Abbreviation("K"));
        actualAbbreviations.add(new Abbreviation("N"));
        
        AbbreviationEvaluator evaluator = new AbbreviationEvaluator(goldAbbreviations);
        ConfusionMatrix confusionMatrix = evaluator.evaluate(actualAbbreviations);
        
        assertEquals(confusionMatrix.getFalseNegatives(), 5);
        assertEquals(confusionMatrix.getFalsePositives(), 2);
        assertEquals(confusionMatrix.getTruePositives(), 3);
    }
    
}
