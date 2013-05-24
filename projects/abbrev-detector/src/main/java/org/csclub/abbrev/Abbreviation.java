package org.csclub.abbrev;

import java.io.Serializable;

/**
 *
 * @author Sergey Serebryakov
 * 
 * Several questions to answer: (1) Better way to store contexts and (2) Should we store only unique contexts
 */
public class Abbreviation implements Serializable, Comparable<Abbreviation> {
    
    /** full text of the abbreviation with period at the end ('etc.', 'e.g.', 'dlr.' ...) */
    protected String abbrevText;
    
    public Abbreviation() {
    }
    
    public Abbreviation(String abbrevText) {
        this.abbrevText = abbrevText;
    }
    
    public void setAbbrevText(String abbrevText) { this.abbrevText = abbrevText; }
    public String getAbbrevText() { return abbrevText; }

    public boolean isValid() { return true; }
    
    @Override
    public String toString() {
        return abbrevText;
    }
    
    public static Abbreviation fromString(String str) {
        return new Abbreviation(str);
    }
    
    @Override
    public int compareTo(Abbreviation abbrev) {
        return abbrevText.compareTo(abbrev.getAbbrevText());
    }
}