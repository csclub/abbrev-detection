/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.evaluation;

/**
 *
 * @author Sergey Serebryakov
 * 
 * Precision, recall and f1 measure are discussed here: 
 * http://en.wikipedia.org/wiki/Precision_and_recall
 */
public class ConfusionMatrix {
    
    public static int UNDEFINED = -1;
    
    private int truePositives;
    private int falsePositives;
    private int falseNegatives;
            
    public ConfusionMatrix () {
        this.truePositives = 0;
        this.falsePositives = 0;
        this.falseNegatives = 0;
    }
    
     public ConfusionMatrix (int truePositives, int falsePositives, int falseNegatives) {
        this.truePositives = truePositives;
        this.falsePositives = falsePositives;
        this.falseNegatives = falseNegatives;
    }
     
    public int getTruePositives() { 
        return truePositives; 
    }
    public int getFalsePositives() { 
        return falsePositives; 
    }
    public int getFalseNegatives() { 
        return falseNegatives; 
    }
     
    public float getPrecision() {
        float precision = UNDEFINED; 
        int retrivedAbbreviations = truePositives + falsePositives;
        if (retrivedAbbreviations > 0) {
            precision = (float) (truePositives) / retrivedAbbreviations;
        }
        return precision;
    }
     
    public float getRecall() {
        float recall = UNDEFINED; 
        int relevantAbbreviations = truePositives + falseNegatives;
        if (relevantAbbreviations > 0) {
            recall = (float) (truePositives) / relevantAbbreviations;
        }
        return recall;
    }
    
    public float getF1Measure() {
        float f1Measure = UNDEFINED;
        float precision = getPrecision();
        float recall = getRecall();
        if (precision >= 0 && recall >= 0 && precision + recall > 0) {
            f1Measure = 2 * precision * recall / (precision + recall);
        }
        return f1Measure;
    }
    
    @Override
    public String toString() {
        return String.format("p=%.3f, r=%.3f, f1=%.3f", getPrecision(), getRecall(), getF1Measure());
    }

    public void print(String info) {
        System.out.println(info);
        System.out.println("\tPrecision =\t" + getPrecision());
        System.out.println("\tRecall =\t" + getRecall());
        System.out.println("\tF1 Measure =\t" + getF1Measure());
        System.out.println();
    }
}
