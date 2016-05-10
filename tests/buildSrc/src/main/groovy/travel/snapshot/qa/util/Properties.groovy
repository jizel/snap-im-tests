package travel.snapshot.qa.util

import org.apache.commons.lang3.SystemUtils
import travel.snapshot.qa.docker.manager.ConnectionMode
import travel.snapshot.qa.test.execution.load.*
import travel.snapshot.qa.test.execution.threescale.ThreeScaleApiEnvironment
import travel.snapshot.qa.test.execution.tomcat.DeploymentStrategy
import travel.snapshot.qa.util.container.DockerContainer
import travel.snapshot.qa.util.machine.DockerMachineHelper

import static travel.snapshot.qa.docker.manager.ConnectionMode.STARTORCONNECTANDLEAVE
import static travel.snapshot.qa.test.execution.tomcat.DeploymentStrategy.DEPLOYORREDEPLOY
import static travel.snapshot.qa.util.DockerMode.HOST
import static travel.snapshot.qa.util.DockerMode.MACHINE
import static travel.snapshot.qa.util.TestExecutionMode.DEVELOPMENT
import static travel.snapshot.qa.util.TestExecutionMode.TEST

class Properties {

    static class DP {

        static final String DATA_PLATFORM_REPOSITORY_URL = "git@bitbucket.org:bbox/data-platform.git"

        static String getRepositoryUrl() {
            System.getProperty("dataPlatformRepositoryUrl", DATA_PLATFORM_REPOSITORY_URL)
        }

        static String getRepository() {
            Location.resolveLocation(System.getProperty("dataPlatformRepository", "data-platform")).absolutePath
        }

        static String getRepositoryCommit(String defaultCommit) {
            System.getProperty("dataPlatformRepositoryCommit", defaultCommit)
        }

        static Boolean getRepositoryCheckoutCommit() {
            Boolean.parseBoolean(System.getProperty("dataPlatformRepositoryCheckoutCommit"))
        }
    }

    static class API {

        static final String DATA_PLATFORM_API_REPOSITORY_URL = "git@bitbucket.org:bbox/dataplatformapi.git"

        static String getRepositoryUrl() {
            System.getProperty("dataPlatformRepositoryApiUrl", DATA_PLATFORM_API_REPOSITORY_URL)
        }

        static String getRepository() {
            Location.resolveLocation(System.getProperty("dataPlatformApiRepository", "dataplatformapi")).absolutePath
        }

        static String getRepositoryCommit(String defaultCommit) {
            System.getProperty("dataPlatformRepositoryApiCommit", defaultCommit)
        }

        static Boolean getRepositoryCheckoutCommit() {
            Boolean.parseBoolean(System.getProperty("dataPlatformRepositoryApiCheckoutCommit"))
        }
    }

    static class QA {

        static final String DATA_PLATFORM_QA_REPOSITORY_URL = "git@bitbucket.org:bbox/dataplatformqa.git"

        static String getRepositoryUrl() {
            System.getProperty("dataPlatformQARepositoryUrl", DATA_PLATFORM_QA_REPOSITORY_URL)
        }

        static String getRepository() {
            Location.resolveLocation(System.getProperty("dataPlatformQARepository", "dataplatformqa")).absolutePath
        }

        static String getRepositoryCommit(String defaultCommit) {
            System.getProperty("dataPlatformRepositoryQACommit", defaultCommit)
        }

        static Boolean getRepositoryCheckoutCommit() {
            Boolean.parseBoolean(System.getProperty("dataPlatformRepositoryQACheckoutCommit"))
        }
    }

    static class Operations {

        static final String OPERATIONS_REPOSITORY_URL = "git@bitbucket.org:bbox/operations.git"

        static String getRepositoryUrl() {
            System.getProperty("operationsRepositoryUrl", OPERATIONS_REPOSITORY_URL)
        }

        static String getRepository() {
            Location.resolveLocation(System.getProperty("operationsRepository", "operations")).absolutePath
        }

        static String getRepositoryCommit(String defaultCommit) {
            System.getProperty("operationsCommit", defaultCommit)
        }

        static Boolean getRepositoryCheckoutCommit() {
            Boolean.parseBoolean(System.getProperty("operationsCheckoutCommit"))
        }
    }

    static class Docker {

        static final DockerMode DEFAULT_DOCKER_MODE = MACHINE

        static final String DEFAULT_DOCKER_MACHINE_NAME = "default"

