package org.csclub.abbrev;

import java.io.Serializable;
import org.apache.commons.lang3.ObjectUtils;

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
        if (null == abbrev) {
            //since this is not null, it is always greater than null
            return 1;
        }
        return ObjectUtils.compare(abbrevText, abbrev.getAbbrevText());
    }
}