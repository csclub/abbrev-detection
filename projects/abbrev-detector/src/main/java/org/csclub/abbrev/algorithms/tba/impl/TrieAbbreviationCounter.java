package org.csclub.abbrev.algorithms.tba.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.algorithms.tba.AbbreviationCounter;

/**
 * Trie implementation of the abbreviation counter component. Time of counting
 * is linear in summary size of abbreviations and contexts.
 *
 * @author Fedor Amosov
 */
public class TrieAbbreviationCounter implements AbbreviationCounter {

    @Override
    public void onNewAbbreviations(final List<Abbreviation> abbreviations) {
        for (Abbreviation abbreviation : abbreviations) {
            abbrevCounter.add(abbreviation);
        }
    }

    @Override
    public void corpusProcessComplete() {
        sortedAbbreviations = abbrevCounter.freqList();
    }

    @Override
    public void print(PrintStream ps) {
        for (Abbreviation abbreviation : sortedAbbreviations) {
            ps.println(abbreviation.toString(-1));
        }
        ps.println("-----");
        ps.println("Total number of unique abbreviations: " + sortedAbbreviations.size());
    }
    
    @Override
    public List<Abbreviation> getSortedAbbreviations() {
        if (sortedAbbreviations == null) {
            corpusProcessComplete();
        }
        return sortedAbbreviations;
    }

    public TrieAbbreviationCounter() {
        abbrevCounter = new Trie();
    }

    private Trie abbrevCounter;   
    private List<Abbreviation> sortedAbbreviations;

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
        public void add(Abbreviation abbreviation) {
            Node cur = root;
            for (int i = 0; i < abbreviation.getAbbrevText().length(); ++i) {
                if (!cur.next.containsKey(Character.valueOf(abbreviation.getAbbrevText().charAt(i)))) {
                    cur.next.put(abbreviation.getAbbrevText().charAt(i), 
                                 new Node(cur, abbreviation.getAbbrevText().charAt(i), abbreviation.getAbbrevState()));
                }
                cur = cur.next.get(abbreviation.getAbbrevText().charAt(i));
            }
            cur.contexts.addAll(abbreviation.getAbbrevContexts());
            
            ++size;
            maxLen = Math.max(maxLen, abbreviation.getAbbrevText().length());
        }

        /**
         * @return list of abbreviations ordered by decreasing
         * of count.
         */
        public List<Abbreviation> freqList() {
            tails = new ArrayList();
            for (int i = 0; i <= size; ++i) {
                tails.add(new ArrayList());
                for (int j = 0; j <= maxLen; ++j) {
                    tails.get(i).add(new ArrayList());
                }
            }

            dfs(root);

            List<Abbreviation> result = new ArrayList();
            for (int i = tails.size() - 1; i > 0; --i) {
                for (List<Node> freqLevel : tails.get(i)) {
                    for (Node tail : freqLevel) {
                        Abbreviation cur = new Abbreviation(tail.state, tail.contexts.size(), get(tail));
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
            public Abbreviation.AbbrevState state;
            public int dist = 0;

            public Node() {
            }

            public Node(Node prev, Character edge, Abbreviation.AbbrevState state) {
                this.prev = prev;
                this.edge = edge;
                this.dist = prev.dist + 1;
                this.state = state;
            }
        }
    }
}