        private static final ConnectionMode DEFAULT_CONNECTION_MODE = STARTORCONNECTANDLEAVE

        private static final int DEFAULT_VM_MEMORY_SIZE = 3072 // in MB

        static String getMode() {

            DockerMode resolvedDockerMode

            try {
                resolvedDockerMode = DockerMode.valueOf(System.getProperty("dockerMode", DEFAULT_DOCKER_MODE.name()))
            } catch (Exception ex) {
                resolvedDockerMode = DEFAULT_DOCKER_MODE
            }

            resolvedDockerMode.name()
        }

        static String getMachineName() {
            System.getProperty("dockerMachine", DEFAULT_DOCKER_MACHINE_NAME)
        }

        static String getConnectionMode() {

            ConnectionMode resolvedConnectionMode

            try {
                resolvedConnectionMode = ConnectionMode.valueOf(System.getProperty("connectionMode", DEFAULT_CONNECTION_MODE.name()))
            } catch (Exception ex) {
                resolvedConnectionMode = DEFAULT_CONNECTION_MODE
            }

            resolvedConnectionMode.name()
        }

        static def getMachineMemorySize() {

            int memory

            try {
                memory = Integer.parseInt(System.getProperty("dockerMachineMemorySize", Integer.toString(DEFAULT_VM_MEMORY_SIZE)))
            } catch (NumberFormatException ex) {
                memory = DEFAULT_VM_MEMORY_SIZE
            }

            memory.toString()
        }

        static String getRegistryPassword() {

            String password = System.getProperty("dockerRegistryPassword")

            if (!password) {
                throw new IllegalStateException("You have not set system property 'dockerRegistryPassword' with " +
                        "password to Docker registry. The best way to set this property is to add line " +
                        "'systemProp.dockerRegistryPassword=<password>' into your gradle.properties file.")
            }

            password
        }

        static String getIp(String containerId) {
            if (Properties.Docker.getMode() == MACHINE.name()) {
                return DockerMachineHelper.getIp(machineName)
            }

            DockerContainer.inspectIP(containerId)
        }

        static String getMachineIp(String machineName) {
            DockerMachineHelper.getIp(machineName)
        }

        static String getMachineIp() {
            getMachineIp(machineName)
        }

        static class Installation {

            static final String DOCKER_INSTALLATION = "docker"

            static final String DOCKER_MACHINE_INSTALLATION = "dockerMachine"

            static final List<String> DATA_PLATFORM_INSTALLATIONS = [ 'tomcat', 'mariadb', 'mongodb', 'activemq' ]

            // mariadbkey is needed for keycloak container to store its data and for identity module
            // tomcat container is needed as a runtime for identity module
            // activemq is needed when identity module sends messages about whats going on
            // keycloak is Wildfly server witk Keycloak installation itself
            // mongodb is for configuration module on which we can try to authenticate against
            static final List<String> KEYCLOAK_PLATFORM_INSTALLATIONS = [ 'mongodb', 'mariadbkey', 'activemq', 'tomcatkey', 'keycloak', 'redis', 'nginx' ]

            static List<String> resolveDockerInstallations() {
                List<String> installations = []

                installations << DOCKER_INSTALLATION

                if (Properties.Docker.getMode() == MACHINE.name()) {
                    installations << DOCKER_MACHINE_INSTALLATION
                }

                installations
            }

            static List<String> resolveContainerInstallations() {
                if (!ProjectHelper.profile.startsWith("keycloak")) {
                    return DATA_PLATFORM_INSTALLATIONS
                } else {
                    return KEYCLOAK_PLATFORM_INSTALLATIONS
                }
            }
        }
    }

    static class Location {

        static File resolveLocation(String directory) {
            resolveLocation(ProjectHelper.workspace, directory)
        }

        static File resolveLocation(File workspace, String directory) {
            File resolvedDirectory

            File directoryFile = new File(directory)

            if (directoryFile.isAbsolute()) {
                resolvedDirectory = directoryFile
            } else {
                resolvedDirectory = new File(workspace, directory)
            }

            resolvedDirectory
        }

        static File getDataPlatformRepository() {
            resolveLocation(ProjectHelper.workspace, Properties.DP.repository)
        }

        static File getDataPlatformQARepository() {
            resolveLocation(ProjectHelper.workspace, Properties.QA.repository)
        }

