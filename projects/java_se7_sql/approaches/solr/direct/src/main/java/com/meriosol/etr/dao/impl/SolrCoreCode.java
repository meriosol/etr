package com.meriosol.etr.dao.impl;

/**
 * Supported Solr core names. Introduced for type safe core short name referencing.
 * TODO: it only adds burden of supporting core in code. Likely String could work better (but 'type safety' get lost then).
 *
 * @author meriosol
 * @version 0.1
 * @since 04/03/14
 */
enum SolrCoreCode {
    //-------
    // FOR ETR
    EVENT_CATEGORIES("EVENT_CATEGORIES"), EVENTS("EVENTS");
    //-------

    private String code;

    SolrCoreCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
