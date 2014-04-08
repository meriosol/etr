package com.meriosol.etr.gen;

import com.meriosol.jaxb.UnmarshallHelper;

/**
 * The only goal of this class is to store default ETR schema name
 *
 * @author meriosol
 * @version 0.1
 * @since 07/04/14
 */
public class EtrUnmarshallHelper<T> extends UnmarshallHelper<T> {

    public EtrUnmarshallHelper() {
        this(Constants.DEFAULT_SCHEMA_URL);
    }

    /**
     * @param xmlSchemaResourceUrl
     */
    public EtrUnmarshallHelper(String xmlSchemaResourceUrl) {
        this(Constants.DEFAULT_VALIDATION_ERROR_TOLERANCE, xmlSchemaResourceUrl);
    }

    /**
     * @param validationErrorTolerant
     */
    public EtrUnmarshallHelper(boolean validationErrorTolerant) {
        this(validationErrorTolerant, Constants.DEFAULT_SCHEMA_URL);
    }

    /**
     * @param validationErrorTolerant
     * @param xmlSchemaResourceUrl
     */
    public EtrUnmarshallHelper(boolean validationErrorTolerant, String xmlSchemaResourceUrl) {
        super(validationErrorTolerant, xmlSchemaResourceUrl);
    }

}
