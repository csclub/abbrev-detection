/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
    
    public List<Abbreviation> fromTextFile(String fileName, String encoding) throws IOException {
        List<Abbreviation> result = new ArrayList();
        try (Scanner reader = new Scanner(new FileInputStream(fileName), encoding)) {
            while (reader.hasNextLine()) {
            
                String s = reader.next();
                Abbreviation.AbbrevState state = Abbreviation.AbbrevState.Unknown;
                if (s.equals("+")) {
                    state = Abbreviation.AbbrevState.True;
                }
                if (s.equals("-")) {
                    state = Abbreviation.AbbrevState.False;
                }
                
                int count = reader.nextInt();
                String text = reader.next();
                
                Abbreviation cur = new Abbreviation(state, count, text); 
                                    
                String contextLine = reader.nextLine().trim();
                String[] contexts = contextLine.substring(1, contextLine.length() - 1).split("', '");
                for (int i = 0; i < contexts.length; ++i) {
                    cur.addAbbrevContext(contexts[i]);
                }
                result.add(cur);
            }
        }
        return result;
    }
    
    public List<Abbreviation> fromBinaryFile(String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)));
        List<Abbreviation> abbreviations = (List<Abbreviation>)ois.readObject();
        return abbreviations;
    }
    
}
