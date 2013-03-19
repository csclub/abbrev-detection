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
public class AbbreviationEvaluator extends Evaluator{
    
    private Set<String> goldAbbreviations;
    
    public AbbreviationEvaluator(List<Abbreviation> abbreviations) {
        goldAbbreviations = getTextForms(abbreviations);
    }
    
    public ConfusionMatrix evaluate(List<Abbreviation> abbreviations) {
        Set<String> actualAbbreviations = getTextForms(abbreviations);
        return evaluate(goldAbbreviations, actualAbbreviations);
    }
    
    private Set<String> getTextForms(List<Abbreviation> abbreviations) {
        Set<String> textForms = Sets.newHashSet();
        for (Abbreviation abbreviation: abbreviations) {
            textForms.add(abbreviation.getAbbrevText());
        }
        return textForms;
    }
}
