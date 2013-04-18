/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.connectors;

import junit.framework.TestCase;
import org.csclub.abbrev.impl.Configuration;

/**
 *
 * @author serebrya
 */
public class FileConnectorTest extends TestCase {
    
    public FileConnectorTest(String name) {
        super(name);
    }
     
    public void testPrecisionAndRecall() {
        
        boolean exceptionThrown;
        
        SentPerLineCorpusReader fc = new SentPerLineCorpusReader ();
        try {
            exceptionThrown = false;
            fc.init(new Configuration((String[])null));
        } catch(Exception e) {
            exceptionThrown = true;
        }
        assertEquals(true, exceptionThrown);
    }
}
