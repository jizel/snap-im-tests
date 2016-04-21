package travel.snapshot.qa.docker.manager.impl;

import static travel.snapshot.qa.docker.ServiceType.ACTIVEMQ;
import static travel.snapshot.qa.docker.ServiceType.GENERIC;
import static travel.snapshot.qa.docker.ServiceType.JBOSS_DOMAIN;
import static travel.snapshot.qa.docker.ServiceType.JBOSS_STANDALONE;
import static travel.snapshot.qa.docker.ServiceType.MARIADB;
import static travel.snapshot.qa.docker.ServiceType.MONGODB;
import static travel.snapshot.qa.docker.ServiceType.REDIS;
import static travel.snapshot.qa.docker.ServiceType.TOMCAT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.docker.ServiceType;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.generic.configuration.GenericConfiguration;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;
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

    private static final String GENERIC_CONNECTION_TIMEOUT_PROPERTY = "docker.generic.connection.timeout";

    private static final String TOMCAT_CONNECTION_TIMEOUT_PROPERTY = "docker.tomcat.connection.timeout";

    private static final String MARIADB_CONNECTION_TIMEOUT_PROPERTY = "docker.mariadb.connection.timeout";

    private static final String MONGODB_CONNECTION_TIMEOUT_PROPERTY = "docker.mongodb.connection.timeout";

    private static final String ACTIVEMQ_CONNECTION_TIMEOUT_PROPERTY = "docker.activemq.connection.timeout";

    private static final String JBOSS_STANDALONE_CONNECTION_TIMEOUT_PROPERTY = "docker.jboss.standalone.connection.timeout";

    private static final String JBOSS_DOMAIN_CONNECTION_TIMEOUT_PROPERTY = "docker.jboss.domain.connection.timeout";

    private static final String REDIS_CONNECTION_TIMEOUT_PROPERTY = "docker.redis.connection.timeout";

    public static long resolveGenericConnectionTimeout(GenericConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), GENERIC_CONNECTION_TIMEOUT_PROPERTY, GENERIC);
    }

    public static long resolveTomcatConnectionTimeout(TomcatManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), TOMCAT_CONNECTION_TIMEOUT_PROPERTY, TOMCAT);
    }

    public static long resolveMariaDBConnectionTimeout(MariaDBManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), MARIADB_CONNECTION_TIMEOUT_PROPERTY, MARIADB);
    }

    public static long resolveMongoDBConnectionTimeout(MongoDBManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), MONGODB_CONNECTION_TIMEOUT_PROPERTY, MONGODB);
    }

    public static long resolveActiveMQConnectionTimeout(ActiveMQManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), ACTIVEMQ_CONNECTION_TIMEOUT_PROPERTY, ACTIVEMQ);
    }

    public static long resolveJBossStandaloneConnectionTimeout(JBossManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), JBOSS_STANDALONE_CONNECTION_TIMEOUT_PROPERTY, JBOSS_STANDALONE);
    }

    public static long resolveJBossDomainConnectionTimeout(JBossManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), JBOSS_DOMAIN_CONNECTION_TIMEOUT_PROPERTY, JBOSS_DOMAIN);
    }

    public static long resolveRedisConnectionTimeout(RedisManagerConfiguration configuration) {
        return resolveTimeout(configuration.getStartupTimeoutInSeconds(), REDIS_CONNECTION_TIMEOUT_PROPERTY, REDIS);
    }

    private static long resolveTimeout(long setTimeout, String timeoutProperty, ServiceType serviceType) {
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
            logger.info("Connection timeout for {} service was not valid: {}", serviceType.name(), resolvedSystemProperty);
        }

        logger.info(String.format("Resolved service timeout for %s is %s seconds.", serviceType.name(), connectionTimeOut));

        return connectionTimeOut;
    }
}
