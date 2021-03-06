package org.csclub.abbrev.algorithms.tba.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.csclub.abbrev.algorithms.tba.AbbreviationCounter;
import org.csclub.abbrev.algorithms.tba.CorpusAbbreviation;

/**
 * Trie implementation of the abbreviation counter component. Time of counting
 * is linear in summary size of abbreviations and contexts.
 *
 * @author Fedor Amosov
 */
public class TrieAbbreviationCounter implements AbbreviationCounter<CorpusAbbreviation> {

    @Override
    public void onNewAbbreviations(final List<CorpusAbbreviation> abbreviations) {
        for (CorpusAbbreviation abbreviation : abbreviations) {
            abbrevCounter.add(abbreviation);
        }
    }

    @Override
    public void corpusProcessComplete() {
        sortedAbbreviations = abbrevCounter.freqList();
    }

    @Override
    public void print(PrintStream ps) {
        for (CorpusAbbreviation abbreviation : sortedAbbreviations) {
            ps.println(abbreviation.toString());
        }
        ps.println("-----");
        ps.println("Total number of unique abbreviations: " + sortedAbbreviations.size());
    }
    
    @Override
    public List<CorpusAbbreviation> getSortedAbbreviations() {
        if (sortedAbbreviations == null) {
            corpusProcessComplete();
        }
        return sortedAbbreviations;
    }

    public TrieAbbreviationCounter() {
        abbrevCounter = new Trie();
    }

    private Trie abbrevCounter;   
    private List<CorpusAbbreviation> sortedAbbreviations;

    /**
     * Standard trie implementation with counters in nodes.
     */
    private class Trie {

        public Trie() {
            root = new Node();
        }

        /**
         * Add abbreviation to the trie.
         *
         * @param abbreviation is addition abbreviation.
         */
        public void add(CorpusAbbreviation abbreviation) {
            //throw new Exception();
            //if (abbreviation.getAbbrevText().equals("ошиблась.")) {
            //    System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            //}
            String text = abbreviation.getAbbrevText();
            
            Node cur = root;
            for (int i = 0; i < text.length(); ++i) {
                if (!cur.next.containsKey(Character.valueOf(text.charAt(i)))) {
                    cur.next.put(text.charAt(i), 
                                 new Node(cur, text.charAt(i), abbreviation.getAbbrevState()));
                }
                cur = cur.next.get(text.charAt(i));
            }
            cur.contexts.addAll(abbreviation.getAbbrevContexts());
            
            ++size;
            maxLen = Math.max(maxLen, text.length());
        }

        /**
         * @return list of abbreviations ordered by decreasing
         * of count.
         */
        public List<CorpusAbbreviation> freqList() {
            tails = new ArrayList();
            for (int i = 0; i <= size; ++i) {
                tails.add(new ArrayList());
                for (int j = 0; j <= maxLen; ++j) {
                    tails.get(i).add(new ArrayList());
                }
            }

            dfs(root);

            List<CorpusAbbreviation> result = new ArrayList();
            for (int i = tails.size() - 1; i > 0; --i) {
                for (List<Node> freqLevel : tails.get(i)) {
                    for (Node tail : freqLevel) {
                        CorpusAbbreviation cur = new CorpusAbbreviation(tail.state, tail.contexts.size(), get(tail));
                        cur.addAbbrevContexts(tail.contexts);
                        result.add(cur);
                    }
                }
            }
            return result;
        }
        
        private Node root;
        private int size;
        private int maxLen;
        private List<List<List<Node>>> tails;

        /**
         * Bypass the subtree of node in which all contexts of abbreviations will
         * be stored.
         *
         * @param v is Node, whose subtree will be bypassed.
         */
        private void dfs(Node v) {
            if (v.contexts.size() > 0) {
                tails.get(v.contexts.size()).get(v.dist).add(v);
            }

            for (Entry<Character, Node> edge : v.next.entrySet()) {
                dfs(edge.getValue());
            }
        }

        /**
         * Obtaining the string, which finishes in the node.
         *
         * @param v is Node, whose string will be obtain.
         *
         * @return string with end in v.
         */
        private String get(Node v) {
            StringBuilder abbreviation = new StringBuilder();
            while (v.prev != null) {
                abbreviation.append(v.edge);
                v = v.prev;
            }
            return abbreviation.reverse().toString();
        }

        private class Node {
            public HashMap<Character, Node> next = new HashMap();
            public Node prev;
            public Character edge;
            public List<String> contexts = new ArrayList();
            public CorpusAbbreviation.AbbrevState state;
            public int dist = 0;

            public Node() {
            }

            public Node(Node prev, Character edge, CorpusAbbreviation.AbbrevState state) {
                this.prev = prev;
                this.edge = edge;
                this.dist = prev.dist + 1;
                this.state = state;
            }
        }
    }
}
