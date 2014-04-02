package com.meriosol.etr.dao.impl;

import com.meriosol.exception.EtrException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton factory for retrieving new JDBC connection.
 *
 * @author meriosol
 * @version 0.1
 * @since 17/01/14
 */
class DbConnectionFactory {
    private static final DbConnectionFactory connectionFactory = new DbConnectionFactory();
    private DbConnectionSettingsHolder dbConnectionSettingsHolder = null;


    static DbConnectionFactory getInstance() {
        return connectionFactory;
    }

    private DbConnectionFactory() {
    }

    Connection obtainNewDefaultConnection() {
        reloadSettingsHolderIfEmpty();
        return obtainNewConnection(dbConnectionSettingsHolder.getDbUrl(), dbConnectionSettingsHolder.getDbSettings());
    }

    /**
     * @param dbUrl Connection URL e.g. 'jdbc:derby://localhost:1527/Sample1DB'.
     * @param props Generally only user/password
     * @return Connection
     */
    Connection obtainNewConnection(String dbUrl, Properties props) {
        Connection conn;
        try {
            conn = DriverManager.getConnection(dbUrl, props);
        } catch (SQLException e) {
            throw new EtrException("Error while getting connection for DB URL = " + dbUrl, e);
        }
        return conn;
    }

    private void reloadSettingsHolderIfEmpty() {
        if (this.dbConnectionSettingsHolder == null) {
            this.dbConnectionSettingsHolder = new DbConnectionSettingsHolder();
        }
    }

}
