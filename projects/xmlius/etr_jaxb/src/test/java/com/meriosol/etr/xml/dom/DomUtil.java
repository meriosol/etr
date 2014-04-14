package com.meriosol.etr.xml.dom;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
