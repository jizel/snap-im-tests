package travel.snapshot.qa.test

import com.github.dockerjava.api.InternalServerErrorException
import org.arquillian.cube.spi.CubeControlException
import org.arquillian.spacelift.gradle.BaseContainerizableObject
import org.arquillian.spacelift.gradle.DeferredValue
import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.arquillian.spacelift.gradle.Test
import org.slf4j.Logger
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.manager.ConnectionMode
import travel.snapshot.qa.util.PropertyResolver
import travel.snapshot.qa.util.container.DockerContainer
import travel.snapshot.qa.util.container.DockerIPLogger

import static java.lang.Boolean.TRUE

class PlatformLifecycle extends BaseContainerizableObject<PlatformLifecycle> implements Test {

    DeferredValue<Boolean> setup = DeferredValue.of(Boolean).from(TRUE)

    DeferredValue<Boolean> teardown = DeferredValue.of(Boolean).from(TRUE)

    DeferredValue<DataPlatformTestOrchestration> with = DeferredValue.of(DataPlatformTestOrchestration)

    DeferredValue<Void> init = DeferredValue.of(Void)

    DeferredValue<Void> execute = DeferredValue.of(Void)

    PlatformLifecycle(String name, Object parent) {
        super(name, parent)
    }

    PlatformLifecycle(String name, PlatformLifecycle template) {
        super(name, template)

        this.setup = template.@setup.copy()
        this.teardown = template.@teardown.copy()
        this.with = template.@with.copy()
        this.init = template.@init.copy()
    }

    @Override
    void executeTest(Logger logger) {
        orchestrationSetup(logger)
        orchestrationTeardown(logger)
    }

    @Override
    PlatformLifecycle clone(String name) {
        new PlatformLifecycle(name, this)
    }

    private def orchestrationSetup(Logger logger) {

        if (!setup.resolve()) {
            return
        }

        if (!isInteractingWithDocker()) {
            logger.info("Orchestration setup will be skipped because there is not any docker-like installation selected.")
            return
        }

        def orchestration = with.resolve()

        init.resolve()

        try {
            if (orchestration) {
                orchestration.initDockerManagers()
                orchestration.start()
                DockerIPLogger.log(orchestration.get())
            } else {
                logger.info("Orchestration setup will be skipped because setup closure was resolved to false.")
            }
        } catch (CubeControlException e) {
            handleException(e, logger)
            throw e
        }

        execute.resolve()
    }

    private def orchestrationTeardown(Logger logger) {

        if (!teardown.resolve()) {
            return
        }

        if (!isInteractingWithDocker()) {
            logger.info("Orchestration teardowns will be skipped because there is not any docker-like installation selected.")
            return
        }

        def orchestration = with.resolve()

        def connectionMode = ConnectionMode.valueOf(PropertyResolver.resolveConnectionMode())

        boolean containersAlreadyStarted = orchestration.get().getDockerManager().containersAlreadyStarted()

        boolean shouldStop = false

        if (isPlatformStopProfileSelected()) {
            shouldStop = true
            logger.info("platformStop profile has been selected. Going to shutdown the platform.")
        } else if (isPlatformStopTestSelected()) {
            // for STARTANDSTOP we will just stop them
            // for STARTORCONNECT if we could not connect to them we started them so in that case we will stop them
            // for STARTORCONNECTANDLEAVE we are not stopping them
            if ((connectionMode == ConnectionMode.STARTORCONNECT) && !containersAlreadyStarted) {
                logger.info("plaformStop test was selected, STARTORCONNECT connection mod was set and " +
                        "containers were not started before test execution. Containers will be shut down.")
                shouldStop = true
            } else if (connectionMode == ConnectionMode.STARTANDSTOP) {
                logger.info("platformStop test was selected, STARTANDSTOP connection mode was set. Containers will be " +
                        "shut down.")
                shouldStop = true
            } else {
                if (connectionMode == ConnectionMode.STARTORCONNECTANDLEAVE) {
                    logger.info("platformStop test was selected and STARTORCONNECTANDLEAVE connection mode " +
                            "was set. Containers will not be shut down.")
                }

                if (connectionMode == ConnectionMode.STARTORCONNECT) {
                    logger.info("platformStop test was selected and STARTORCONNECT connection mode was set. " +
                            "Containers were started before test execution. Containers will not be shut down.")
                }
            }
        }

        if (!shouldStop) {
            return
        }

        if (orchestration) {
            if (setup.resolve()) {
                orchestration.initDockerManagers()
                orchestration.stop()
                return
            }

            // we have not set setup to true and teardown is true
            // so we are just stopping the platform
            // we have to set system property for connectionMode in arquillian.xml
            // to STARTANDSTOP because that is the only mode which will stop running containers
            // when already started

            //System.setProperty("arquillian.xml.connection.mode", ConnectionMode.STARTANDSTOP.name())

            orchestration.initDockerManagers()

            new GradleSpaceliftDelegate().project().spacelift.configuration['serviceInstallations'].value.each { installation ->
                try {
                    DockerContainer.removeContainer(installation)
                    // TODO this does not work properly with Cube 1.0.0.Alpha7 and Docker 1.10.1
                    // it works with Docker 1.9.1 and it works with current Cube 1.0.0.Final-SNAPSHOT
                    // once Cube 1.0.0.Final is released, it will be done correctly
                    //orchestration.dockerManager.stop(installation)
                } catch (Exception ex) {
                    logger.warn("Unable to remove container: {}", ex.getMessage())
                }
            }
        } else {
            logger.info("Orchestration teardown will be skipped because teardown closure was resolved to false.")
        }
    }

    private def isInteractingWithDocker() {
        !new GradleSpaceliftDelegate().project().selectedInstallations.findAll {
            it['name'].startsWith("docker")
        }.isEmpty()
    }

    private handleException(CubeControlException ex, Logger logger) {
        Throwable cause = ex.getCause()
        if (cause instanceof InternalServerErrorException) {

            def m = cause.getMessage() =~ /.* address already in use$/

            if (m) {
                List<String> containers = new GradleSpaceliftDelegate().project().spacelift.configuration['serviceInstallations'].value
                DockerContainer.removeContainers("Created", containers)
                DockerContainer.removeContainers("Running", containers)

                logger.error(cause.getMessage())
                logger.error("Try to shut down services you are running locally in order not to block services " +
                        "from binding to already bound ports by them.")

                System.exit(1)
            }

            throw ex
        }
    }

    private boolean isPlatformStopProfileSelected() {
        new GradleSpaceliftDelegate().project().selectedProfile['name'] == "platformStop"
    }

    private boolean isPlatformStopTestSelected() {
        ! new GradleSpaceliftDelegate().project().selectedTests.findAll { test ->
            test['name'] == "platformStop"
        }.isEmpty()
    }
}