        static File getDataPlatformAPIRepository() {
            resolveLocation(ProjectHelper.workspace, Properties.API.repository)
        }
    }

    static class Tomcat {

        static final DeploymentStrategy DEFAULT_DEPLOYMENT_STRATEGY = DEPLOYORREDEPLOY

        static String getSpringConfigDirectoryMount() {

            String mount

            if (Properties.Docker.mode == HOST.name()) {
                if (SystemUtils.IS_OS_UNIX) {
                    mount = ProjectHelper.project.rootDir.absolutePath + "/configuration"
                } else {
                    mount = "configuration"
                }
            } else {
                mount = "/home/docker/configuration"
            }

            mount
        }

        static String getSpringConfigDirectorySource() {

            String configurationSource

            if (SystemUtils.IS_OS_UNIX) {
                configurationSource = ProjectHelper.project.rootDir.absolutePath + "/configuration"
            } else {
                configurationSource = "configuration"
            }

            configurationSource
        }

        static String getDeploymentStrategy() {

            DeploymentStrategy deploymentStrategy

            try {
                deploymentStrategy = DeploymentStrategy.valueOf(System.getProperty("tomcatDeploymentStrategy", DEFAULT_DEPLOYMENT_STRATEGY.toString()))
            } catch (Exception ex) {
                deploymentStrategy = DEFAULT_DEPLOYMENT_STRATEGY
            }

            deploymentStrategy.name()
        }

        /**
         * When running in TEST execution mode, we do not want to mount anything because that mount would
         * overlay manager deployment in Tomcat so we would not be able to connect to it because that connection check
         * checks if we are able to list deployments via manager which is not there because binding of directory from
         * localhost or Docker machine would make that directory effectively empty.
         *
         * On the other hand if we are running in DEVELOPMENT mode, we want mount directory to Tomcat from which
         * it will pick deployments we copy there from local host or IDEA copies them there by Tomcat remote configuration.
         *
         * @return bind record which get propagated to arquillian.xml for Tomcat container
         */
        static String getDeploymentDirectoryBind() {
            TestExecutionMode testExecutionMode = TestExecutionMode.valueOf(Test.executionMode)

            switch (testExecutionMode) {
                case DEVELOPMENT:
                    // this will be expanded to arquillian.xml into 'arquillian.xml.deployments.mount' property
                    return "- ${deploymentDirectory}:/opt/tomcat/webapps"
                case TEST:
                    // returning empty String here means that in arquillian.xml, there will be effectively nothing expanded
                    return ""
                default:
                    throw new IllegalStateException("Unable to get deployment directory bind for " + testExecutionMode.name())
            }
        }

        static String getDeploymentDirectory() {
            DockerMode dockerMode = DockerMode.valueOf(Docker.mode)
            switch (dockerMode) {
                case MACHINE:
                    return "/opt/tomcat/webapps"
                case HOST:
                    throw new IllegalStateException("It is not possible to use deployment feature in connection with HOST Docker mode.")
                default:
                    throw new IllegalStateException("Unable to resolve deployment directory for Docker mode ${dockerMode}")
            }
        }
    }

    static class Nginx {

        static String getNginxConfigDirectoryMount() {

            String mount

            if (Properties.Docker.mode == HOST.name()) {
                if (SystemUtils.IS_OS_UNIX) {
                    mount = Properties.Operations.repository
                } else {
                    throw new IllegalStateException("It is not possible to use HOST mode with other then Unix-like operation system.")
                }
            } else {
                mount = "/home/docker/configuration"
            }

            mount
        }

        static String getNginxConfigDirectorySource() {

            String configurationSource

            if (SystemUtils.IS_OS_UNIX) {
                configurationSource = Properties.Operations.repository + "/nginx"
            } else {
                // TODO fix this path for Windows machines
                throw new UnsupportedOperationException()
                //configurationSource = "configuration"
            }

            configurationSource
        }
    }
    
    static class Test {

        private static final TestExecutionMode DEFAULT_EXECUTION_MODE = TEST

        /**
         * Return execution mode of Snapshot Data Platform test project.
         *
         * There are two modes, DEVELOPMENT and TEST, in case TEST is resolved, platform will be started in such way
         * it will be possible to deploy wars directly from IDE (such as IDEA) to Docker containers. In case TEST is choosen,
         * it will be possible just to run tests in a CI manner.
         *
         * Default test execution mode is DEVELOPMENT.
         *
         * @return resolved test execution mode
         */
        static String getExecutionMode() {

            TestExecutionMode testExecutionMode

            try {
                testExecutionMode = TestExecutionMode.valueOf(System.getProperty("testExecutionMode", DEFAULT_EXECUTION_MODE.toString()))
            } catch (Exception ex) {
                testExecutionMode = DEFAULT_EXECUTION_MODE
            }

            testExecutionMode.name()
        }
    }

