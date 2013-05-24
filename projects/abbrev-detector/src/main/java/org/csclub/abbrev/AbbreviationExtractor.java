/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev;

import org.csclub.abbrev.impl.Configuration;

/**
 *
 * @author Sergey Serebryakov
 */
public class AbbreviationExtractor {
 
    public static void main(String [] args) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        if (args.length != 2) {
            System.err.println("Usage: AbbreviationExtractor config-file-name config-file-encoding");
            return;
        }
        try {
            Configuration config = Configuration.loadFromFile(args[0], args[1]);
            AbbreviationExtractorEngine engine= new AbbreviationExtractorEngine ();
            engine.init(config);
            engine.run();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
}
