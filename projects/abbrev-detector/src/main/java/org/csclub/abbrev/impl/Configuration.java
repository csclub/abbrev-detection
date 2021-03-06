package org.csclub.abbrev.impl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

/**
 *
 * @author Sergey Serebryakov
 * 
 * A class is used to initialize the components. Actually, it is a wrapper
 * for such types as map, array and property.
 */
public class Configuration {
    
    private Map<String, String> data;
    
    public Configuration(Map<String, String> data) {
        this.data = data;
        if (null == this.data) {
            this.data = Collections.EMPTY_MAP;
        }
    }
    
    public Configuration(String [] data) throws InitializationException {
        if (null != data && data.length%2 == 0) {
            this.data = new HashMap<> ();
            for (int i=0; i<data.length/2; i++) {
                this.data.put(data[2*i], data[2*i+1]);
            }
        } else {
            throw new InitializationException("Storage cannot be initialized");
        }
    }
    
    public boolean hasValue(String key) {
        return data.containsKey(key);
    }
    
    public String getValue(String key) {
        return data.get(key);
    }
    
    public Configuration getOnlyForNamespace(final String namespace) {
        String prefix = String.format("%s.", namespace);
        Map<String, String> namespaceStorage = new HashMap ();
        for (Entry<String, String> entry : data.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                namespaceStorage.put(entry.getKey().substring(prefix.length(), entry.getKey().length()), entry.getValue());
            }
        }
        return new Configuration(namespaceStorage);
    }
    
    public static Configuration loadFromFile(String fileName, String encoding) throws IOException {
        Properties properties = new Properties ();
        properties.load( new InputStreamReader(new FileInputStream(fileName), encoding));
        Map<String, String> data = new HashMap<> ();
        Enumeration keys = properties.propertyNames();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            data.put(key, properties.getProperty(key));
        }
        return new Configuration(data);
    }
}
