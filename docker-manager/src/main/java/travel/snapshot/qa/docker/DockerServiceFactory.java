package travel.snapshot.qa.docker;

import travel.snapshot.qa.docker.manager.DockerServiceManager;
import travel.snapshot.qa.docker.manager.impl.ActiveMQDockerManager;
import travel.snapshot.qa.docker.manager.impl.JBossDomainDockerManager;
import travel.snapshot.qa.docker.manager.impl.JBossStandaloneDockerManager;
import travel.snapshot.qa.docker.manager.impl.MariaDBDockerManager;
import travel.snapshot.qa.docker.manager.impl.MongoDBDockerManager;
import travel.snapshot.qa.docker.manager.impl.TomcatDockerManager;
import travel.snapshot.qa.manager.activemq.api.ActiveMQManager;
import travel.snapshot.qa.manager.activemq.configuration.ActiveMQManagerConfiguration;
import travel.snapshot.qa.manager.activemq.impl.ActiveMQManagerImpl;
import travel.snapshot.qa.manager.jboss.JBossDomainManager;
import travel.snapshot.qa.manager.jboss.JBossStandaloneManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;
import travel.snapshot.qa.manager.mariadb.configuration.MariaDBManagerConfiguration;
import travel.snapshot.qa.manager.mariadb.impl.MariaDBManagerImpl;
import travel.snapshot.qa.manager.mongodb.api.MongoDBManager;
import travel.snapshot.qa.manager.mongodb.configuration.MongoDBManagerConfiguration;
import travel.snapshot.qa.manager.mongodb.impl.MongoDBManagerImpl;
import travel.snapshot.qa.manager.tomcat.TomcatManager;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;

/**
 * Convenience wrapper for all available Snapshot DataPlatform services.
 *
 * You should use this class in connection with platform orchestration via its {@link
 * travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration#with(DockerServiceManager)}
 *
 * @see travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration
 */
public class DockerServiceFactory {

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

    public static JBossDomainService jbossDomainService() {
        return new JBossDomainService();
    }

    /**
     * Represents Docker service with running Tomcat container. When not explicitly specified the name of the container
     * to start will be "tomcat".
     */
    public static final class TomcatService implements Service<TomcatManager, TomcatManagerConfiguration> {

        public static final String DEFAULT_TOMCAT_CONTAINER_ID = "tomcat";

