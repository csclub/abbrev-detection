package org.csclub.abbrev;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.csclub.abbrev.algorithms.tba.CorpusAbbreviation;
import org.csclub.abbrev.impl.Configuration;
import org.csclub.abbrev.impl.InitializationException;

/**
 *
 * @author Sergey Serebryakov
 */
public class AbbreviationUtils {

    public static final String PERIOD = ".";
    
    public static double toSeconds(long start, long stop) {
        return (double)(stop - start) / 1000000000.0;
    }
    
    
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
    //Оно нашло сакральную персону себе под стать
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
    
    public static Configuration commandLineArgsToConfiguration(String [] args) throws InitializationException {
        for (int i=0; i<args.length/2; i++) {
             if (args[2*i].startsWith("--")) {
                 args[2*i] = args[2*i].substring(2);
             } else if (args[2*i].startsWith("-")) {
                 args[2*i] = args[2*i].substring(1);
             }
         }
         return new Configuration(args);
    }
    
    /**
     * Implementation from Weka (http://grepcode.com/file/repo1.maven.org/maven2/nz.ac.waikato.cms.weka/weka-dev/3.7.5/weka/core/Utils.java)
     */
    public static String[] splitOptions(String quotedOptionString) throws Exception {
        Vector<String> optionsVec = new Vector<>();
        String str = new String(quotedOptionString);
        int i;
        
        while (true){

            //trimLeft 
            i = 0;
            while ((i < str.length()) && (Character.isWhitespace(str.charAt(i)))) i++;
            str = str.substring(i);

            //stop when str is empty
            if (str.length() == 0) break;

            //if str start with a double quote
            if (str.charAt(0) == '"'){

                //find the first not anti-slached double quote
                i = 1;
                while(i < str.length()){
                    if (str.charAt(i) == str.charAt(0)) break;
                    if (str.charAt(i) == '\\'){
                        i += 1;
                        if (i >= str.length()) 
                            throw new Exception("String should not finish with \\");
                    }
                    i += 1;
                }
                if (i >= str.length()) throw new Exception("Quote parse error.");

                //add the founded string to the option vector (without quotes)
                String optStr = str.substring(1,i);
                optStr = unbackQuoteChars(optStr);
                optionsVec.addElement(optStr);
                str = str.substring(i+1);
            } else {
                //find first whiteSpace
                i=0;
                while((i < str.length()) && (!Character.isWhitespace(str.charAt(i)))) i++;

                //add the founded string to the option vector
                String optStr = str.substring(0,i);
                optionsVec.addElement(optStr);
                str = str.substring(i);
            }
        }

        //convert optionsVec to an array of String
        String[] options = new String[optionsVec.size()];
        for (i = 0; i < optionsVec.size(); i++) {
            options[i] = (String)optionsVec.elementAt(i);
        }
        return options;
    }    
    
    /**
     * taken from Weka (http://grepcode.com/file/repo1.maven.org/maven2/nz.ac.waikato.cms.weka/weka-dev/3.7.5/weka/core/Utils.java#Utils.unbackQuoteChars%28java.lang.String%29)
     */
    public static String unbackQuoteChars(String string) {

        int index;
        StringBuffer newStringBuffer;

        // replace each of the following characters with the backquoted version
        String charsFind[]    = {"\\\\", "\\'", "\\t", "\\n", "\\r", "\\\"", "\\%", "\\u001E"};
        char   charsReplace[] = {'\\',   '\'',  '\t',  '\n',  '\r',  '"',    '%', '\u001E'};
        int pos[] = new int[charsFind.length];
        int	curPos;

        String str = new String(string);
        newStringBuffer = new StringBuffer();
        while (str.length() > 0) {
            // get positions and closest character to replace
            curPos = str.length();
            index  = -1;
            for (int i = 0; i < pos.length; i++) {
                pos[i] = str.indexOf(charsFind[i]);
                if ( (pos[i] > -1) && (pos[i] < curPos) ) {
                    index  = i;
                    curPos = pos[i];
                }
            }

            // replace character if found, otherwise finished
            if (index == -1) {
                newStringBuffer.append(str);
                str = "";
            } else {
                newStringBuffer.append(str.substring(0, pos[index]));
                newStringBuffer.append(charsReplace[index]);
                str = str.substring(pos[index] + charsFind[index].length());
            }
        }

        return newStringBuffer.toString();
    }    
    
}
