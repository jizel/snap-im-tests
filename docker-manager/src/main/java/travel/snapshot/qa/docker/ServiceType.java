package travel.snapshot.qa.docker;

/**
 * Enumeration of services we want to have dockerized.
 */
public enum ServiceType {
    GENERIC,
    MARIADB,
    MONGODB,
    TOMCAT,
    ACTIVEMQ,
    JBOSS_STANDALONE,
    JBOSS_DOMAIN,
    REDIS;
}
