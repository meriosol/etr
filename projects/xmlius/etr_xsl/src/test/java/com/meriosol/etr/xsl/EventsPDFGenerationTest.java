package com.meriosol.etr.xsl;

import org.apache.fop.apps.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URL;
import java.util.logging.Logger;

import static org.junit.Assert.assertNotNull;

/**
 * PDF Generation test.
 * @author meriosol
 * @version 0.1
 * @since 13/04/14
 */
public class EventsPDFGenerationTest {
    private static final Class<EventsPDFGenerationTest> MODULE = EventsPDFGenerationTest.class;
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
    public void testEventsPdfGeneration() throws TransformerException, IOException, FOPException {
        // File/resource names:

        // Input XML:
        String inputXmlResourceName = DEFAULT_INPUT_XML;

        // XSL-FO:
        String inputXsFolResourceName = "eventium_as_paginated_cards.xsl";

        String eventsOutputFileName = "etr_events.pdf";
        File outputFile = new File(DEFAULT_OUTPUT_DIR + File.separator + eventsOutputFileName);

        // URLs:
        URL inputXslResourceUrl = MODULE.getClassLoader().getResource(inputXsFolResourceName);
        assertNotNull("inputXslResourceUrl sh not be null!", inputXslResourceUrl);

        URL inputXmlResourceUrl = MODULE.getClassLoader().getResource(inputXmlResourceName);
        assertNotNull("inputXmlResourceUrl sh not be null!", inputXmlResourceUrl);

        // Create an instance of fop factory:
        FopFactory fopFactory = FopFactory.newInstance();

        // User agent for transformation:
        FOUserAgent foUserAgent = fopFactory.newFOUserAgent();

        Fop fop;
        try (OutputStream outputStream = new FileOutputStream(outputFile);
             InputStream eventTransformSSheetStream = inputXslResourceUrl.openStream();
             InputStream inputEventsXmlStream = inputXmlResourceUrl.openStream();
        ) {
            fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, outputStream);

            // Resulting SAX events (the generated FO) must be piped through to FOP
            Result fopResults = new SAXResult(fop.getDefaultHandler());

            // 1. Create transformer:
            Transformer xslfoTransformer = TransformerFactory.newInstance().newTransformer(
                    new StreamSource(eventTransformSSheetStream));

            xslfoTransformer.transform(new StreamSource(inputEventsXmlStream), fopResults);

            // NOTE: in production env you'll need to cache it.
            lOG.info(String.format("Events xml resource '%s' was transformed via xsl-fo '%s' into PDF file '%s'."
                    , DEFAULT_INPUT_XML, inputXsFolResourceName, outputFile.getAbsolutePath()));
        }

    }

}
