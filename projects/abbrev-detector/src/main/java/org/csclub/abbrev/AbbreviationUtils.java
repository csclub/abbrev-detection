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
    
    public static Object createClassInstance(String className) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        @SuppressWarnings("unchecked")
        Class<? extends Object> klass = (Class<? extends Object>)Class.forName(className);
        Constructor<? extends Object> constructor = klass.getConstructor();
        return constructor.newInstance();
    }
    
    public static void print(List<Abbreviation> abbreviations, int maxContextsCount) {
        for(Abbreviation abbrev : abbreviations){
            System.out.println(abbrev.toString(maxContextsCount));
        }
    }
}
