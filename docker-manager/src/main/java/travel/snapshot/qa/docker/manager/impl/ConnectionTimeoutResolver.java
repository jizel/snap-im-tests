package travel.snapshot.qa.docker.manager.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

/**
 * Overrides connection timeouts set during configuration building by respective system property for reach service.
 *
 * When system property is not set, connection timeout from the configuration of the service is used.<br><br>
 *
 * Tomcat connection timeout property: 'docker.tomcat.connection.timeout' <br> MariaDB connection timeout property:
 * 'docker.mariadb.connection.timeout' <br> MongoDB connection timeout property: 'docker.mongodb.connection.timeout'
 * <br> ActiveMQ connection timeout property: 'docker.activemq.connection.timeout' <br>
 */
public class ConnectionTimeoutResolver {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionTimeoutResolver.class);

    private static final String TOMCAT_CONNECTION_TIMEOUT_PROPERTY = "docker.tomcat.connection.timeout";

    private static final String MARIADB_CONNECTION_TIMEOUT_PROPERTY = "docker.mariadb.connection.timeout";

    private static final String MONGODB_CONNECTION_TIMEOUT_PROPERTY = "docker.mongodb.connection.timeout";

    private static final String ACTIVEMQ_CONNECTION_TIMEOUT_PROPERTY = "docker.activemq.connection.timeout";

    private static final String JBOSS_STANDALONE_CONNECTION_TIMEOUT_PROPERTY = "docker.jboss.standalone.connection.timeout";

    private static final String JBOSS_DOMAIN_CONNECTION_TIMEOUT_PROPERTY = "docker.jboss.domain.connection.timeout";

    public static long resolveTomcatConnectionTimeout(TomcatManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), TOMCAT_CONNECTION_TIMEOUT_PROPERTY, ServiceType.TOMCAT.name());
    }

    public static long resolveMariaDBConnectionTimeout(MariaDBManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), MARIADB_CONNECTION_TIMEOUT_PROPERTY, ServiceType.MARIADB.name());
    }

    public static long resolveMongoDBConnectionTimeout(MongoDBManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), MONGODB_CONNECTION_TIMEOUT_PROPERTY, ServiceType.MONGODB.name());
    }

    public static long resolveActiveMQConnectionTimeout(ActiveMQManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), ACTIVEMQ_CONNECTION_TIMEOUT_PROPERTY, ServiceType.ACTIVEMQ.name());
    }

    public static long resolveJBossStandaloneConnectionTimeout(JBossManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), JBOSS_STANDALONE_CONNECTION_TIMEOUT_PROPERTY, ServiceType.JBOSS_STANDALONE.name());
    }

    public static long resolveJBossDomainConnectionTimeout(JBossManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), JBOSS_DOMAIN_CONNECTION_TIMEOUT_PROPERTY, ServiceType.JBOSS_DOMAIN.name());
    }

    private static long resolveTimeout(long setTimeout, String timeoutProperty, String service) {
        long connectionTimeOut = setTimeout;

        String resolvedSystemProperty = System.getProperty(timeoutProperty);

        if (resolvedSystemProperty == null) {
            return connectionTimeOut;
        }

        resolvedSystemProperty = resolvedSystemProperty.trim();

        if (resolvedSystemProperty.isEmpty()) {
            return connectionTimeOut;
        }

        try {
            connectionTimeOut = Long.parseLong(resolvedSystemProperty);
        } catch (NumberFormatException ex) {
            logger.info("Connection timeout for {} service was not valid: {}", service, resolvedSystemProperty);
        }

        logger.info(String.format("Resolved service timeout for %s is %s seconds.", service, connectionTimeOut));

        return connectionTimeOut;
    }
}
