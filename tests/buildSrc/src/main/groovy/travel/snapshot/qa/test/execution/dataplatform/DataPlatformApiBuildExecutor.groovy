package travel.snapshot.qa.test.execution.dataplatform

import travel.snapshot.qa.util.Properties

class DataPlatformApiBuildExecutor {

    File dataPlatformAPIDir

    boolean withoutTests = false

    DataPlatformApiBuildExecutor() {
        this.dataPlatformAPIDir = Properties.Location.dataPlatformAPIRepository
    }

    /**
     *
     * @param dataPlatformPath path to data-platform-api repository
     * @return this
     */
    DataPlatformApiBuildExecutor dataPlatformApi(String dataPlatformApiPath) {
        this.dataPlatformAPIDir = Properties.Location.resolveLocation(dataPlatformApiPath)
        this
    }

    /**
     * When called, build will be executed without tests
     *
     * @return this
     */
    DataPlatformApiBuildExecutor withoutTests() {
        withoutTests = true
        this
    }

    /**
     * Builds API
     *
     * @return this
     */
    DataPlatformApiBuildExecutor execute() {
        execute(withoutTests)
        this
    }

    /**
     * Builds API
     *
     * @param withoutTests if set to true, tests will be executed during build as well
     * @return this
     */
    DataPlatformApiBuildExecutor execute(boolean withoutTests) {

        final DataPlatformApiBuilder dataPlatformBuilder = new DataPlatformApiBuilder(dataPlatformAPIDir.absolutePath, withoutTests)

        dataPlatformBuilder.build()

        this
    }
}
