/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub;

import java.util.List;

/**
 *
 * @author serebrya
 */
public interface AbbreviationExtractor {
    public List<String> extract(final String sentence);
}
