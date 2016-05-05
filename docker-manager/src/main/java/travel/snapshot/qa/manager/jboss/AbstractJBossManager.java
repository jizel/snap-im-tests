package travel.snapshot.qa.manager.jboss;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.arquillian.spacelift.execution.CountDownWatch;
import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.task.Task;
import org.jboss.as.controller.client.ModelControllerClient;
import org.jboss.shrinkwrap.api.Archive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.configuration.Validate;
import travel.snapshot.qa.manager.api.container.ContainerDeploymentException;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.api.io.ConsoleConsumer;
import travel.snapshot.qa.manager.api.io.ContainerProcessDestroyer;
import travel.snapshot.qa.manager.api.io.ContainerShutdownThread;
import travel.snapshot.qa.manager.jboss.api.JBossContainerDeployer;
import travel.snapshot.qa.manager.jboss.api.JBossContainerManager;
import travel.snapshot.qa.manager.jboss.configuration.JBossCommandBuilder;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

import java.io.File;

abstract class AbstractJBossManager<T extends Object, U extends ModelControllerClient> implements JBossContainerManager, JBossContainerDeployer {

    private static final Logger logger = LoggerFactory.getLogger(AbstractJBossManager.class);

    private final JBossManagerConfiguration configuration;

    private Thread shutdownThread;

    private Process process;

    AbstractJBossManager(final JBossManagerConfiguration configuration) {
        Validate.notNull(configuration, "Provided configuration is a null object!");
        this.configuration = configuration;
    }

    public abstract T getManagementClient();

    public abstract U getModelControllerClient();

    public abstract void closeManagementClient(T managementClient);

    abstract Task<T, Boolean> getStartingTask();

    @Override
    public JBossManagerConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String deploy(String archiveFilePath) throws ContainerDeploymentException {
        return getDeployer().deploy(archiveFilePath);
    }

    @Override
    public String deploy(File archiveFile) throws ContainerDeploymentException {
        return getDeployer().deploy(archiveFile);
    }

    @Override
    public String deploy(Archive<?> deployment) throws ContainerDeploymentException {
        return getDeployer().deploy(deployment);
    }

    @Override
    public void undeploy(String runtimeName) throws ContainerDeploymentException {
        getDeployer().undeploy(runtimeName);
    }

    @Override
    public void start() throws ContainerManagerException {

        if (isRunning()) {
            throw new ContainerManagerException("JBoss container is already running.");
        }

        if (configuration.isRemote()) {
            throw new IllegalStateException("Starting of JBoss container is allowed only if 'remote' is false.");
        }

        try {
            Command command = new JBossCommandBuilder().build(configuration);

            logger.info("Starting JBoss container with {}.", command.toString());

            ProcessBuilder processBuilder = new ProcessBuilder(command.getFullCommand());
            processBuilder.redirectErrorStream(true);

            process = processBuilder.start();

            ConsoleConsumer consoleConsumer = new ConsoleConsumer(process, logger).setOutputToConsole(configuration.isOutputToConsole());
            Thread consoleThread = new Thread(consoleConsumer);
            consoleThread.start();

            shutdownThread = new ContainerShutdownThread(process);

            // it is important to have this shutdown hook added before the next check
            // otherwise we would start the container but it would be never killed upon JVM termination
            Runtime.getRuntime().addShutdownHook(shutdownThread);

            getStartingTask()
                    .execute()
                    .until(new CountDownWatch(configuration.getStartupTimeoutInSeconds(), SECONDS), started -> started);

        } catch (Exception e) {
            throw new ContainerManagerException("Could not start JBoss container: ", e);
        }
    }

    @Override
    public void stop() throws ContainerManagerException {
        new ContainerProcessDestroyer(process).shutdownThread(shutdownThread).destroy();
        closeManagementClient(getManagementClient());
    }
}
