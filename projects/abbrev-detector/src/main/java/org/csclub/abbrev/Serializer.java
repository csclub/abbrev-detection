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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    
    /** each line contains single element from list  <code>abbreviations</code>*/
    public static <E extends Abbreviation> void toTextFile(final String fileName, final String encoding, final List<E> abbreviations) throws IOException {
        Path filePath = Paths.get(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.forName(encoding))) {
            for(E abbrev : abbreviations){
                writer.write(abbrev.toString());
                writer.newLine();
            }
        }
    }
    
    public static <E extends Abbreviation> List<E> fromTextFile(final String fileName, final String encoding, Class<E> klass) throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method factoryMethod = klass.getMethod("fromString", String.class);
        List<E> abbreviations = new ArrayList();
        try (Scanner reader = new Scanner(new FileInputStream(fileName), encoding)) {
            while (reader.hasNextLine()) {
                String str = reader.nextLine();
                Object abbrev = factoryMethod.invoke(null, str);
                abbreviations.add((E)abbrev);
            }
        }
        return abbreviations;
    }
    
    public static void toBinaryFile(final String fileName, final List<? extends Abbreviation> abbrevs) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)))) {
            oos.writeObject(abbrevs);
        }
    }
    
    public static List<? extends Abbreviation> fromBinaryFile(final String fileName) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)));
        Object obj = (List<Abbreviation>)ois.readObject();
        return (List<? extends Abbreviation>)obj;
    }
}
