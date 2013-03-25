package org.csclub.abbrev.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.csclub.abbrev.Abbreviation;
import org.csclub.abbrev.AbbreviationCounter;

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
                    cur.next.put(abbreviation.getAbbrevText().charAt(i), new Node(cur, abbreviation.getAbbrevText().charAt(i)));
                }
                cur = cur.next.get(abbreviation.getAbbrevText().charAt(i));
            }
            cur.contexts.addAll(abbreviation.getAbbrevContexts());
            ++size;
        }

        /**
         * @return list of abbreviations ordered by decreasing
         * of count.
         */
        public List<Abbreviation> freqList() {
            ends = new ArrayList<List<Node>>();
            for (int i = 0; i <= size; ++i) {
                ends.add(new ArrayList<Node>());
            }

            dfs(root);

            List<Abbreviation> result = new ArrayList<Abbreviation>();
            for (int i = ends.size() - 1; i > 0; --i) {
                for (Node end : ends.get(i)) {
                    Abbreviation current = new Abbreviation();
                    current.setAbbrevText(get(end));
                    
                    current.addAbbrevContexts(end.contexts);
                    current.incrementCounter(end.contexts.size());
                    result.add(current);
                }
            }
            return result;
        }
        private Node root;
        private int size;
        private List<List<Node>> ends;
        private List<List<String>> contexts;

        /**
         * Bypass the subtree of node in which all contexts of abbreviations will
         * be stored.
         *
         * @param v is Node, whose subtree will be bypassed.
         */
        private void dfs(Node v) {
            if (v.contexts.size() > 0) {
                ends.get(v.contexts.size()).add(v);
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
            public HashMap<Character, Node> next = new HashMap<Character, Node>();
            public Node prev;
            public Character edge;
            public List<String> contexts = new ArrayList<String>();

            public Node() {
            }

            public Node(Node prev, Character edge) {
                this.prev = prev;
                this.edge = edge;
            }
        }
    }
}
