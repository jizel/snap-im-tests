package travel.snapshot.qa.util

import org.apache.commons.lang3.SystemUtils
import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.text.ProcessTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.docker.manager.ConnectionMode
import travel.snapshot.qa.inspection.InspectionException
import travel.snapshot.qa.test.execution.load.LoadTestEnvironment
import travel.snapshot.qa.test.execution.load.LoadTestsConfiguration
import travel.snapshot.qa.test.execution.load.LoadTestsSimulation
import travel.snapshot.qa.test.execution.load.LoadTestsSimulations
import travel.snapshot.qa.test.execution.tomcat.DeploymentStrategy
import travel.snapshot.qa.util.container.DockerContainer
import travel.snapshot.qa.util.machine.DockerMachineHelper

import static travel.snapshot.qa.docker.manager.ConnectionMode.STARTORCONNECTANDLEAVE
import static travel.snapshot.qa.test.execution.tomcat.DeploymentStrategy.DEPLOYORREDEPLOY
import static travel.snapshot.qa.util.DockerMode.HOST
import static travel.snapshot.qa.util.DockerMode.MACHINE
import static travel.snapshot.qa.util.TestExecutionMode.DEVELOPMENT
import static travel.snapshot.qa.util.TestExecutionMode.TEST

class PropertyResolver {

    private static final Logger logger = LoggerFactory.getLogger(PropertyResolver)

    private static final int DEFAULT_VM_MEMORY_SIZE = 3072 // in MB

    private static final DeploymentStrategy DEFAULT_DEPLOYMENT_STRATEGY = DEPLOYORREDEPLOY

    private static final ConnectionMode DEFAULT_CONNECTION_MODE = STARTORCONNECTANDLEAVE

    private static final DockerMode DEFAULT_DOCKER_MODE = MACHINE

    private static final TestExecutionMode DEFAULT_EXECUTION_MODE = TEST

    private static final String DATA_PLATFORM_REPOSITORY_URL = "git@bitbucket.org:bbox/data-platform.git"

    private static final String DATA_PLATFORM_QA_REPOSITORY_URL = "git@bitbucket.org:bbox/dataplatformqa.git"

    static def resolveDockerMode() {

        DockerMode resolvedDockerMode

        try {
            resolvedDockerMode = DockerMode.valueOf(System.getProperty("dockerMode", DEFAULT_DOCKER_MODE.name()))
        } catch (Exception ex) {
            resolvedDockerMode = DEFAULT_DOCKER_MODE
        }

        resolvedDockerMode.name()
    }

    static def resolveDockerMachine() {
        System.getProperty("dockerMachine", "default")
    }

    static def resolveConnectionMode() {

        ConnectionMode resolvedConnectionMode

        try {
            resolvedConnectionMode = ConnectionMode.valueOf(System.getProperty("connectionMode", DEFAULT_CONNECTION_MODE.name()))
        } catch (Exception ex) {
            resolvedConnectionMode = DEFAULT_CONNECTION_MODE
        }

        resolvedConnectionMode.name()
    }

    static def resolveDockerRegistryPassword() {
        String password = System.getProperty("dockerRegistryPassword")

        if (!password) {
            throw new IllegalStateException("You have not set system property 'dockerRegistryPassword' with " +
                    "password to Docker registry. The best way to set this property is to add line " +
                    "'systemProp.dockerRegistryPassword=<password>' into your gradle.properties file.")
        }

        password
    }

    static def resolveDataPlatformRepositoryUrl() {
        System.getProperty("dataPlatformRepositoryUrl", DATA_PLATFORM_REPOSITORY_URL)
    }

    static def resolveDataPlatformQARepositoryUrl() {
        System.getProperty("dataPlatformQARepositoryUrl", DATA_PLATFORM_QA_REPOSITORY_URL)
    }

    /**
     * In case system property is specified, return that one, in case it is not set, return "data-platform".
     *
     * "data-platform" is relative path so it will be placed in workspace. In case path is absolute as in case of
     * value from system property, it will be set absolutely so it will live outside of workspace (where e.g.
     * your already existing data-platform sources are).
     *
     * @return path to data-platform repository, absolute or relative one
     */
    static def resolveDataPlatformRepository() {
        def systemVariable = System.getProperty("dataPlatformRepository")

        if (systemVariable) {
            return systemVariable
        }

        "data-platform"
    }

