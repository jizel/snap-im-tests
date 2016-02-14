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
import travel.snapshot.qa.util.container.DockerContainer
import travel.snapshot.qa.util.container.DockerIPLogger

class PlatformLifecycle extends BaseContainerizableObject<PlatformLifecycle> implements Test {

    DeferredValue<Boolean> setup = DeferredValue.of(Boolean).from(Boolean.TRUE)

    DeferredValue<Boolean> teardown = DeferredValue.of(Boolean).from(Boolean.TRUE)

    DeferredValue<DataPlatformTestOrchestration> with = DeferredValue.of(DataPlatformTestOrchestration)

    DeferredValue<Void> init = DeferredValue.of(Void)

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

        if (!isInteractingWithDocker()) {
            logger.info("Orchestration setup will be skipped because there is not any docker-like installation selected.")
            return
        }

        def orchestration = with.resolve()

        init.resolve()

        try {
            if (orchestration && setup.resolve()) {
                orchestration.initDockerManagers()
                orchestration.start()
                DockerIPLogger.log(orchestration.get())
            }  else {
                logger.info("Orchestration setup will be skipped because setup closure was resolved to false.")
            }
        } catch (CubeControlException e) {
            handleException(e, logger)
            throw e
        }
    }

    private def orchestrationTeardown(Logger logger) {

        if (!isInteractingWithDocker()) {
            logger.info("Orchestration teardowns will be skipped because there is not any docker-like installation selected.")
            return
        }

        def orchestration = with.resolve()

        if (orchestration && teardown.resolve()) {
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

            System.setProperty("arquillian.xml.connection.mode", ConnectionMode.STARTANDSTOP.name())

            orchestration.initDockerManagers()

            def orchestrationDelegate = orchestration.get()

            new GradleSpaceliftDelegate().project().selectedInstallations
                    .findAll { !it['name'].startsWith("docker") }
                    .each { installation ->
                try {
                    orchestrationDelegate.dockerManager.stop(installation['name'])
                } catch (Exception ex) {
                    throw new RuntimeException(ex)
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
}
