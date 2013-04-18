package org.csclub.abbrev;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Sergey Serebryakov
 * 
 * Several questions to answer: (1) Better way to store contexts and (2) Should we store only unique contexts
 */
public class Abbreviation implements Serializable, Comparable {
    
    public static enum AbbrevState {
        True ("+"),
        False ("-"),
        Unknown ("?");
        
        private final String state;
        
        private AbbrevState(final String state) {
            this.state = state;
        }
        
        @Override
        public String toString() {
            return state;
        }
    }
    
    /** full text of the abbreviation with period at the end ('etc.', 'e.g.', 'dlr.' ...) */
    private String abbrevText;
    /** number of times this abbreviation has been hit (found in the documents) */
    private int abbrevCount;
    /** list of abbreviation contexts */
    private List<String> abbrevContexts = new ArrayList();
    /** state of the abbreviation (true abbreviation, false abbreviation or hard to say (unknown)) */
    private AbbrevState abbrevState;
    
    public Abbreviation() {
        abbrevCount = 0;
        this.abbrevState = AbbrevState.Unknown;
    }
    
    public Abbreviation(String abbrevText) {
        this.abbrevText = abbrevText;
        this.abbrevCount = 1;
        this.abbrevState = AbbrevState.Unknown;
    }
    
    public Abbreviation(String abbrevText, AbbrevState abbrevState) {
        this.abbrevText = abbrevText;
        this.abbrevCount = 1;
        this.abbrevState = abbrevState;
    }
    
    public Abbreviation(AbbrevState abbrevState, int abbrevCount, String abbrevText) {
        this.abbrevText = abbrevText;
        this.abbrevCount = abbrevCount;
        this.abbrevState = abbrevState;
    }
     
    public void setAbbrevText(String abbrevText) {
        this.abbrevText = abbrevText;
    }
     
    public void addAbbrevContext(String abbrevContext) {
        this.abbrevContexts.add(abbrevContext);
    }
    public void addAbbrevContexts(List<String> abbrevContexts) {
        this.abbrevContexts.addAll(abbrevContexts);
    }
    
    
    public String getAbbrevText() { return abbrevText; }
    public int getAbbrevCount() { return abbrevCount; }
    public List<String> getAbbrevContexts() { return abbrevContexts; }
    public AbbrevState getAbbrevState() { return abbrevState; }
    
    public void incrementCounter() { this.abbrevCount ++; }
    public void incrementCounter(int count) { 
        this.abbrevCount += count; 
    }
    
    
    public String toString(int maxContextsCount) {
        StringBuilder sb = new StringBuilder ();
        if (maxContextsCount < 0) {
            maxContextsCount = Integer.MAX_VALUE;
        }
        for (int i=0; i< Math.min( abbrevContexts.size(), maxContextsCount); i++) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append("'").append(abbrevContexts.get(i)).append("'");
        }
        return String.format("%s\t%d\t%s\t%s", abbrevState, abbrevCount, abbrevText, sb.toString());
    }
    
    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Abbreviation)) {
            return 0;
        }
        
        Abbreviation other = (Abbreviation)o;
        
        if (getAbbrevCount() < other.getAbbrevCount()) {
            return 1;
        } 
        if (getAbbrevCount() > other.getAbbrevCount()) {
            return -1;
        }
        
        if (abbrevText.length() > other.abbrevText.length()) {
            return 1;
        }
        if (abbrevText.length() < other.abbrevText.length()) {
            return -1;
        }
        
        if (this.hashCode() < other.hashCode()) {
            return 1;
        } 
        if (this.hashCode() > other.hashCode()) {
            return -1;
        } 
        
        return 0;
    }
}