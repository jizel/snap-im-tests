package travel.snapshot.qa.docker;

import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.docker.manager.impl.ActiveMQDockerManager;
import travel.snapshot.qa.docker.manager.impl.GenericDockerManager;
import travel.snapshot.qa.docker.manager.impl.JBossDomainDockerManager;
import travel.snapshot.qa.docker.manager.impl.JBossStandaloneDockerManager;
import travel.snapshot.qa.docker.manager.impl.MariaDBDockerManager;
import travel.snapshot.qa.docker.manager.impl.MongoDBDockerManager;
import travel.snapshot.qa.docker.manager.impl.RedisDockerManager;
import travel.snapshot.qa.docker.manager.impl.TomcatDockerManager;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.activemq.impl.ActiveMQManagerImpl;
import travel.snapshot.qa.manager.generic.api.GenericManager;
import travel.snapshot.qa.manager.generic.configuration.GenericConfiguration;
import travel.snapshot.qa.manager.generic.impl.GenericManagerImpl;
import travel.snapshot.qa.manager.jboss.JBossDomainManager;
import travel.snapshot.qa.manager.jboss.JBossStandaloneManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.mariadb.api.MariaDBManager;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;
import travel.snapshot.qa.manager.mariadb.impl.MariaDBManagerImpl;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;
import travel.snapshot.qa.manager.mongodb.impl.MongoDBManagerImpl;
import travel.snapshot.qa.manager.redis.api.RedisManager;
import travel.snapshot.qa.manager.redis.configuration.RedisManagerConfiguration;
import travel.snapshot.qa.manager.redis.impl.RedisManagerImpl;
import travel.snapshot.qa.manager.tomcat.TomcatManager;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

import java.util.function.Supplier;

/**
 * Convenience wrapper for all available Snapshot DataPlatform services.
 *
 * You should use this class in connection with platform orchestration via its {@link
 * travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration#with(DockerServiceManager)}
 *
 * @see travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration
 */
public class DockerServiceFactory {

    public static GenericService generic() {
        return new GenericService();
    }

    public static TomcatService tomcat() {
        return new TomcatService();
    }

    public static MongoDBService mongodb() {
        return new MongoDBService();
    }

    public static MariaDBService mariadb() {
        return new MariaDBService();
    }

    public static ActiveMQService activemq() {
        return new ActiveMQService();
    }

    public static JBossStandaloneService jbossStandalone() {
        return new JBossStandaloneService();
    }

    public static JBossDomainService jbossDomain() {
        return new JBossDomainService();
    }

    public static RedisService redis() {
        return new RedisService();
    }

    /**
     * Represents dummy Docker service which does not really manage anything. It just starts and stops a container.
     */
    public static class GenericService implements Service<GenericManager, GenericConfiguration> {

        public static final String DEFAULT_GENERIC_CONTAINER_ID = "default";

        private final Supplier<GenericConfiguration> genericConfigurationSupplier = () -> new GenericConfiguration.Builder().build();

