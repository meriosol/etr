package com.meriosol.util;

import com.meriosol.exception.EtrException;

import java.io.*;
import java.util.Properties;

/**
 * Thin wrapper around properties file loading. XML and properties formats are supported.
 *
 * @author meriosol
 * @version 0.1
 * @since 17/01/14
 */
public class PropertiesLoader {

    private PropertiesLoader() {
    }

    /**
     *
     * @param filePath
     * @return Properties loaded from file path
     * @throws EtrException
     */
    public static Properties loadPropertiesFromFile(String filePath) throws EtrException {
        Properties properties = new Properties();
        Reader reader = null;
        try {
            reader = new FileReader(filePath);
        } catch (FileNotFoundException e) {
            throw new EtrException(String.format("Property file '%s' not found!", filePath), e);
        }

        try {
            properties.load(reader);
        } catch (IOException e) {
            throw new EtrException(String.format("Error occurred while loading property file '%s'!", filePath), e);
        }
        return properties;
    }

    /**
     *
     * @param filePath
     * @return Properties loaded from file path
     * @throws EtrException
     */
    public static Properties loadPropertiesFromXmlFile(String filePath) throws EtrException {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            throw new EtrException(String.format("Properties XML file '%s' not found!", filePath), e);
        }

        try {
            properties.loadFromXML(inputStream);
        } catch (IOException e) {
            throw new EtrException(String.format("Error occurred while loading properties XML file '%s'!", filePath), e);
        }
        return properties;
    }

    /**
     *
     * @param resourcePath
     * @return Properties   loaded from classpath
     * @throws EtrException
     */
    public static Properties loadPropertiesFromClassPath(String resourcePath) throws EtrException {
        Properties properties = new Properties();
        InputStream inputStream = PropertiesLoader.class.getResourceAsStream(resourcePath);
        // assert inputStream != null;
        if (inputStream != null) {
            try {
                properties.load(inputStream);
            } catch (IOException e) {
                throw new EtrException(String.format("Error occurred while loading properties file from class path '%s'!", resourcePath), e);
            }

        } else {
            throw new EtrException(String.format("InputStream is null for class path '%s'!", resourcePath));
        }
        return properties;
    }

    /**
     *
     * @param xmlResourcePath
     * @return Properties   loaded from classpath
     * @throws EtrException
     */
    public static Properties loadPropertiesXmlFromClassPath(String xmlResourcePath) throws EtrException {
        Properties properties = new Properties();
        InputStream inputStream = PropertiesLoader.class.getResourceAsStream(xmlResourcePath);
        // assert inputStream != null;
        if (inputStream != null) {
            try {
                properties.loadFromXML(inputStream);
            } catch (IOException e) {
                throw new EtrException(String.format("Error occurred while loading properties XML file from class path '%s'!", xmlResourcePath), e);
            }

        } else {
            throw new EtrException(String.format("InputStream is null for class path '%s'!", xmlResourcePath));
        }
        return properties;
    }
}
