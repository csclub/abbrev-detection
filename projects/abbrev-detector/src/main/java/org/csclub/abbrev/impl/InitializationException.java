/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.impl;

/**
 *
 * @author Sergey Serebryakov
 * 
 * This exception is thrown whenever something cannot be initialized.
 */
public class InitializationException extends Exception {
    
    public InitializationException(String msg) {
        super(msg);
    }
    
     public InitializationException(Exception ex) {
        super(ex);
    }
    
}
