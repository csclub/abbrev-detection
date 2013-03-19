/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
    
    public void toTextFile(String fileName, String encoding, List<Abbreviation> abbreviations, int maxContextsCount) throws IOException {
        Path filePath = Paths.get(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.forName(encoding))) {
            for(Abbreviation abbrev : abbreviations){
                writer.write(abbrev.toString(maxContextsCount));
                writer.newLine();
            }
        }
    }
    
    public void toBinaryFile(String fileName, List<Abbreviation> abbreviations) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))) {
            oos.writeObject(abbreviations);
        }
    }
    
    public List<Abbreviation> fromTextFile(String fileName, String encoding) {
        return null;
    }
    
    public List<Abbreviation> fromBinaryFile(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)));
        List<Abbreviation> abbreviations = (List<Abbreviation>)ois.readObject();
        return abbreviations;
    }
    
}
