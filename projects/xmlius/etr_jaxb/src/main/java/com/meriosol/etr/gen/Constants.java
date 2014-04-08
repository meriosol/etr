package com.meriosol.etr.gen;

/**
 * Common constants. NOTE: ideally they should be in corresponding classes.
 *
 * @author meriosol
 * @version 0.1
 * @since 07/04/14
 */
interface Constants {
    String DEFAULT_SCHEMA_URL = "etr.xsd";
    boolean DEFAULT_VALIDATION_ERROR_TOLERANCE = false;

    //--------------
    // Marshaller specific constants.
    // TODO: consider moving them to proper class..
    String DEFAULT_SCHEMA_NAMESPACE = "http://com/meriosol/etr/schema";
    boolean DEFAULT_OUTPUT_FORMATTING = true;
    //--------------
}
