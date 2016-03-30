package travel.snapshot.qa.test.execution.threescale

import travel.snapshot.qa.util.Properties

class ThreeScaleUploadExecutor {

    private File dataPlatformAPIDir

    private ThreeScaleApiEnvironment environment

    ThreeScaleUploadExecutor() {
        this.dataPlatformAPIDir = Properties.Location.dataPlatformAPIRepository
        this.environment = Properties.ThreeScale.environment
    }

    ThreeScaleUploadExecutor dataPlatformApi(String dataPlatformApiPath) {
        this.dataPlatformAPIDir = Properties.Location.resolveLocation(dataPlatformApiPath)
        this
    }

    ThreeScaleUploadExecutor environment(ThreeScaleApiEnvironment environment) {
        this.environment = environment
        this
    }

    def execute() {

        final ThreeScaleHttpUploadHelper helper = new ThreeScaleHttpUploadHelper()

        helper.providerKey(Properties.ThreeScale.providerKey)
        helper.dataPlatformApiPath(dataPlatformAPIDir)

        for (ThreeScaleDataPlatformApiModule module : ThreeScaleDataPlatformApiModule.values()) {
            helper.upload(module, environment)
        }
    }
}
