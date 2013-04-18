package org.csclub.abbrev.connectors;

import org.csclub.abbrev.Corpus;
import org.csclub.abbrev.impl.Component;

/**
 *
 * @author Sergey Serebryakov
 * 
 * Connector to some source from which sentences could be fetched.
 * If this source is completely unstructured, some sort of pipeline
 * should be used inside the connector to parse the text and split it
 * into sentences and tokens
 */
public abstract class CorpusReader extends Component {
    
    public abstract Corpus read() throws Exception;
}