    static class LoadTest {

        static List<String> getInstallations() {

            List<String> loadTestsInstallations = []

            if (environment == LoadTestEnvironment.DOCKER) {
                // Docker specific
                loadTestsInstallations.addAll(Properties.Docker.Installation.resolveDockerInstallations())

                // we need data-platform repository because we build modules which
                // will be subsequently deployed to Tomcat in Docker,
                // load tests are in QA repository itself
                loadTestsInstallations.addAll(Properties.Docker.Installation.DATA_PLATFORM_INSTALLATIONS)

                // in case we are running against Docker, we have to build modules to run tests against by
                // gradle installation in the first place
                loadTestsInstallations.addAll(['dataPlatformQARepository', 'dataPlatformRepository', 'maven', 'gradle'])
            } else {
                // if load tests environment is not DOCKER, it means we are executing them against
                // environment which has them already deployed so we just execute tests
                // and we are not building these services on our own
                loadTestsInstallations << 'dataPlatformQARepository'
                loadTestsInstallations << 'maven'
            }

            loadTestsInstallations
        }

        static List<String> getTestExecutions() {
            List<String> loadTestsTestExecutions

            if (environment == LoadTestEnvironment.DOCKER) {
                loadTestsTestExecutions = ['platformStart', 'loadTestsDeployment', 'loadTests', 'platformStop']
            } else {
                loadTestsTestExecutions = ['loadTests']
            }

            loadTestsTestExecutions
        }

        static List<LoadTestsSimulation> getSimulations() {

            List<LoadTestsSimulation> simulations

            try {
                simulations = LoadTestsSimulations.valueOf(System.getProperty("loadTestSimulations", LoadTestsSimulations.ALL.name()).toUpperCase()).simulations()
            } catch (Exception ex) {
                simulations = LoadTestsSimulations.ALL.simulations()
            }

            simulations
        }

        static LoadTestEnvironment getEnvironment() {

            LoadTestEnvironment environment

            try {
                environment = LoadTestEnvironment.valueOf(System.getProperty("loadTestEnvironment", LoadTestEnvironment.DOCKER.name()).toUpperCase())
            } catch (Exception ex) {
                environment = LoadTestEnvironment.DOCKER
            }

            environment
        }

        static int getStartUsers() {
            Integer.parseInt(System.getProperty("loadTestStartUsers", Integer.toString(LoadTestsConfiguration.DEFAULT_START_USERS)))
        }

        static int getEndUsers() {
            Integer.parseInt(System.getProperty("loadTestEndUsers", Integer.toString(LoadTestsConfiguration.DEFAULT_END_USERS)))
        }

        static int getRamp() {
            Integer.parseInt(System.getProperty("loadTestRamp", Integer.toString(LoadTestsConfiguration.DEFAUT_RAMP)))
        }

        static String getHost() {
            System.getProperty("loadTestHost")
        }

        static String getPort() {
            System.getProperty("loadTestPort")
        }

        static OAuthConfiguration getOauthConfiguration() {
            new OAuthConfiguration(System.getProperty("oauthClientId"), System.getProperty("oauthClientSecret"))
        }
    }

    static class Execution {

        static boolean isRunningWithGui() {
            Boolean.parseBoolean(System.getProperty("testRunningWithGui"))
        }

        static boolean isSkipTestExecution() {
            Boolean.parseBoolean(System.getProperty("skipTestExecution"))
        }
    }

    static class ThreeScale {

        static String getProviderKey() {
            String providerKey = System.getProperty("threeScaleProviderKey")

            if (!providerKey || providerKey.isEmpty()) {
                throw new IllegalArgumentException("You have to provide 'threeScaleProviderKey' system property with " +
                        "3scale provider key to be able to upload JSON swagger files there.")
            }

            providerKey
        }

        static ThreeScaleApiEnvironment getEnvironment() {
            ThreeScaleApiEnvironment.valueOf(System.getProperty("threeScaleEnvironment", ThreeScaleApiEnvironment.DEV.name()).toUpperCase())
        }
    }
}
