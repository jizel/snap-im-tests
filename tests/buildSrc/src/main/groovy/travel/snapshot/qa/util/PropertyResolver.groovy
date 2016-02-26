package travel.snapshot.qa.util

import org.apache.commons.lang3.SystemUtils
import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.arquillian.spacelift.gradle.text.ProcessTemplate
import org.gradle.api.Project
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.docker.manager.ConnectionMode
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration
import travel.snapshot.qa.inspection.InspectionException
import travel.snapshot.qa.test.execution.tomcat.DeploymentStrategy
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

    private static final TestExecutionMode DEFAULT_EXECUTION_MODE = DEVELOPMENT

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

    static File resolveDataPlatformRepositoryLocation() {

        Project project = new GradleSpaceliftDelegate().project()

        File workspace = project.spacelift.workspace
        String dataPlatformDirectory = project.spacelift.configuration['dataPlatformRepository'].value

        resolveDataPlatformRepositoryLocation(workspace, dataPlatformDirectory)
    }

    static File resolveDataPlatformRepositoryLocation(String dataPlatformPath) {
        File workspace = new GradleSpaceliftDelegate().project().spacelift.workspace

        resolveDataPlatformRepositoryLocation(workspace, dataPlatformPath)
    }

    static File resolveDataPlatformRepositoryLocation(File workspace, String dataPlatformDirectory) {
        File dataPlatformProject

        File dataPlatformDir = new File(dataPlatformDirectory)

        if (dataPlatformDir.isAbsolute()) {
            dataPlatformProject = dataPlatformDir
        } else {
            dataPlatformProject = new File(workspace, dataPlatformDirectory)
        }

        dataPlatformProject
    }

    static def resolveDataPlatformRespositoryCommit(String defaultCommit) {
        System.getProperty("dataPlatformRepositoryCommit", defaultCommit)
    }

    static def resolveDataPlatformQARespositoryCommit(String defaultCommit) {
        System.getProperty("dataPlatformQARepositoryCommit", defaultCommit)
    }

    static def resolveForceDataPlatformBuild() {
        Boolean.parseBoolean(System.getProperty("forceDataPlatformBuild"))
    }

    static def resolveTomcatSpringConfigDirectoryMount() {
        def mount

        if (resolveDockerMode() == HOST.name()) {
            if (SystemUtils.IS_OS_UNIX) {
                mount = new GradleSpaceliftDelegate().project().rootDir.absolutePath + "/configuration"
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
            configurationSource = new GradleSpaceliftDelegate().project().rootDir.absolutePath + "/configuration"
        } else {
            configurationSource = "configuration"
        }

        configurationSource
    }

    static def resolveRepositoryFetchSkip() {
        Boolean.parseBoolean(System.getProperty("repositorySkipFetch"))
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

    /**
     * Returns properties file for DP API tests. When not set, 'dp.properties' is default.
     *
     * This will be set in apiTests Spacelift gradle task as a property.
     *
     * @return properties file for DP API tests
     */
    static def resolveApiTestsDpProperties(DataPlatformOrchestration orchestration) {

        // in case we want to override template dp file
        String dpPropertiesSystemProperty = System.getProperty("dp.properties")

        if (dpPropertiesSystemProperty) {
            logger.info("Going to use ${dpPropertiesSystemProperty} file for API tests")
            return dpPropertiesSystemProperty
        }

        File rootDir = (File) new GradleSpaceliftDelegate().project().rootDir
        File templateDpProperties = new File(rootDir, "configuration/api-tests/template_dp.properties")

        Map containerIPMapping = [:]

        String tomcatIP
        String mariadbIP
        String activemqIP
        String mongodbIP

        if (resolveDockerMode() == MACHINE.name()) {
            try {
                tomcatIP = mariadbIP = activemqIP = mongodbIP = DockerMachineHelper.getIp(resolveDockerMachine())
            } catch (InspectionException ex) {
                throw new InspectionException("Unable to get IP of the container for dp.properties file", ex)
            }
        } else {
            try {
                tomcatIP = orchestration.inspectIP("tomcat")
                mariadbIP = orchestration.inspectIP("mariadb")
                activemqIP = orchestration.inspectIP("activemq")
                mongodbIP = orchestration.inspectIP("mongodb")
            } catch (InspectionException ex) {
                throw new InspectionException("Unable to get IP of the container for dp.properties file", ex)
            }
        }

        File output = Spacelift.task(templateDpProperties, ProcessTemplate)
                .bindings(["tomcat.ip"      : tomcatIP])
                .bindings(["mariadb.ip"     : mariadbIP])
                .bindings(["activemq.ip"    : activemqIP])
                .bindings(["mongodb.ip"     : mongodbIP])
                .execute().await()

        logger.info("Going to use this dp.properties file for API tests")
        logger.info(output.text)

        output.absolutePath
    }

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

}
