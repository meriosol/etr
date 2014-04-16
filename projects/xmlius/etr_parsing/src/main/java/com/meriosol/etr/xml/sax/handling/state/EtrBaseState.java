package com.meriosol.etr.xml.sax.handling.state;

import com.meriosol.etr.xml.sax.handling.domain.Info;
import org.xml.sax.Attributes;

import java.util.Properties;
import java.util.logging.Logger;

/**
 * Base state. Made as a class to keep common methods in future.
 *
 * @author meriosol
 * @version 0.1
 * @since 15/04/14
 */
public abstract class EtrBaseState {
    private static final Class<EtrBaseState> MODULE = EtrBaseState.class;
    private static final Logger lOG = Logger.getLogger(MODULE.getName());
    private String tempPropertyKey;

    //--------------
    // Handle Openings:
    public abstract void handleEventsOpening();

    public abstract void handleEventOpening(Properties properties);

    public abstract void handleEventCategoryOpening(Properties properties);

    //--------------
    // Handle Closings:
    public abstract void handleEventCategoryClosing();

    public abstract void handleEventClosing();

    public abstract void handleEventsClosing();

    //--------------
    // Handle misc:
    public abstract void handleAddingPropertyKey(final String key);
    public abstract void handleAddingText(String text);

    public abstract String getStateName();
    //----------------------
    // TODO: consider own class for it..

    /**
     * @return key (usually basic element Qname)
     */
    protected String getTempPropertyKey() {
        return this.tempPropertyKey;
    }

    protected void setTempPropertyKey(String tempPropertyKey) {
        this.tempPropertyKey = tempPropertyKey;
    }

    /**
     * Removes key once not needed anymore
     */
    protected void removeTempPropertyKey() {
        this.tempPropertyKey = null;
    }

    protected void addProperty(Info info, String value) {
        if (info != null && this.tempPropertyKey != null && !"".equals(this.tempPropertyKey)) {
            info.addProperty(this.tempPropertyKey, value);
            lOG.info(String.format("~~ In %s / addProperty: For info with name '%s' set key='%s' and value '%s'."
                    , getStateName(), info.getName(), this.tempPropertyKey, value));
        } else {
            lOG.warning(String.format("~~ In %s / addProperty: either info object is null(%s) or key null/empty (%s)"
                    , getStateName(), info == null, this.tempPropertyKey));
        }
    }
    //----------------------
}
