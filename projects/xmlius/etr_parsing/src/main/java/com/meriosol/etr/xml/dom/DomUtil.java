package com.meriosol.etr.xml.dom;

import com.meriosol.etr.CommonUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

/**
 * DOM related utils.
 *
 * @author meriosol
 * @version 0.1
 * @since 13/04/14
 */
public class DomUtil {
    private static final Class<DomUtil> MODULE = DomUtil.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());

    /**
     * @param resourcePath
     * @return DOM Document for events.
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Document loadDom(String resourcePath)
            throws ParserConfigurationException, IOException, SAXException {
        URL resourceUrl = CommonUtil.getResourceUrl(resourcePath);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "file, jar:file");
        } catch (IllegalArgumentException e) {
            //jaxp 1.5 feature not supported
            lOG.warning("Looks like jaxp 1.5 feature not supported: " + e.getMessage());
        }

        dbf.setNamespaceAware(true);
        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

        return documentBuilder.parse(resourceUrl.openStream());
    }

    /**
     * @param parentElement
     * @param childElementName
     * @return 1st element
     */
    public static Element loadFirstElementNode(Element parentElement, String childElementName) {
        Element childElement = null;
        if (parentElement != null && childElementName != null && !"".equals(childElementName)) {
            NodeList childNodes = parentElement.getElementsByTagNameNS(parentElement.getNamespaceURI(), childElementName);
            if (childNodes != null && childNodes.getLength() > 0) {
                if (childNodes.getLength() > 1) {
                    lOG.warning(String.format("(CAUTION: for element %s/%s '%s' children found, but 1 is expected!)"
                            , parentElement.getNodeName(), childElementName, childNodes.getLength()));
                }
                Node node = childNodes.item(0);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    childElement = (Element) node;
                }
            }
        }
        return childElement;
    }

    /**
     * @param element
     * @param childElementName
     * @return Element text
     */
    public static String loadChildElementText(Element element, String childElementName) {
        StringBuilder elementTextSB = new StringBuilder();
        if (element != null) {
            Element childElement = loadFirstElementNode(element, childElementName);
            if (childElement != null) {
                elementTextSB.append(childElement.getTextContent());
            }
        }

        return elementTextSB.toString();

    }

}
