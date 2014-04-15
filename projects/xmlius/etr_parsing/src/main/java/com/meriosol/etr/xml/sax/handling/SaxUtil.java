package com.meriosol.etr.xml.sax.handling;

import org.xml.sax.Attributes;

/**
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public class SaxUtil {
    private SaxUtil() {
    }

    /**
     * TODO: despite debug shows values, nothing is shown eventually from attributes..
     * @param attributes
     * @return gathered info about attributes
     */
    public static String gatherAttributesInfo(Attributes attributes) {
        StringBuilder sb = new StringBuilder("Attrs: {");
        if (attributes != null) {
            int ln = attributes.getLength();
            for (int i = 0; i < ln; i++) {
                sb.append(" ").append(attributes.getQName(i)).append("='").append(attributes.getValue(i)).append("'");
                if (i < attributes.getLength()-1) {
                    sb.append(", ");
                }
            }
            sb.append("}");
        }
        return sb.toString();
    }
}
