package com.huangyiming.disjob.common.util;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
 
/**
 * Load configurations from property file in classpath, default is {@link #PROPERTY_FILE}.  
 */
public class Config {
	private Log log = org.apache.commons.logging.LogFactory.getLog(this.getClass());
    /**
     * Default property file for the configuration of the clustering updater
     */
    public static final String PROPERTY_FILE = "crawler.properties";

    private Properties prop;
    /**
     * Default Constructor using the default {@link #PROPERTY_FILE}.
     */
    public Config() {
//        this(PROPERTY_FILE);
    }
    
    /**
     * Constructor that takes a custom property file.
     * @param propertyFile custom property file in classpath
     */
    public Config(String propertyFile) {
        prop = new Properties();
        try {
            InputStream stream = getClass().getClassLoader().getResourceAsStream(propertyFile);
            if(stream==null) stream = ClassLoader.getSystemResourceAsStream(propertyFile);
            prop.load(stream);
        } catch (IOException e) {
            log.error("Cannot load config file " + PROPERTY_FILE, e);
        }
    }

    /**
     * Get the property value of the given property key
     * @param key   property key
     * @return      value of the property
     */
    public String getProperty(String key) {
        return prop.getProperty(key);
    }
    
    public String getPropertyNotEmpty(String key) {
        String value = getProperty(key);
        if(StringUtils.isBlank(value)) throw new RuntimeException("property " + key + " is blank");
        return value;
    }
    
    /**
     * Get the boolean value of the given property key
     * @param key   property key
     * @return      true/false
     */
    public boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        if("true".equalsIgnoreCase(value)) return true;
        if("false".equalsIgnoreCase(value)) return false;
        throw new RuntimeException("value of property " + key + " must be either true/false");
    }

    public boolean getBooleanPropertyNotEmpty(String key) {
        String value = getPropertyNotEmpty(key);
        if("true".equalsIgnoreCase(value)) return true;
        if("false".equalsIgnoreCase(value)) return false;
        throw new RuntimeException("value of property " + key + " must be either true/false");
    }

    /**
     * Get the integer value of the given property key
     * @param key   property key
     * @return      integer value of the property
     */
    public int getIntProperty(String key) {
        String value = getProperty(key);
        return Integer.parseInt(value);
    }

    public int getIntPropertyNotEmpty(String key) {
        String value = getPropertyNotEmpty(key);
        return Integer.parseInt(value);
    }
}
