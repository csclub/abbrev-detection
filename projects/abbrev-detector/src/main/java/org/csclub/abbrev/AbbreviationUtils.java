package org.csclub.abbrev;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.csclub.abbrev.algorithms.tba.CorpusAbbreviation;

/**
 *
 * @author Sergey Serebryakov
 */
public class AbbreviationUtils {

    public static String PERIOD = ".";
    
    public static List<String> tokenize(String sentence) {
        String[] tempTokens = sentence.split("[\\s()\"«»\\[\\]?:,;!]");
        List<String> tokens = new ArrayList();
        for (int i = 0; i < tempTokens.length; ++i) {
            if (!tempTokens[i].isEmpty()) {
                tokens.add(tempTokens[i].replaceAll("\\.+", "."));
            }
        }
        
        int b = tokens.size() - 1;
        int pb = tokens.size() - 2;
        int spb = sentence.length() - 2;
        if (pb >= 0 && spb >= 0) {
            if (tokens.get(b).equals(PERIOD) && sentence.charAt(spb) == ' ') {
                tokens.set(pb, tokens.get(pb) + PERIOD);
                tokens.remove(b);
            }
        }
        
        return tokens;
    }
    
    public static List<String> tokenize(Corpus corpus) {
        List<String> tokens = new ArrayList();
        for (Sentence sentence : corpus.getSentences()) {
            for (String token : sentence.getTokens()) {
                token = token.replaceAll("\\.+", ".");
                if (token.endsWith(".")) {
                    if (token.length() > 1) {
                        tokens.add(token.substring(0, token.length() - 1));
                    }
                    tokens.add(".");
                } else {
                    tokens.add(token);
                }
            }
        }
        return tokens;
    }
    
    public static Object createClassInstance(String className) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        @SuppressWarnings("unchecked")
        Class<? extends Object> klass = (Class<? extends Object>)Class.forName(className);
        Constructor<? extends Object> constructor = klass.getConstructor();
        return constructor.newInstance();
    }
    
    public static <E extends Abbreviation> void print(List<E> abbreviations) {
        for(E abbrev : abbreviations){
            System.out.println(abbrev.toString());
        }
    }
    
    /** method that probably will be deleted soon */
    public static void convertCorpusAbbreviationsToAbbreviations(final String intputFile, final String outputFile, final String encoding) throws IOException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<CorpusAbbreviation> corpusAbbreviations = Serializer.fromTextFile(intputFile, encoding, CorpusAbbreviation.class);
        List<Abbreviation> abbreviations = new ArrayList<> ();
        for (CorpusAbbreviation corpusAbbreviation : corpusAbbreviations) {
            if (corpusAbbreviation.isValid()) {
                abbreviations.add(new Abbreviation(corpusAbbreviation.getAbbrevText()));
            }
        }
        Serializer.toTextFile(outputFile, encoding, abbreviations);
    }
    
}
