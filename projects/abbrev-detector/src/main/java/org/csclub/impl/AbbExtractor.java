package org.csclub.impl;

import java.util.ArrayList;
import java.util.List;

import org.csclub.AbbreviationExtractor;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * User: Ann Voevodskaia
 */
public class AbbExtractor implements AbbreviationExtractor {

    public List<String> extract(final String sentence) {
        Pattern p = Pattern.compile("(.*)([\\.,&!<>)(»«²]+)(.)");  //kills structures as "yes)."
        Matcher m;
        StringTokenizer st = new StringTokenizer(sentence, " \t\n\r ");
        List<String> wordsWithDots = new ArrayList<String>();
        String currentWord;
        currentWord = st.nextToken();
        m = p.matcher(currentWord);
        if (currentWord.endsWith(".") && (!currentWord.equals("."))  && (!m.matches())) {
            wordsWithDots.add(currentWord);
        }
        if (st.hasMoreElements()) currentWord = st.nextToken();
        while (st.hasMoreElements()) {
            m = p.matcher(currentWord);
            if ((currentWord.endsWith(".")) && (!currentWord.equals(".")) && (!m.matches())) {
                wordsWithDots.add(currentWord);
            }
            currentWord = st.nextToken();

        }
        return wordsWithDots;
    }
}
