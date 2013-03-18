/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author Sergey Serebryakov
 */
public class Serializer {
    
    public void toFile(String fileName, String encoding, List<Abbreviation> abbreviations, int maxContextsCount) throws IOException {
        Path filePath = Paths.get(fileName);
        BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.forName(encoding));
        for(Abbreviation abbrev : abbreviations){
            writer.write(abbrev.toString(maxContextsCount));
            writer.newLine();
        }
    }
    
    public List<Abbreviation> fromFile(String fileName, String encoding) {
        return null;
    }
}
