/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.evaluation;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.csclub.abbrev.Abbreviation;

/**
 *
 * @author Sergey Serebryakov
 */
public class Evaluator {
    
    private Set<String> goldAbbreviations;
    
    public Evaluator(List<Abbreviation> abbreviations) {
        goldAbbreviations = getTextForms(abbreviations);
    }
    
    public ConfusionMatrix evaluate(List<Abbreviation> abbreviations) {
        Set<String> actualAbbreviations = getTextForms(abbreviations);
        
        // abbreviatinos from goldAbbreviations that are not in actualAbbreviations
        Set<String> falseNegativesAbbreviations = Sets.difference(goldAbbreviations, actualAbbreviations);
        // abrbeviatinos that are in actualAbbreviations but not in goldAbbreviations
        Set<String> falsePositivesAbbreviations = Sets.difference(actualAbbreviations, goldAbbreviations);
        // abbreviations that are both in actualAbbreviations and goldAbbreviations
        Set<String> truePositivesAbbreviations = Sets.intersection(goldAbbreviations, actualAbbreviations);
        
        return new ConfusionMatrix( truePositivesAbbreviations.size(), falsePositivesAbbreviations.size(), falseNegativesAbbreviations.size() );
    }
    
    private Set<String> getTextForms(List<Abbreviation> abbreviations) {
        Set<String> textForms = Sets.newHashSet();
        for (Abbreviation abbreviation: abbreviations) {
            textForms.add(abbreviation.getAbbrevText());
        }
        return textForms;
    }
}
