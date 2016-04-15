package travel.snapshot.qa.manager.tomcat;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.CountDownWatch;
import org.arquillian.spacelift.process.Command;
import org.jboss.shrinkwrap.api.Archive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.BasicWaitingCondition;
import travel.snapshot.qa.manager.api.container.ContainerDeploymentException;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.api.io.ConsoleConsumer;
import travel.snapshot.qa.manager.api.io.ContainerProcessDestroyer;
import travel.snapshot.qa.manager.api.io.ContainerShutdownThread;
import travel.snapshot.qa.manager.tomcat.api.DeploymentFilter;
import travel.snapshot.qa.manager.tomcat.api.DeploymentRecord;
import travel.snapshot.qa.manager.tomcat.api.DeploymentRecordsBuilderException;
import travel.snapshot.qa.manager.tomcat.api.DeploymentState;
import travel.snapshot.qa.manager.tomcat.api.Deployments;
import travel.snapshot.qa.manager.tomcat.api.TomcatContainerManager;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseBody;
import travel.snapshot.qa.manager.tomcat.check.TomcatStartedCheckTask;
import travel.snapshot.qa.manager.tomcat.command.TomcatListCommand;
import travel.snapshot.qa.manager.tomcat.command.deployment.TomcatDeployCommand;
import travel.snapshot.qa.manager.tomcat.command.deployment.TomcatFileDeployCommand;
import travel.snapshot.qa.manager.tomcat.command.deployment.TomcatFileUndeployCommand;
import travel.snapshot.qa.manager.tomcat.command.deployment.TomcatUndeployCommand;
import travel.snapshot.qa.manager.tomcat.command.deployment.lifecycle.TomcatReloadDeploymentCommand;
import travel.snapshot.qa.manager.tomcat.command.deployment.lifecycle.TomcatStartDeploymentCommand;
import travel.snapshot.qa.manager.tomcat.command.deployment.lifecycle.TomcatStopDeploymentCommand;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.impl.TomcatArchiveDeployCommandExecutor;
import travel.snapshot.qa.manager.tomcat.impl.TomcatArchiveUndeployCommandExecutor;
import travel.snapshot.qa.manager.tomcat.impl.TomcatCommandBuilder;
import travel.snapshot.qa.manager.tomcat.impl.TomcatCommandExecutor;
import travel.snapshot.qa.manager.tomcat.impl.TomcatFileDeployCommandExecutor;
import travel.snapshot.qa.manager.tomcat.impl.TomcatFileUndeployCommandExecutor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Tomcat manager which is returned after container is started by respective task. This object has to be chained to
 * stopper task.
 *
 * @see travel.snapshot.qa.manager.tomcat.spacelift.TomcatStarter
 * @see travel.snapshot.qa.manager.tomcat.spacelift.TomcatStopper
 */
public class TomcatManager implements TomcatContainerManager {

    private static final Logger logger = LoggerFactory.getLogger(TomcatManager.class);

    private ContainerShutdownThread shutdownThread;

    private Process process;

    private final TomcatManagerConfiguration configuration;

    private final TomcatCommandExecutor commandExecutor;

    public TomcatManager() {
        this(new TomcatManagerConfiguration.Builder().build());
    }

    public TomcatManager(TomcatManagerConfiguration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration must not be a null object.");
        }

        this.configuration = configuration;

