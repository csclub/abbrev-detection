/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Sergey Serebryakov
 * 
 * Java annotation class that should be used to automatically initialize 
 * component fields.
 * 
 * Supported types are: 
 *     Byte    : "any parsable byte" 
 *     Short   : "any parsable short" 
 *     Integer : "any parsable integer" 
 *     Long    : "any parsable long"
 *     Float   : "any parsable float" 
 *     Double  : "any parsable double" 
 *     Boolean : "any parsable boolean"
 *     Char    : "a" (charAt(0))
 *     String  : "any string"
 * 
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigurationParameter {
    
    /** 
     * Special constant indicating that a field must be initialized with
     * default value for that type (null for objects, zero for primitive types).
     */
    public static final String NULL = "org.csclub.abbrev.impl.ConfigurationParameter.NULL";
    /** 
     * Name of the parameter. May differ from the field name. This name will used
     * to search the parameter in the storage.
     */
    String name();
    /** Parameter default value (please, see class comments for format)*/
    String defaultValue() default NULL;
    /** 
     * If this parameter is mandatory, it must be presented in the storage.
     * Else, exception will be thrown.
     */
    boolean mandatory() default false;
    
}
