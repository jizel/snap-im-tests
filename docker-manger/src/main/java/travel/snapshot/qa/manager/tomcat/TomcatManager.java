package travel.snapshot.qa.manager.tomcat;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.CountDownWatch;
import org.arquillian.spacelift.process.Command;
import org.jboss.shrinkwrap.api.Archive;
import travel.snapshot.qa.manager.tomcat.api.ContainerDeploymentException;
import travel.snapshot.qa.manager.tomcat.api.ContainerManager;
import travel.snapshot.qa.manager.tomcat.api.ContainerManagerException;
import travel.snapshot.qa.manager.tomcat.api.DeploymentFilter;
import travel.snapshot.qa.manager.tomcat.api.DeploymentRecord;
import travel.snapshot.qa.manager.tomcat.api.DeploymentRecordsBuilderException;
import travel.snapshot.qa.manager.tomcat.api.DeploymentState;
import travel.snapshot.qa.manager.tomcat.api.Deployments;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponse;
import travel.snapshot.qa.manager.tomcat.api.response.TomcatResponseBody;
import travel.snapshot.qa.manager.tomcat.check.TomcatStartChecker;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Tomcat manager which is returned after container is started by respective task. This object has to be chained to
 * stopper task.
 *
 * @see travel.snapshot.qa.manager.tomcat.spacelift.TomcatStarter
 * @see travel.snapshot.qa.manager.tomcat.spacelift.TomcatStopper
 */
public class TomcatManager implements ContainerManager {

    private static final Logger logger = Logger.getLogger(TomcatManager.class.getName());

    private Thread shutdownThread;

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

        if (Spacelift.task(configuration, TomcatStartChecker.class).execute().await()) {
            throw new ContainerManagerException("Tomcat container is already running!");
        }

        try {
            final Command command = new TomcatCommandBuilder().build(configuration);

            logger.info(String.format("Starting Tomcat container with: %s.", command.toString()));

            final ProcessBuilder processBuilder = new ProcessBuilder(command.getFullCommand());
            processBuilder.redirectErrorStream(true);

            process = processBuilder.start();

            ConsoleConsumer consoleConsumer = new ConsoleConsumer(process, configuration);
            Thread consoleThread = new Thread(consoleConsumer);
            consoleThread.start();

            shutdownThread = new Thread(() -> {

                if (process != null) {
                    process.destroy();
                    try {
                        process.waitFor();
                    } catch (final InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // it is important to have this shutdown hook adding before the next check
            // otherwise we would start the container but it would be never killed upon JVM termination
            Runtime.getRuntime().addShutdownHook(shutdownThread);

            Spacelift.task(configuration, TomcatStartChecker.class).execute().until(
                    new CountDownWatch(configuration.getStartupTimeoutInSeconds(), TimeUnit.SECONDS),
                    TomcatStartChecker.TOMCAT_STARTED_CONDITION);
        } catch (Exception ex) {
            throw new ContainerManagerException("Could not start Tomcat container.", ex);
        }

    }

    @Override
    public void stop() throws ContainerManagerException {
        if (shutdownThread != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownThread);
            shutdownThread = null;
        }

        try {
            if (process != null) {
                process.destroy();
                process.waitFor();
                process = null;
            }
        } catch (Exception ex) {
            throw new ContainerManagerException("Could not stop Tomcat container.", ex);
        }
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

    private static class ConsoleConsumer implements Runnable {

        private final Process process;

        private final TomcatManagerConfiguration configuration;

        public ConsoleConsumer(final Process process, final TomcatManagerConfiguration configuration) {
            this.process = process;
            this.configuration = configuration;
        }

        @Override
        public void run() {
            final InputStream stream = process.getInputStream();

            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));

            final boolean writeOutput = configuration.isOutputToConsole();

            String line;

            try {
                while ((line = reader.readLine()) != null) {
                    if (writeOutput) {
                        logger.info(line);
                    }
                }
            } catch (IOException e) {
                // intentionally empty
            } finally {
                try {
                    reader.close();
                } catch (IOException ex) {
                    // intentionally empty
                }
            }
        }
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
