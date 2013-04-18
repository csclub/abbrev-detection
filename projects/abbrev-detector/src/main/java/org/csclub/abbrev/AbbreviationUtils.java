package org.csclub.abbrev;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 *
 * @author Sergey Serebryakov
 */
public class AbbreviationUtils {

    public static String[] tokenize(String sentence) {
        return sentence.split("[\\s()\"«»\\[\\]]");
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
