package com.meriosol.etr.dao.impl;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DowngradingConsistencyRetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton factory for retrieving new Cassandra session.
 *
 * @author meriosol
 * @version 0.1
 * @since 09/02/14
 */
class DbSessionFactory {
    private static final Logger LOG = LoggerFactory.getLogger(DbSessionFactory.class);
    private static DbSessionFactory connectionFactory = new DbSessionFactory();
    private DbSessionSettingsHolder dbSessionSettingsHolder = null;


    static DbSessionFactory getInstance() {
        return connectionFactory;
    }

    private DbSessionFactory() {
    }

    String obtainDefaultKeyspace() {
        reloadSettingsHolderIfEmpty();
        return this.dbSessionSettingsHolder.getKeyspace();
    }

    Session obtainNewDefaultSession() {
        reloadSettingsHolderIfEmpty();

        String hosts = this.dbSessionSettingsHolder.getHosts();
        String keyspace = this.dbSessionSettingsHolder.getKeyspace();
        String username = this.dbSessionSettingsHolder.getUsername();
        String password = this.dbSessionSettingsHolder.getPassword();

        return obtainNewSession(hosts, keyspace, username, password);
    }

    /**
     * @return Session
     */
    Session obtainNewSession(String hosts, String keyspace, String username, String password) {
        String[] hostsArray = hosts.split(","); // TODO: think of trim()
        Cluster cluster = Cluster.builder()
                .addContactPoints(hostsArray).withCredentials(username, password)
                        // .withSSL() // uncomment if using client to node encryption
                .withRetryPolicy(DowngradingConsistencyRetryPolicy.INSTANCE)
                .withReconnectionPolicy(new ConstantReconnectionPolicy(100L))
                .build();
        Metadata metadata = cluster.getMetadata();
        LOG.info("Connected to cluster {}.", metadata.getClusterName());
        return cluster.connect();
    }

    private void reloadSettingsHolderIfEmpty() {
        if (this.dbSessionSettingsHolder == null) {
            this.dbSessionSettingsHolder = new DbSessionSettingsHolder();
        }
    }

}
