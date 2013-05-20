/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.csclub.abbrev.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 * @author Sergey Serebryakov
 * 
 * Base class for all components - connectors, algorithms and serializers.
 * Can be used to automatically initialize corresponding components. Please,
 * see ConfigurationParameter class for supported types (and formats) that can 
 * automatically be initialized.
 */
public class Component {
    
     public void init(Configuration config) throws Exception {
        if (null != config) {
            for(Field field : this.getClass().getDeclaredFields()){
                if (field.isAnnotationPresent(ConfigurationParameter.class)) {
                    field.setAccessible(true);
                    ConfigurationParameter cp = field.getAnnotation(ConfigurationParameter.class);
                    // name, default value, mandatory
                    //if (cp.mandatory()) {
                    //    if (!config.hasValue(cp.name())) {
                    //        throw new InitializationException(String.format("Parameter '%s' is declared as mandatory but is missing in storage", cp.name()));
                    //    }
                    //}
                    
                    if (config.hasValue(cp.name())) {
                        String value = config.getValue(cp.name());
                        setFromString(field, value);
                    } else {
                        String defaultValue = cp.defaultValue();
                        if (defaultValue.equals(ConfigurationParameter.NULL)) {
                            if (!field.getClass().isPrimitive()) {
                                field.set(this, null);
                            } else {
                                field.set(this, 0);
                            }
                        } else {
                            setFromString(field, defaultValue);    
                        }
                    }
                }
            }
        }
    }
     
     private void setFromString(Field field, String value) throws IllegalArgumentException, IllegalAccessException {
         Class klass = field.getType();
         
         Class strClass = String.class;
         
         if (klass.equals(String.class)) {
              field.set(this, value);
         } else if (klass.equals(Integer.class) || klass.equals(int.class)) {
             field.set(this, Integer.parseInt(value));
         } else if (klass.equals(Float.class) || klass.equals(float.class)) {
             field.set(this, Float.parseFloat(value));
         } else if (klass.equals(Double.class) || klass.equals(double.class)) {
             field.set(this, Double.parseDouble(value));
         } else if (klass.equals(Boolean.class) || klass.equals(boolean.class)) {
             field.set(this, Boolean.parseBoolean(value));
         } else if (klass.equals(Byte.class) || klass.equals(byte.class)) {
             field.set(this, Byte.parseByte(value));
         } else if (klass.equals(Short.class) || klass.equals(short.class)) {
             field.set(this, Short.parseShort(value));
         } else if (klass.equals(Long.class) || klass.equals(long.class)) {
             field.set(this, Long.parseLong(value));
         } else if (klass.equals(Character.class) || klass.equals(char.class)) {
             field.set(this, value.charAt(0));
         } else if (klass.isEnum()) {
             try {
                Class<Enum> cl = (Class<Enum>)field.getType();
                Method m = cl.getDeclaredMethod("customValueOf", String.class);
                Enum enumObj = (Enum)m.invoke(null, value);
                field.set(this, enumObj);
             } catch(NoSuchMethodException | InvocationTargetException e) {
                 field.set(this, Enum.valueOf((Class<Enum>) field.getType(), value));
             }
         }   
     }
}
