package travel.snapshot.qa.manager.jboss;

import org.arquillian.spacelift.Spacelift;
import org.arquillian.spacelift.execution.CountDownWatch;
import org.arquillian.spacelift.process.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import travel.snapshot.qa.manager.api.configuration.Validate;
import travel.snapshot.qa.manager.api.container.ContainerManagerException;
import travel.snapshot.qa.manager.api.io.ConsoleConsumer;
import travel.snapshot.qa.manager.api.io.ContainerProcessDestroyer;
import travel.snapshot.qa.manager.api.io.ContainerShutdownThread;
import travel.snapshot.qa.manager.jboss.api.JBossContainerManager;
import travel.snapshot.qa.manager.jboss.check.JBossStartChecker;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

import java.util.concurrent.TimeUnit;

public class JBossManager implements JBossContainerManager {

    private static final Logger logger = LoggerFactory.getLogger(JBossManager.class);

    private Thread shutdownThread;

    private Process process;

    private final JBossManagerConfiguration configuration;

    public JBossManager() {
        this(new JBossManagerConfiguration());
    }

    public JBossManager(JBossManagerConfiguration configuration) {
        Validate.notNull(configuration, "Provided configuration to JBossManager is a null object!");
        this.configuration = configuration;
    }

    @Override
    public void start() throws ContainerManagerException {

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

            Spacelift.task(configuration, JBossStartChecker.class)
                    .execute()
                    .until(new CountDownWatch(configuration.getStartupTimeoutInSeconds(), TimeUnit.SECONDS), JBossStartChecker.jbossStartedCondition);

        } catch (Exception e) {
            throw new ContainerManagerException("Could not start JBoss container: ", e);
        }
    }

    @Override
    public void stop() throws ContainerManagerException {
        new ContainerProcessDestroyer(process).shutdownThread(shutdownThread).destroy();
    }

    @Override
    public boolean isRunning() throws ContainerManagerException {
        return Spacelift.task(configuration, JBossStartChecker.class).execute().await();
    }

    @Override
    public JBossManagerConfiguration getConfiguration() {
        return configuration;
    }
}
