package com.meriosol.etr.xsl;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * XSL transform test.
 * @author meriosol
 * @version 0.1
 * @since 12/04/14
 */
public class EventsTransformTest {
    private static final Class<EventsTransformTest> MODULE = EventsTransformTest.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private static final String DEFAULT_INPUT_XML = "events.xml";
    private static final String DEFAULT_OUTPUT_DIR = System.getProperty("java.io.tmpdir");


    @Before
    public void setUp() {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEventsTransform() throws TransformerException, IOException {
        // File/resource names:
        String inputXmlResourceName = DEFAULT_INPUT_XML;
        String inputXslResourceName = "events_basic_grid.xsl";
        String eventsOutputFileName = "etr_transformed_events_page.html";
        File outputFile = new File(DEFAULT_OUTPUT_DIR + File.separator + eventsOutputFileName);

        // URLs:
        URL inputXslResourceUrl = MODULE.getClassLoader().getResource(inputXslResourceName);
        assertNotNull("inputXslResourceUrl sh not be null!", inputXslResourceUrl);

        URL inputXmlResourceUrl = MODULE.getClassLoader().getResource(inputXmlResourceName);
        assertNotNull("inputXmlResourceUrl sh not be null!", inputXmlResourceUrl);

        // Streams:
        InputStream eventTransformSSheetStream = inputXslResourceUrl.openStream();
        InputStream inputEventsXmlStream = inputXmlResourceUrl.openStream();

        try (OutputStream outputStream = new FileOutputStream(outputFile);
        ) {
            // 1. Create transformer:
            Transformer xmlTransformer = TransformerFactory.newInstance().newTransformer(
                    new StreamSource(eventTransformSSheetStream));
            // NOTE: in production env you'll need to cache it.

            // 2. Transform:
            xmlTransformer.transform(
                    new StreamSource(inputEventsXmlStream), new StreamResult(outputStream)
            );
        }

        lOG.info(String.format("Events xml resource '%s' was transformed via xsl '%s' into file '%s'."
                , DEFAULT_INPUT_XML, inputXslResourceName, outputFile.getAbsolutePath()));
    }

}
