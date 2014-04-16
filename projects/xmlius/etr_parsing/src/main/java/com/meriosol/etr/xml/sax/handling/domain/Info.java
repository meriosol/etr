package com.meriosol.etr.xml.sax.handling.domain;

import java.util.Properties;

/**
 * Base abstract class for keeping entity info.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public abstract class Info {
    // For simple element text values (key is element qname, value - text in that element), for complex element - properties.
    private Properties properties;

    protected Info() {
        this(null);
    }

    protected Info(Properties properties) {
        initProperties(properties);
    }

    public abstract String getName();

    public Properties getProperties() {
        return properties;
    }

    public void addProperty(String key, String value) {
        this.properties.setProperty(key, value);
    }

    public void addProperties(Properties properties) {
        if (properties != null) {
            this.properties.putAll(properties);
        }
    }

    public void cleanProperties() {
        initProperties(null);
    }

    private void initProperties(Properties properties) {
        if (properties == null) {
            this.properties = new Properties();
        } else {
            this.properties = properties;
        }
    }

    @Override
    public String toString() {
        return "Info{" + " properties: {" + properties + "}}";
    }
}