        @Override
        public DockerServiceManager<TomcatManager> init(TomcatManagerConfiguration configuration, String containerId) {

            if (!configuration.isRemote()) {
                throw new IllegalStateException("Used TomcatManagerConfiguration is not remote.");
            }

            return new TomcatDockerManager(new TomcatManager(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<TomcatManager> init(String containerId) {
            return new TomcatDockerManager(new TomcatManager(new TomcatManagerConfiguration.Builder().remote().build())).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<TomcatManager> init(TomcatManagerConfiguration configuration) {
            return init(configuration, DEFAULT_TOMCAT_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<TomcatManager> init() {
            return init(new TomcatManagerConfiguration.Builder().remote().build());
        }
    }

    /**
     * Represents Docker service with running Mongo container. When not explicitly specified, the name of the container
     * to start will be "mongodb".
     */
    public static final class MongoDBService implements Service<MongoDBManager, MongoDBManagerConfiguration> {

        public static final String DEFAULT_MONGODB_CONTAINER_ID = "mongodb";

        @Override
        public DockerServiceManager<MongoDBManager> init(MongoDBManagerConfiguration configuration, String containerId) {
            return new MongoDBDockerManager(new MongoDBManagerImpl(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<MongoDBManager> init(String containerId) {
            return new MongoDBDockerManager(new MongoDBManagerImpl(new MongoDBManagerConfiguration.Builder().build())).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<MongoDBManager> init(MongoDBManagerConfiguration configuration) {
            return init(configuration, DEFAULT_MONGODB_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<MongoDBManager> init() {
            return init(new MongoDBManagerConfiguration.Builder().build());
        }
    }

    /**
     * Represents Docker service with running MariaDB container. When not explicitly specified, the name of the
     * container to start will be "mariadb".
     */
    public static final class MariaDBService implements Service<MongoDBManager, MariaDBManagerConfiguration> {

        public static final String DEFAULT_MARIADB_CONTAINER_ID = "mariadb";

        @Override
        public DockerServiceManager<MongoDBManager> init(MariaDBManagerConfiguration configuration, String containerId) {
            return new MariaDBDockerManager(new MariaDBManagerImpl(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<MongoDBManager> init(String containerId) {
            return new MariaDBDockerManager(new MariaDBManagerImpl(new MariaDBManagerConfiguration.Builder().build())).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<MongoDBManager> init(MariaDBManagerConfiguration configuration) {
            return init(configuration, DEFAULT_MARIADB_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<MongoDBManager> init() {
            return init(new MariaDBManagerConfiguration.Builder().build());
        }
    }

    /**
     * Represents Docker service with running ActiveMQ container. When not explicitly specified, the name of the
     * container to start will be "activemq".
     */
    public static final class ActiveMQService implements Service<ActiveMQManager, ActiveMQManagerConfiguration> {

        public static final String DEFAULT_ACTIVEMQ_CONTAINER_ID = "activemq";

        @Override
        public DockerServiceManager<ActiveMQManager> init(ActiveMQManagerConfiguration configuration, String containerId) {
            return new ActiveMQDockerManager(new ActiveMQManagerImpl(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<ActiveMQManager> init(String containerId) {
            return init(new ActiveMQManagerConfiguration.Builder().build(), containerId);
        }

        @Override
        public DockerServiceManager<ActiveMQManager> init(ActiveMQManagerConfiguration configuration) {
            return init(configuration, DEFAULT_ACTIVEMQ_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<ActiveMQManager> init() {
            return init(new ActiveMQManagerConfiguration.Builder().build());
        }
    }

    /**
     * Represents Docker service with running JBoss container in standalone mode. When not explicitly specified, the
     * name of the container to start will be "jboss".
     */
    public static final class JBossStandaloneService implements Service<JBossStandaloneManager, JBossManagerConfiguration> {

        public static final String DEFAULT_JBOSS_STANDALONE_CONTAINER_ID = "jboss";

        @Override
        public DockerServiceManager<JBossStandaloneManager> init(JBossManagerConfiguration configuration, String containerId) {
            return new JBossStandaloneDockerManager(new JBossStandaloneManager(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<JBossStandaloneManager> init(String containerId) {
            return init(new JBossManagerConfiguration.Builder().build(), containerId);
        }

        @Override
        public DockerServiceManager<JBossStandaloneManager> init(JBossManagerConfiguration configuration) {
            return init(configuration, DEFAULT_JBOSS_STANDALONE_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<JBossStandaloneManager> init() {
            return init(new JBossManagerConfiguration.Builder().build());
        }
    }

    /**
     * Represents Docker service with running JBoss container in domain mode. When not explicitly specified, the name of
     * the container to start will be "jboss_domain".
     */
    public static final class JBossDomainService implements Service<JBossDomainManager, JBossManagerConfiguration> {

        public static final String DEFAULT_JBOSS_DOMAIN_CONTAINER_ID = "jboss_domain";

        @Override
        public DockerServiceManager<JBossDomainManager> init(JBossManagerConfiguration configuration, String containerId) {
            return new JBossDomainDockerManager(new JBossDomainManager(configuration)).setContainerId(containerId);
        }

        @Override
        public DockerServiceManager<JBossDomainManager> init(String containerId) {
            return init(new JBossManagerConfiguration.Builder().build(), containerId);
        }

        @Override
        public DockerServiceManager<JBossDomainManager> init(JBossManagerConfiguration configuration) {
            return init(configuration, DEFAULT_JBOSS_DOMAIN_CONTAINER_ID);
        }

        @Override
        public DockerServiceManager<JBossDomainManager> init() {
            return init(new JBossManagerConfiguration.Builder().build());
        }
    }
}
