package org.csclub.impl;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.csclub.AbbreviationCounter;


/**
 * Trie implementation of the abbreviation counter component.
 * Time of counting is linear in summary size of abbreviations.
 * 
 * @author Fedor Amosov
 */
public class TrieAbbreviationCounter implements AbbreviationCounter {
    
    public TrieAbbreviationCounter() {
        abbrevCounter = new Trie();
    }
    
    public void onNewAbbreviations(final List<String> abbreviations) {
        for(String abbreviation : abbreviations) {
            abbrevCounter.add(abbreviation);
        }
    }
    
    public void corpusProcessComplete() {
        sortedAbbreviations = abbrevCounter.freqList();
    }
    
    public void print(PrintStream cout) {
        for (Pair<String, Integer> abbreviation : sortedAbbreviations) {
            cout.println(String.format("%s\t%d", abbreviation.getKey(), abbreviation.getValue()));
        }
    }
    
    private Trie abbrevCounter;
    private List<Pair<String, Integer>> sortedAbbreviations;
    
    /**
     * Standart trie implementation with counters in nodes.
     */
    private class Trie {
        
        public Trie() {
            root = new Node();
        }
        
        /**
         * Add string to the trie.
         *
         * @param s is addition string.
         */
        public void add(String s) {
            Node cur = root;
            for (int i = 0; i < s.length(); ++i) {
                if (!cur.next.containsKey(Character.valueOf(s.charAt(i)))) {
                    cur.next.put(s.charAt(i), new Node(cur, s.charAt(i)));
                }
                cur = cur.next.get(s.charAt(i));
            }
            ++cur.val;
            ++size;
        }
        
        /**
         * @return list of pairs (string, count in trie) ordered by decreasing 
         * of count. 
         */
        public List<Pair<String, Integer>> freqList() {
            ends =  new ArrayList<List<Node>>();
            for (int i = 0; i <= size; ++i) { 
                ends.add(new ArrayList<Node>());
            }
            
            dfs(root);
            
            List<Pair<String, Integer>> result = new ArrayList<Pair<String, Integer>>();
            for (int i = ends.size() - 1; i > 0; --i) {
                for (Node end : ends.get(i)) {
                    result.add(new ImmutablePair<String, Integer>(get(end), i));
                }
            }
            return result;
        }
        
        private Node root;
        private int size;    
        private List<List<Node>> ends;
        
        /**
         * Bypass the subtree of node in which all occurences of strings will be
         * stored.
         * 
         * @param v is Node, whose subtree will be bypassed.
         */
        private void dfs(Node v) {
            if (v.val > 0) {
                ends.get(v.val).add(v);
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
            public int val;
            public HashMap<Character, Node> next;
            public Node prev;
            public Character edge;
            
            public Node() {
                next = new HashMap<Character, Node>();
            }
            
            public Node(Node prev, Character edge) {
                this.prev = prev;
                this.edge = edge;
                next = new HashMap<Character, Node>();
            }    
        }
    }
}
