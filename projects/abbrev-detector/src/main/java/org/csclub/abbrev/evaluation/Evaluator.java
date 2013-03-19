/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.evaluation;

import com.google.common.collect.Sets;
import java.util.Set;

/**
 *
 * @author Sergey Serebryakov
 */
public class Evaluator {
    
    protected ConfusionMatrix evaluate(Set<String> goldStandard, Set<String> actual) {
        // abbreviatinos from goldAbbreviations that are not in actualAbbreviations
        Set<String> falseNegativesAbbreviations = Sets.difference(goldStandard, actual);
        // abrbeviatinos that are in actualAbbreviations but not in goldAbbreviations
        Set<String> falsePositivesAbbreviations = Sets.difference(actual, goldStandard);
        // abbreviations that are both in actualAbbreviations and goldAbbreviations
        Set<String> truePositivesAbbreviations = Sets.intersection(goldStandard, actual);
        
        return new ConfusionMatrix( truePositivesAbbreviations.size(), falsePositivesAbbreviations.size(), falseNegativesAbbreviations.size() );
    }
    
}
