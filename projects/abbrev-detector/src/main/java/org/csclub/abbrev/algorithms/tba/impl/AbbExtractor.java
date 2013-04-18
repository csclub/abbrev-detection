package org.csclub.abbrev.algorithms.tba.impl;

import java.util.ArrayList;
import java.util.List;

import org.csclub.abbrev.algorithms.tba.AbbreviationExtractor;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.Sentence;

/**
 * Created by IntelliJ IDEA.
 * User: Ann Voevodskaia
 */
public class AbbExtractor implements AbbreviationExtractor {

    private Pattern p = Pattern.compile("(.*)([\\.,&!<>)(»«²]+)(.)");  //kills structures as "yes)."
    
    @Override
    public List<Abbreviation> extract(Sentence sentence) {
        Matcher m;
        StringTokenizer st = new StringTokenizer(sentence.getSentence(), "[ \t\n\r ]+");
        List<Abbreviation> wordsWithDots = new ArrayList<>();
        String currentWord;
        currentWord = st.nextToken();
        m = p.matcher(currentWord);
        if (currentWord.endsWith(".") && (!currentWord.equals("."))  && (!m.matches())) {
            wordsWithDots.add(new Abbreviation(currentWord));
        }
        if (st.hasMoreElements()) currentWord = st.nextToken();
        while (st.hasMoreElements()) {
            m = p.matcher(currentWord);
            if ((currentWord.endsWith(".")) && (!currentWord.equals(".")) && (!m.matches())) {
                wordsWithDots.add(new Abbreviation(currentWord));
            }
            currentWord = st.nextToken();

        }
        return wordsWithDots;
    }
}
