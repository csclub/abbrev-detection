package org.csclub.abbrev.impl;

import java.util.ArrayList;
import java.util.List;
import org.csclub.abbrev.Abbreviation;

/**
 *
 * Pruning candidates on abbreviations by share, which builds by marked(golden)
 * abbreviations.
 *
 * @author Fedor Amosov
 */
public class Delimiter {
    public static double shareOfAbbreviations;
    
    /**
     * Pruning candidates on abbreviations by calculated share.
     *
     * @param abbreviations is list of abbreviations from big text.
     *
     * @return list of probably abbreviations from argument abbreviations. 
     */
    public static List<Abbreviation> probablyAbbreviations(List<Abbreviation> abbreviations) {
        List<Abbreviation> result = new ArrayList<>();
        for (int i = 0; i < abbreviations.size(); ++i) {
            if (i <= shareOfAbbreviations * abbreviations.size()) {
                result.add(abbreviations.get(i));
            }
            else {
                break;
            }
        }
        return result;
    }
            
    /**
     * Obtaining the proportion of abbreviations from all sorted marked candidates.
     *
     * @param goldAbbreviations is list of sorted by dectreasing of count and marked 
     * candidates.
     */
    public static void setShareOfAbbreviations(List<Abbreviation> goldAbbreviations) {
        double[] f = new double[goldAbbreviations.size()];
        for (int i = 0; i < f.length; ++i) {
            switch (goldAbbreviations.get(i).getAbbrevState().toString()) {
                case "+":
                    f[i] = 1.0;
                    
                break;
                case "-":
                    f[i] = 0.0;
                    
                break;
                default:
                    f[i] = 0.5;
                break;
            }
        }
        shareOfAbbreviations = firstPartShare(f);
    }

    /**
     * Obtaining the portion of first approximately constant elements in the
     * sequence.
     *
     * @param f is sequence of double values.
     *
     * @return number between 0 and 1, the portion of first similar elements.
     */
    private static double firstPartShare(double[] f) {
        int windowSize = (int) Math.sqrt(f.length) + 1;

        double windowAverage = 0;
        for (int i = 0; i < f.length; ++i) {
            windowAverage += f[i];
        }
        windowAverage = windowSize * windowAverage / f.length;

        double windowSum = 0;
        for (int i = 0; i < windowSize; ++i) {
            windowSum += f[i];
        }

        double[] smooth = new double[f.length - windowSize];
        for (int i = 0; i < smooth.length; ++i) {
            smooth[i] = windowSum;
            windowSum -= f[i];
            windowSum += f[i + windowSize];
        }

        int l = 0, r = 0;
        for (int i = 1; i < smooth.length; ++i) {
            if ((smooth[i - 1] - windowAverage) * (smooth[i] - windowAverage) < 0) {
                if (l == 0) {
                    l = i;
                }
                r = i;
            }
        }
        //System.out.println(l + " " + r);
        return (l + r + windowSize) / (2.0 * f.length);
    }
}