        @Override
        public DockerServiceManager<GenericManager> init(GenericConfiguration configuration, String containerId) {
            return new GenericDockerManager(new GenericManagerImpl(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<GenericManager> init(String containerId) {
            return init(genericConfigurationSupplier.get(), DEFAULT_GENERIC_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<GenericManager> init(GenericConfiguration configuration) {
            return init(configuration, DEFAULT_GENERIC_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<GenericManager> init() {
            return init(genericConfigurationSupplier.get());
        }
    }

    /**
     * Represents Docker service with running Tomcat container. When not explicitly specified the name of the container
     * to start will be "tomcat".
     */
    public static final class TomcatService implements Service<TomcatManager, TomcatManagerConfiguration> {

        public static final String DEFAULT_TOMCAT_CONTAINER_ID = "tomcat";

        private final Supplier<TomcatManagerConfiguration> tomcatConfigurationSupplier = () -> new TomcatManagerConfiguration.Builder().remote().build();

        @Override
        public DockerServiceManager<TomcatManager> init(TomcatManagerConfiguration configuration, String containerId) {

            if (!configuration.isRemote()) {
                throw new IllegalStateException("Used configuration is not remote.");
            }

            return new TomcatDockerManager(new TomcatManager(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<TomcatManager> init(String containerId) {
            return init(tomcatConfigurationSupplier.get(), containerId);
        }

        @Override
        public DockerServiceManager<TomcatManager> init(TomcatManagerConfiguration configuration) {
            return init(configuration, DEFAULT_TOMCAT_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<TomcatManager> init() {
            return init(tomcatConfigurationSupplier.get());
        }
    }

    /**
     * Represents Docker service with running Mongo container. When not explicitly specified, the name of the container
     * to start will be "mongodb".
     */
    public static final class MongoDBService implements Service<MongoDBManager, MongoDBManagerConfiguration> {

        public static final String DEFAULT_MONGODB_CONTAINER_ID = "mongodb";

        private final Supplier<MongoDBManagerConfiguration> mongoDBConfigurationSupplier = () -> new MongoDBManagerConfiguration.Builder().build();

        @Override
        public DockerServiceManager<MongoDBManager> init(MongoDBManagerConfiguration configuration, String containerId) {
            return new MongoDBDockerManager(new MongoDBManagerImpl(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<MongoDBManager> init(String containerId) {
            return init(mongoDBConfigurationSupplier.get(), containerId);
        }

        @Override
        public DockerServiceManager<MongoDBManager> init(MongoDBManagerConfiguration configuration) {
            return init(configuration, DEFAULT_MONGODB_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<MongoDBManager> init() {
            return init(mongoDBConfigurationSupplier.get());
        }
    }

    /**
     * Represents Docker service with running MariaDB container. When not explicitly specified, the name of the
     * container to start will be "mariadb".
     */
    public static final class MariaDBService implements Service<MariaDBManager, MariaDBManagerConfiguration> {

        public static final String DEFAULT_MARIADB_CONTAINER_ID = "mariadb";

        private final Supplier<MariaDBManagerConfiguration> mariadbConfigurationSupplier = () -> new MariaDBManagerConfiguration.Builder().build();

        @Override
        public DockerServiceManager<MariaDBManager> init(MariaDBManagerConfiguration configuration, String containerId) {
            return new MariaDBDockerManager(new MariaDBManagerImpl(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<MariaDBManager> init(String containerId) {
            return init(mariadbConfigurationSupplier.get(), containerId);
        }

        @Override
        public DockerServiceManager<MariaDBManager> init(MariaDBManagerConfiguration configuration) {
            return init(configuration, DEFAULT_MARIADB_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<MariaDBManager> init() {
            return init(mariadbConfigurationSupplier.get());
        }
    }

    /**
     * Represents Docker service with running ActiveMQ container. When not explicitly specified, the name of the
     * container to start will be "activemq".
     */
    public static final class ActiveMQService implements Service<ActiveMQManager, ActiveMQManagerConfiguration> {

        public static final String DEFAULT_ACTIVEMQ_CONTAINER_ID = "activemq";

        private final Supplier<ActiveMQManagerConfiguration> activeMQConfigurationSupplier = () -> new ActiveMQManagerConfiguration.Builder().build();

        @Override
        public DockerServiceManager<ActiveMQManager> init(ActiveMQManagerConfiguration configuration, String containerId) {
            return new ActiveMQDockerManager(new ActiveMQManagerImpl(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<ActiveMQManager> init(String containerId) {
            return init(activeMQConfigurationSupplier.get(), containerId);
        }

        @Override
        public DockerServiceManager<ActiveMQManager> init(ActiveMQManagerConfiguration configuration) {
            return init(configuration, DEFAULT_ACTIVEMQ_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<ActiveMQManager> init() {
            return init(activeMQConfigurationSupplier.get());
        }
    }

    /**
     * Represents Docker service with running JBoss container in standalone mode. When not explicitly specified, the
     * name of the container to start will be "jboss".
     */
    public static final class JBossStandaloneService implements Service<JBossStandaloneManager, JBossManagerConfiguration> {

        public static final String DEFAULT_JBOSS_STANDALONE_CONTAINER_ID = "jboss";

        private final Supplier<JBossManagerConfiguration> jbossConfigurationSupplier = () -> new JBossManagerConfiguration.Builder().remote().build();

        /**
         * @param configuration configuration of a service
         * @param containerId   container ID which this service will manage
         * @return respective Docker manager for standalone JBoss container
         * @throws IllegalStateException in case provided {@code configuration} is "domain" or not "remote".
         */
        @Override
        public DockerServiceManager<JBossStandaloneManager> init(JBossManagerConfiguration configuration, String containerId) {

            if (configuration.isDomain()) {
                throw new IllegalStateException("Used configuration is 'domain' for standalone service.");
            }

            if (!configuration.isRemote()) {
                throw new IllegalStateException("Used configuration is not 'remote'.");
            }

            return new JBossStandaloneDockerManager(new JBossStandaloneManager(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<JBossStandaloneManager> init(String containerId) {
            return init(jbossConfigurationSupplier.get(), containerId);
        }

        @Override
        public DockerServiceManager<JBossStandaloneManager> init(JBossManagerConfiguration configuration) {
            return init(configuration, DEFAULT_JBOSS_STANDALONE_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<JBossStandaloneManager> init() {
            return init(jbossConfigurationSupplier.get());
        }
    }

    /**
     * Represents Docker service with running JBoss container in domain mode. When not explicitly specified, the name of
     * the container to start will be "jboss_domain".
     */
    public static final class JBossDomainService implements Service<JBossDomainManager, JBossManagerConfiguration> {

        public static final String DEFAULT_JBOSS_DOMAIN_CONTAINER_ID = "jboss_domain";

        private final Supplier<JBossManagerConfiguration> jbossConfigurationSupplier = () -> new JBossManagerConfiguration.Builder().domain().remote().build();

        /**
         * @param configuration configuration of a service
         * @param containerId   container ID which this service will manage
         * @return respective Docker manager for domain JBoss container
         * @throws IllegalStateException in case provided {@code configuration} is not "domain" or "remote".
         */
        @Override
        public DockerServiceManager<JBossDomainManager> init(JBossManagerConfiguration configuration, String containerId) {

            if (!configuration.isDomain()) {
                throw new IllegalStateException("Used configuratoin is not 'domain' for domain service.");
            }

            if (!configuration.isRemote()) {
                throw new IllegalStateException("Used configuration is not 'remote'.");
            }

            return new JBossDomainDockerManager(new JBossDomainManager(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<JBossDomainManager> init(String containerId) {
            return init(jbossConfigurationSupplier.get(), containerId);
        }

        @Override
        public DockerServiceManager<JBossDomainManager> init(JBossManagerConfiguration configuration) {
            return init(configuration, DEFAULT_JBOSS_DOMAIN_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<JBossDomainManager> init() {
            return init(jbossConfigurationSupplier.get());
        }
    }

    public static final class RedisService implements Service<RedisManager, RedisManagerConfiguration> {

        public static final String DEFAULT_REDIS_CONTAINER_ID = "redis";

        private final Supplier<RedisManagerConfiguration> redisConfigurationSupplier = () -> new RedisManagerConfiguration.Builder().build();

        @Override
        public DockerServiceManager<RedisManager> init(RedisManagerConfiguration configuration, String containerId) {
            return new RedisDockerManager(new RedisManagerImpl(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<RedisManager> init(String containerId) {
            return init(redisConfigurationSupplier.get(), containerId);
        }

        @Override
        public DockerServiceManager<RedisManager> init(RedisManagerConfiguration configuration) {
            return init(configuration, DEFAULT_REDIS_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<RedisManager> init() {
            return init(redisConfigurationSupplier.get());
        }
    }

}
