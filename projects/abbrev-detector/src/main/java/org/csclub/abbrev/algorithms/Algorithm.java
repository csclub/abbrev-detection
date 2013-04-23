package org.csclub.abbrev.algorithms;

import java.util.List;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.impl.Component;

/**
 *
 * @author Sergey Serebryakov
 * 
 * The main goal of every algorithm is to build (!!) the list of abbreviations
 * given either annotated or un-annotated corpus. The output of the algorithm - 
 * list (dictionary) of abbreviations with some additional information such as,
 * for instance, number of times the particular abbreviation has been found in 
 * the corpus.
 * 
 */
public abstract class Algorithm <E extends Abbreviation> extends Component {
    /** */
    public abstract void run(Corpus corpus);
    
    /** get results (dictionary of abbreviations) */
    public abstract List<E> getAbbreviations();
}
