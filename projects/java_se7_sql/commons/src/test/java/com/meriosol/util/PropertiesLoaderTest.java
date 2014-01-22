package com.meriosol.util;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author meriosol
 * @version 0.1
 * @since 17/01/14
 */
public class PropertiesLoaderTest {
    // CAUTION: if dir paths or file names get changed, amend these constants:
    private static final String DEFAULT_RESOURCE_FILE_NAME = "resource_test";
    public static final String COMMONS_TESTS_CONF = "commons/tests/conf";
    public static final String COM_MERIOSOL = "/com/meriosol";

    // CAUTION: Because of file path differences tests for file based props fail if to launch them from cmd line.
    // To avoid it it's suggested to uncomment them only for IDE unit tests.
    // In real life systems absolute paths will be much safer to use (or use classpath resource loading).

    @Test
    @Ignore
    public void testLoadPropertiesFromFile() {
        String workingDir = System.getProperty("user.dir");
        String filePath = String.format("%s/%s/%s.properties", workingDir, COMMONS_TESTS_CONF, DEFAULT_RESOURCE_FILE_NAME);
        File file = new File(filePath);
        assertTrue("Properties file cannot be read from filePath = " + filePath, file.canRead());

        Properties properties = PropertiesLoader.loadPropertiesFromFile(filePath);
        assertNotNull("Properties were not loaded for filePath = " + filePath, properties);
    }

    @Test
    @Ignore
    public void testLoadPropertiesFromXmlFile() {
        String workingDir = System.getProperty("user.dir");
        String filePath = String.format("%s/%s/%s.xml", workingDir, COMMONS_TESTS_CONF, DEFAULT_RESOURCE_FILE_NAME);
        File file = new File(filePath);
        assertTrue("Properties file cannot be read from XML filePath = " + filePath, file.canRead());

        Properties properties = PropertiesLoader.loadPropertiesFromXmlFile(filePath);
        assertNotNull("Properties were not loaded for XML filePath = " + filePath, properties);
    }

    @Test
    public void testLoadPropertiesFromClassPathWithoutPackages() {
        String resourcePath = String.format("/%s.properties", DEFAULT_RESOURCE_FILE_NAME);
        Properties properties = PropertiesLoader.loadPropertiesFromClassPath(resourcePath);
        assertNotNull("Properties were not loaded for resourcePath = " + resourcePath, properties);
    }

    @Test
    public void testLoadPropertiesFromClassPathWithPackages() {
        String resourcePath = String.format("%s/%s.properties", COM_MERIOSOL, DEFAULT_RESOURCE_FILE_NAME);
        Properties properties = PropertiesLoader.loadPropertiesFromClassPath(resourcePath);
        assertNotNull("Properties were not loaded for resourcePath = " + resourcePath, properties);
    }

    @Test
    public void testLoadPropertiesXmlFromClassPathWithoutPackages() {
        String resourcePath = String.format("/%s.xml", DEFAULT_RESOURCE_FILE_NAME);
        Properties properties = PropertiesLoader.loadPropertiesFromClassPath(resourcePath);
        assertNotNull("Properties were not loaded for XML resourcePath = " + resourcePath, properties);
    }

    @Test
    public void testLoadPropertiesXmlFromClassPathWithPackages() {
        String resourcePath = String.format("%s/%s.xml", COM_MERIOSOL, DEFAULT_RESOURCE_FILE_NAME);
        Properties properties = PropertiesLoader.loadPropertiesFromClassPath(resourcePath);
        assertNotNull("Properties were not loaded for XML resourcePath = " + resourcePath, properties);
    }
}
