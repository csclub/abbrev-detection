/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev;

/**
 *
 * @author Sergey Serebryakov
 */
public class WeightedAbbreviation extends Abbreviation {
    public double weight;
        
    public WeightedAbbreviation(String abbrev, double weight) {
        super(abbrev);
        this.weight = weight;
    }
        
    @Override
    public String toString() {
        return String.format("%s\t%f", getAbbrevText(), weight);
    }

    @Override
    public int compareTo(Abbreviation _o) {
        if (_o instanceof WeightedAbbreviation) {
            WeightedAbbreviation o = (WeightedAbbreviation)_o;
            if (weight > o.weight) {
                return 1;
            } else if (weight < o.weight) {
                return -1;
            } else {
                return 0;
            }
        } else {
            throw new ClassCastException(String.format("Cannot compare this (%s) with class '%s'", this.getClass().getName(), _o.getClass().getName()));
        }
    }
}
