package travel.snapshot.qa.test

import org.arquillian.spacelift.gradle.BaseContainerizableObject
import org.arquillian.spacelift.gradle.DeferredValue
import org.arquillian.spacelift.gradle.Test
import org.slf4j.Logger

class DataPlatformTest extends BaseContainerizableObject<DataPlatformTest> implements Test {

    // lifecycle closures

    DeferredValue<List> dataProvider = DeferredValue.of(List)

    DeferredValue<List<String>> data = DeferredValue.of(List).from([null])

    // this will be resolved as the first execution in beforeSuite closure
    DeferredValue<Void> init = DeferredValue.of(Void)

    DeferredValue<Void> beforeSuite = DeferredValue.of(Void)

    DeferredValue<Void> beforeTest = DeferredValue.of(Void)

    DeferredValue<Void> execute = DeferredValue.of(Void)

    DeferredValue<Void> afterTest = DeferredValue.of(Void)

    DeferredValue<Void> afterSuite = DeferredValue.of(Void)

    DeferredValue<Void> report = DeferredValue.of(Void)

    DataPlatformTest(String name, Object parent) {
        super(name, parent)

        this.dataProvider.from({ data.resolve() as ArrayList })
        beforeSuite.from({ init.resolve() })
        afterSuite.from({ report.resolve() })
    }

    DataPlatformTest(String name, DataPlatformTest template) {
        super(name, template)

        this.dataProvider = template.@dataProvider.copy()
        this.data = template.@data.copy()
        this.init = template.@init.copy()
        this.beforeSuite = template.@beforeSuite.copy()
        this.beforeTest = template.@beforeTest.copy()
        this.execute = template.@execute.copy()
        this.afterTest = template.@afterTest.copy()
        this.afterSuite = template.@afterSuite.copy()
        this.report = template.@report.copy()
    }

    @Override
    DataPlatformTest clone(String name) {
        new DataPlatformTest(name, this)
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
}
