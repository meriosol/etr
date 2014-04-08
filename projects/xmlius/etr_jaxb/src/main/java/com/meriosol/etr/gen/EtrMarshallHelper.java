package com.meriosol.etr.gen;


import com.meriosol.jaxb.MarshallHelper;

/**
 * The only goal of this class is to store default ETR schema name
 *
 * @author meriosol
 * @version 0.1
 * @since 07/04/14
 */
public class EtrMarshallHelper<T> extends MarshallHelper<T> {

    public EtrMarshallHelper() {
        this(Constants.DEFAULT_SCHEMA_URL);
    }

    /**
     * @param xmlSchemaResourceUrl
     */
    public EtrMarshallHelper(String xmlSchemaResourceUrl) {
        this(Constants.DEFAULT_VALIDATION_ERROR_TOLERANCE, xmlSchemaResourceUrl);
    }

    /**
     * @param validationErrorTolerant
     */
    public EtrMarshallHelper(boolean validationErrorTolerant) {
        this(validationErrorTolerant, Constants.DEFAULT_SCHEMA_URL);
    }

    /**
     * @param validationErrorTolerant
     * @param xmlSchemaResourceUrl
     */
    public EtrMarshallHelper(boolean validationErrorTolerant, String xmlSchemaResourceUrl) {
        super(validationErrorTolerant, xmlSchemaResourceUrl,
                Constants.DEFAULT_OUTPUT_FORMATTING, Constants.DEFAULT_SCHEMA_NAMESPACE);
    }

    /**
     * @param xmlSchemaNameSpace
     * @param formattedOutput
     */
    public EtrMarshallHelper(String xmlSchemaNameSpace, boolean formattedOutput) {
        super(Constants.DEFAULT_VALIDATION_ERROR_TOLERANCE, Constants.DEFAULT_SCHEMA_URL, formattedOutput, xmlSchemaNameSpace);
    }

}
