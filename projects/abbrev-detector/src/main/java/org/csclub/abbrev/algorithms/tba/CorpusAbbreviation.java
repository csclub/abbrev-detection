/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.algorithms.tba;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.csclub.abbrev.Abbreviation;

/**
 *
 * @author Sergey Serebryakov
 */
public class CorpusAbbreviation extends Abbreviation {
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
     
    /** number of times this abbreviation has been hit (found in the documents) */
    private int abbrevCount;
    /** list of abbreviation contexts */
    private List<String> abbrevContexts = new ArrayList();
    /** state of the abbreviation (true abbreviation, false abbreviation or hard to say (unknown)) */
    private AbbrevState abbrevState;
    
     public CorpusAbbreviation() {
        abbrevCount = 0;
        this.abbrevState = AbbrevState.Unknown;
    }
    
    public CorpusAbbreviation(String abbrevText) {
        super(abbrevText);
        this.abbrevCount = 1;
        this.abbrevState = AbbrevState.Unknown;
    }
    
    public CorpusAbbreviation(String abbrevText, AbbrevState abbrevState) {
        super(abbrevText);
        this.abbrevCount = 1;
        this.abbrevState = abbrevState;
    }
    
    public CorpusAbbreviation(AbbrevState abbrevState, int abbrevCount, String abbrevText) {
        super(abbrevText);
        this.abbrevCount = abbrevCount;
        this.abbrevState = abbrevState;
    }
     
    public void addAbbrevContext(String abbrevContext) {
        this.abbrevContexts.add(abbrevContext);
    }
    public void addAbbrevContexts(List<String> abbrevContexts) {
        this.abbrevContexts.addAll(abbrevContexts);
    }
    
    public int getAbbrevCount() { return abbrevCount; }
    public List<String> getAbbrevContexts() { return abbrevContexts; }
    public AbbrevState getAbbrevState() { return abbrevState; }
    
    @Override
    public boolean isValid() { return (abbrevState == AbbrevState.True); }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder ();

        for (int i=0; i< abbrevContexts.size(); i++) {
            if (sb.length() != 0) {
                sb.append(", ");
            }
            sb.append("'").append(abbrevContexts.get(i)).append("'");
        }
        return String.format("%s\t%d\t%s\t%s", abbrevState, abbrevCount, abbrevText, sb.toString());
    }
    
    public static CorpusAbbreviation fromString(String str) {
         
        Scanner reader = new Scanner(new StringReader(str));
        String s = reader.next();
                
        CorpusAbbreviation.AbbrevState state = CorpusAbbreviation.AbbrevState.Unknown;
        switch (s) {
            case "+":
                state = CorpusAbbreviation.AbbrevState.True;
                break;
            case "-":
                state = CorpusAbbreviation.AbbrevState.False;
                break;
        }
                
        int count = reader.nextInt();
        String text = reader.next();
                
        CorpusAbbreviation cur = new CorpusAbbreviation(state, count, text); 
                                    
        String contextLine = reader.nextLine().trim();
        String[] contexts = contextLine.substring(1, contextLine.length() - 1).split("', '");
        for (int i = 0; i < contexts.length; ++i) {
            cur.addAbbrevContext(contexts[i]);
        }
        
        return cur;
    }
    
    public void incrementCounter() { this.abbrevCount ++; }
    public void incrementCounter(int count) { 
        this.abbrevCount += count; 
    }
    
    @Override
    public int compareTo(Abbreviation o) {
        if (!(o instanceof CorpusAbbreviation)) {
            throw new IllegalArgumentException("Abbreviation must be an instance of CorpusAbbreviation");
        }
        
        CorpusAbbreviation other = (CorpusAbbreviation)o;
        
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
