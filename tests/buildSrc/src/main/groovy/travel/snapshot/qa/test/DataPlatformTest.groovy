package travel.snapshot.qa.test

import org.arquillian.spacelift.gradle.BaseContainerizableObject
import org.arquillian.spacelift.gradle.DeferredValue
import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.arquillian.spacelift.gradle.Test
import org.slf4j.Logger
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.manager.ConnectionMode

class DataPlatformTest extends BaseContainerizableObject<DataPlatformTest> implements Test {

    // lifecycle closures

    DeferredValue<Void> execute = DeferredValue.of(Void)

    DeferredValue<List> dataProvider = DeferredValue.of(List).from([null])

    // this will be resolved as the first execution in beforeSuite closure
    DeferredValue<Void> init = DeferredValue.of(Void)

    DeferredValue<Void> beforeSuite = DeferredValue.of(Void)

    DeferredValue<Void> beforeTest = DeferredValue.of(Void)

    DeferredValue<Void> afterSuite = DeferredValue.of(Void)

    DeferredValue<Void> afterTest = DeferredValue.of(Void)

    DeferredValue<List<String>> data = DeferredValue.of(List).from([null])

    DeferredValue<Boolean> setup = DeferredValue.of(Boolean).from(Boolean.TRUE)

    DeferredValue<Boolean> teardown = DeferredValue.of(Boolean).from(Boolean.TRUE)

    DeferredValue<DataPlatformTestOrchestration> with = DeferredValue.of(DataPlatformTestOrchestration)

    DataPlatformTest(String testName, Object parent) {
        super(testName, parent)

        dataProvider.from({ data.resolve() as ArrayList })
        beforeSuite.from({ init.resolve(); orchestrationSetup() })
        afterSuite.from({ orchestrationTeardown() })
    }

    @Override
    void executeTest(Logger logger) {
        // before suite
        try {
            logger.info(":test:${name} before suite execution")
            beforeSuite.resolve()
        }
        catch (Exception e) {
            logger.error(":test:${name} failed before suite phase: ${e.getMessage()}")
            throw e
        }

        Exception cause = null

        // in case anything in this try block fails, we will still run the `after suite` in the finally block
        try {
            // iterate through beforeTest, execute and afterTest based on data provider
            dataProvider.resolve().each { data ->

                String dataString = data ? " (${data})" : ""

                try {
                    logger.info(":test:${name} before test execution${dataString}")
                    beforeTest.resolveWith(this, data)
                }
                catch (Exception e) {
                    logger.error(":test:${name} failed before test phase: ${e.getMessage()}")
                    throw e
                }

                // in case anything in this try block fails, we will still run the `after test` in the finally block
                try {
                    logger.invokeMethod("lifecycle", ":test:${name}${dataString}")
                    execute.resolveWith(this, data)
                }
                catch (Exception e) {
                    logger.error(":test:${name} failed execute phase: ${e.getMessage()}")
                    cause = e
                }
                // clean up
                finally {
                    try {
                        logger.info(":test:${name} after test execution${dataString}")
                        afterTest.resolveWith(this, data)
                    }
                    catch (Exception e) {
                        logger.error(":test:${name} failed after test phase: ${e.getMessage()}")
                        if (cause == null) {
                            cause = e
                        }
                    }
                    if (cause) {
                        throw cause
                    }
                }
            }
        } finally {
            // after suite
            try {
                logger.info(":test:${name} after suite execution")
                afterSuite.resolve()
            }
            catch (Exception e) {
                logger.error(":test:${name} failed after suite phase: ${e.getMessage()}")
                if (cause == null) {
                    cause = e
                }
            }
            finally {
                if (cause) {
                    throw cause
                }
            }
        }
    }

    @Override
    DataPlatformTest clone(String name) {
        new DataPlatformTest(name, this)
    }

    private def orchestrationSetup() {
        def orchestration = with.resolve()

        if (orchestration && setup.resolve()) {
            orchestration.initDockerManagers()
            orchestration.start()
        }
    }

    private def orchestrationTeardown() {

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
                    println "stopping " + installation['name']
                    orchestrationDelegate.dockerManager.stop(installation['name'])
                } catch (Exception ex) {
                    throw new RuntimeException(ex)
                }
            }
        }
    }
}
