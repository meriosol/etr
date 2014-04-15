package com.meriosol.etr.xml.sax.handling.domain;

import com.meriosol.etr.xml.sax.handling.SaxUtil;
import org.xml.sax.Attributes;

import java.util.Properties;

/**
 * Base abstract class for keeping entity info.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public abstract class Info {
    // For XML element attributes
    private Attributes attributes;
    // For simple element text values (key is element qname, value - text in that element)
    private Properties properties;

    protected Info() {
        this(null, null);
    }

    protected Info(Attributes attributes, Properties properties) {
        this.attributes = attributes;
        initProperties(properties);
    }

    public abstract String getName();

    protected Info(Attributes attributes) {
        this(attributes, null);
    }

    protected Info(Properties properties) {
        this(null, properties);
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public void setAttributes(Attributes attributes) {
        this.attributes = attributes;
    }

    public Properties getProperties() {
        return properties;
    }

    public void addProperty(String key, String value) {
        this.properties.setProperty(key, value);
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
        return "Info{" +
                "attributes=" + SaxUtil.gatherAttributesInfo(attributes) +
                ", properties=" + properties +
                '}';
    }
}