    static def resolveDataPlatformQARepository() {
        def systemVariable = System.getProperty("dataPlatformQARepository")

        if (systemVariable) {
            return systemVariable
        }

        "dataplatformqa"
    }

    static File resolveDataPlatformRepositoryLocation() {
        File workspace = ProjectHelper.workspace
        String dataPlatformDirectory = ProjectHelper.spacelift.configuration['dataPlatformRepository'].value

        resolveLocation(workspace, dataPlatformDirectory)
    }

    static File resolveDataPlatformQARepositoryLocation() {
        File workspace = ProjectHelper.workspace
        String dataPlatformQADirectory = ProjectHelper.spacelift.configuration['dataPlatformQARepository'].value

        resolveLocation(workspace, dataPlatformQADirectory)
    }

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

    static def resolveDataPlatformRespositoryCommit(String defaultCommit) {
        System.getProperty("dataPlatformRepositoryCommit", defaultCommit)
    }

    static def resolveDataPlatformRespositoryQACommit(String defaultCommit) {
        System.getProperty("dataPlatformRepositoryQACommit", defaultCommit)
    }

    static def resolveForceDataPlatformBuild() {
        Boolean.parseBoolean(System.getProperty("forceDataPlatformBuild"))
    }

    static def resolveTomcatSpringConfigDirectoryMount() {
        def mount

        if (resolveDockerMode() == HOST.name()) {
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

    static def resolveTomcatSpringConfigDirectorySource() {

        def configurationSource

        if (SystemUtils.IS_OS_UNIX) {
            configurationSource = ProjectHelper.project.rootDir.absolutePath + "/configuration"
        } else {
            configurationSource = "configuration"
        }

        configurationSource
    }

    static def resolveDataPlatformRepositoryQACheckoutCommit() {
        Boolean.parseBoolean(System.getProperty("dataPlatformRepositoryQACheckoutCommit"))
    }

    static def resolveDataPlatformRepositoryCheckoutCommit() {
        Boolean.parseBoolean(System.getProperty("dataPlatformRepositoryCheckoutCommit"))
    }

    /**
     *
     * @return size of memory for VM in MB, when not set on command line, defaults to 3072
     */
    static def resolveDockerMachineMemorySize() {

        int memory

        try {
            memory = Integer.parseInt(System.getProperty("dockerMachineMemorySize", Integer.toString(DEFAULT_VM_MEMORY_SIZE)))
        } catch (NumberFormatException ex) {
            memory = DEFAULT_VM_MEMORY_SIZE
        }

        memory.toString()
    }

    /**
     *
     * @return resolved deployment strategy, when not set on command line, default to DEPLOYORREDEPLOY
     */
    static def resolveTomcatDeploymentStrategy() {

        def deploymentStrategy

        try {
            deploymentStrategy = DeploymentStrategy.valueOf(System.getProperty("tomcatDeploymentStrategy", DEFAULT_DEPLOYMENT_STRATEGY.toString()))
        } catch (Exception ex) {
            deploymentStrategy = DEFAULT_DEPLOYMENT_STRATEGY
        }

        deploymentStrategy.name()
    }

    static def resolveContainerIP(String containerId) {
        if (resolveDockerMode() == MACHINE.toString()) {
            return DockerMachineHelper.getIp(resolveDockerMachine())
        }

        DockerContainer.inspectIP(containerId)
    }

    // Load Tests

    static List<String> resolveLoadTestsInstallations() {
        List<String> loadTestsInstallations = []

        if (resolveLoadTestEnvironment() == LoadTestEnvironment.DOCKER) {
            loadTestsInstallations = [
                    'docker', 'dockerMachine', // Docker specific

                    'tomcat', 'mariadb', 'mongodb', 'activemq', // Docker services

                    // we need data-platform repository because we build modules which
                    // will be subsequently deployed to Tomcat in Docker,
                    // load tests are in QA repository itself
                    'dataPlatformQARepository', 'dataPlatformRepository',
                    // load tests are located in Maven project so we need Maven installation
                    'maven',
                    // in case we are running against Docker, we have to build modules to run tests against by
                    // gradle installation in the first place
                    'gradle'
            ]
        } else {
            // if load tests environment is not DOCKER, it means we are executing them against
            // environment which has them already deployed so we just execute tests
            // and we are not building these services on our own
            loadTestsInstallations << 'dataPlatformQARepository'
            loadTestsInstallations << 'maven'
        }

        loadTestsInstallations
    }

    static List<String> resolveLoadTestsTestExecutions() {
        List<String> loadTestsTestExecutions

        if (resolveLoadTestEnvironment() == LoadTestEnvironment.DOCKER) {
            loadTestsTestExecutions = [ 'platformStart', 'loadTestsDeployment', 'loadTests', 'platformStop' ]
        } else {
            loadTestsTestExecutions = [ 'loadTests' ]
        }

        loadTestsTestExecutions
    }

    static List<LoadTestsSimulation> resolveLoadTestSimulations() {

        List<LoadTestsSimulation> simulations

        try {
            simulations = LoadTestsSimulations.valueOf(System.getProperty("loadTestSimulations", LoadTestsSimulations.ALL.name()).toUpperCase()).simulations()
        } catch (Exception ex) {
            simulations = LoadTestsSimulations.ALL.simulations()
        }

        simulations
    }

    static LoadTestEnvironment resolveLoadTestEnvironment() {

        LoadTestEnvironment environment

        try {
            environment = LoadTestEnvironment.valueOf(System.getProperty("loadTestEnvironment", LoadTestEnvironment.DOCKER.name()).toUpperCase())
        } catch (Exception ex) {
            environment = LoadTestEnvironment.DOCKER
        }

        environment
    }

    static int resolveLoadTestStartUsers() {
        Integer.parseInt(System.getProperty("loadTestStartUsers", Integer.toString(LoadTestsConfiguration.DEFAULT_START_USERS)))
    }

    static int resolveLoadTestEndUsers() {
        Integer.parseInt(System.getProperty("loadTestEndUsers", Integer.toString(LoadTestsConfiguration.DEFAULT_END_USERS)))
    }

    static int resolveLoadTestRamp() {
        Integer.parseInt(System.getProperty("loadTestRamp", Integer.toString(LoadTestsConfiguration.DEFAUT_RAMP)))
    }

    static String resolveLoadTestHost() {
        System.getProperty("loadTestHost")
    }

    // Execution modes

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
    static def resolveTestExecutionMode() {
        def testExecutionMode

        try {
            testExecutionMode = TestExecutionMode.valueOf(System.getProperty("testExecutionMode", DEFAULT_EXECUTION_MODE.toString()))
        } catch (Exception ex) {
            testExecutionMode = DEFAULT_EXECUTION_MODE
        }

        testExecutionMode.name()
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
    static def resolveTomcatDeploymentDirectoryBind() {
        TestExecutionMode testExecutionMode = TestExecutionMode.valueOf(resolveTestExecutionMode())

        switch (testExecutionMode) {
            case DEVELOPMENT:
                // this will be expanded to arquillian.xml into 'arquillian.xml.deployments.mount' property
                return "- ${resolveTomcatDeploymentDirectory()}:/opt/tomcat/webapps"
            case TEST:
                // returning empty String here means that in arquillian.xml, there will be effectively nothing expanded
                return ""
            default:
                throw new IllegalStateException("Unable to get deployment directory bind for " + testExecutionMode.name())
        }
    }

    static def resolveTomcatDeploymentDirectory() {
        DockerMode dockerMode = DockerMode.valueOf(resolveDockerMode())
        switch (dockerMode) {
            case MACHINE:
                return "/opt/tomcat/webapps"
            case HOST:
                throw new IllegalStateException("It is not possible to use deployment feature in connection with HOST Docker mode.")
            default:
                throw new IllegalStateException("Unable to resolve deployment directory for Docker mode ${dockerMode}")
        }
    }

    static boolean isRunningWithGui() {
        Boolean.parseBoolean(System.getProperty("testRunningWithGui"))
    }

    static boolean skipTestExecution() {
        Boolean.parseBoolean(System.getProperty("skipTestExecution"))
    }
}
