package org.csclub.abbrev;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

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
                tokens.add(tempTokens[i]);
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
}
