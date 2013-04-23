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
public class AbbreviationEvaluator <E extends Abbreviation> extends Evaluator{
    
    private Set<String> goldAbbreviations;
    
    public AbbreviationEvaluator(List<E> abbreviations) {
        goldAbbreviations = getTextForms(abbreviations, true);
    }
    
    public ConfusionMatrix evaluate(List<E> abbreviations) {
        Set<String> actualAbbreviations = getTextForms(abbreviations, false);
        return evaluate(goldAbbreviations, actualAbbreviations);
    }
    
    private Set<String> getTextForms(List<E> abbreviations, boolean goldStandard) {
        Set<String> textForms = Sets.newHashSet();
        for (Abbreviation abbreviation: abbreviations) {
            if (goldStandard) {
                if (abbreviation.isValid()) {
                    textForms.add(abbreviation.getAbbrevText());
                }
            } else {
                textForms.add(abbreviation.getAbbrevText());
            }
        }
        return textForms;
    }
}