        commandExecutor = new TomcatCommandExecutor(configuration);
    }

    @Override
    public void start() throws ContainerManagerException {

        if (Spacelift.task(configuration, TomcatStartedCheckTask.class).execute().await()) {
            throw new ContainerManagerException("Tomcat container is already running!");
        }

        try {
            final Command command = new TomcatCommandBuilder().build(configuration);

            logger.info("Starting Tomcat container with: {}.", command.toString());

            final ProcessBuilder processBuilder = new ProcessBuilder(command.getFullCommand());
            processBuilder.redirectErrorStream(true);

            process = processBuilder.start();

            ConsoleConsumer consoleConsumer = new ConsoleConsumer(process, logger).setOutputToConsole(configuration.isOutputToConsole());
            Thread consoleThread = new Thread(consoleConsumer);
            consoleThread.start();

            shutdownThread = new ContainerShutdownThread(process);

            // it is important to have this shutdown hook added before the next check
            // otherwise we would start the container but it would be never killed upon JVM termination
            Runtime.getRuntime().addShutdownHook(shutdownThread);

            Spacelift.task(configuration, TomcatStartedCheckTask.class).execute().until(
                    new CountDownWatch(configuration.getStartupTimeoutInSeconds(), TimeUnit.SECONDS),
                    new BasicWaitingCondition());
        } catch (Exception ex) {
            throw new ContainerManagerException("Could not start Tomcat container.", ex);
        }

    }

    @Override
    public void stop() throws ContainerManagerException {
        new ContainerProcessDestroyer(process).shutdownThread(shutdownThread).destroy();
    }

    @Override
    public boolean isRunning() throws ContainerManagerException {
        if (configuration.isRemote()) {
            return canListDeployments();
        }

        return process != null && process.isAlive() && canListDeployments();
    }

    @Override
    public TomcatResponse deploy(String archiveFilePath) {
        try {
            final TomcatFileDeployCommandExecutor deployExecutor = new TomcatFileDeployCommandExecutor(configuration);
            return deployExecutor.execute(new TomcatFileDeployCommand(archiveFilePath));
        } catch (ContainerManagerException ex) {
            throw new ContainerDeploymentException(String.format("Unable to deploy archive %s.", archiveFilePath), ex);
        }
    }

    @Override
    public TomcatResponse deploy(Archive<?> deployment) throws ContainerDeploymentException {
        try {
            final TomcatArchiveDeployCommandExecutor deployExecutor =
                    new TomcatArchiveDeployCommandExecutor(configuration);
            return deployExecutor.execute(new TomcatDeployCommand(deployment));
        } catch (ContainerManagerException ex) {
            throw new ContainerDeploymentException(String.format("Unable to deploy archive %s.", deployment.getName()), ex);
        }
    }

    @Override
    public TomcatResponse undeploy(Archive<?> undeployment) throws ContainerDeploymentException {
        try {
            final TomcatArchiveUndeployCommandExecutor undeployExecutor =
                    new TomcatArchiveUndeployCommandExecutor(configuration);
            return undeployExecutor.execute(new TomcatUndeployCommand(undeployment));
        } catch (ContainerManagerException ex) {
            throw new ContainerDeploymentException(String.format("Unable to undeploy archive %s.",
                    undeployment.getName()), ex);
        }
    }

    @Override
    public TomcatResponse undeploy(String archiveFilePath) throws ContainerDeploymentException {
        try {
            final TomcatFileUndeployCommandExecutor undeployExecutor =
                    new TomcatFileUndeployCommandExecutor(configuration);
            return undeployExecutor.execute(new TomcatFileUndeployCommand(archiveFilePath));
        } catch (ContainerManagerException ex) {
            throw new ContainerDeploymentException(String.format("Unable to undeploy archive %s.",
                    archiveFilePath), ex);
        }
    }

    @Override
    public boolean isDeployed(String deployment) throws ContainerManagerException {
        DeploymentRecord deploymentRecord = listDeployments().getDeployment(deployment);
        return deploymentRecord != null && deploymentRecord.isRunning();
    }

    @Override
    public Deployments listDeployments() throws ContainerManagerException {
        TomcatResponse response = commandExecutor.execute(new TomcatListCommand());

        List<DeploymentRecord> deploymentRecords;

        try {
            final TomcatResponseBody responseBody = response.getResponseBody();
            deploymentRecords = new DeploymentRecord.DeploymentRecordsBuilder(responseBody).build();
        } catch (DeploymentRecordsBuilderException ex) {
            throw new ContainerManagerException(String.format("Unable to list deployments: %s.", ex.getMessage()));
        }

        return new Deployments(deploymentRecords);
    }

    @Override
    public Deployments listDeployments(DeploymentState state) throws ContainerManagerException {
        return new DeploymentFilter(state).filter(listDeployments());
    }

    @Override
    public TomcatResponse startDeployment(String deployment) throws ContainerDeploymentException {
        try {
            return commandExecutor.execute(new TomcatStartDeploymentCommand(deployment));
        } catch (ContainerManagerException ex) {
            throw new ContainerDeploymentException(ex);
        }
    }

    @Override
    public TomcatResponse stopDeployment(String deployment) throws ContainerDeploymentException {
        try {
            return commandExecutor.execute(new TomcatStopDeploymentCommand(deployment));
        } catch (ContainerManagerException ex) {
            throw new ContainerDeploymentException(ex);
        }
    }

    @Override
    public TomcatResponse reloadDeployment(String deployment) throws ContainerDeploymentException {
        try {
            return commandExecutor.execute(new TomcatReloadDeploymentCommand(deployment));
        } catch (ContainerManagerException ex) {
            throw new ContainerDeploymentException(ex);
        }
    }

    @Override
    public TomcatManagerConfiguration getConfiguration() {
        return configuration;
    }

    private boolean canListDeployments() {
        try {
            listDeployments();
            return true;
        } catch (ContainerDeploymentException ex) {
            return false;
        }
    }
}
