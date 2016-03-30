package travel.snapshot.qa.test.execution.threescale

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.process.Command
import org.arquillian.spacelift.process.CommandBuilder
import org.arquillian.spacelift.process.ProcessResult
import org.arquillian.spacelift.task.os.CommandTool
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ThreeScaleHttpUploadHelper {

    private static final Logger logger = LoggerFactory.getLogger(ThreeScaleHttpUploadHelper)

    private static final String THREE_SCALE_BASE_URL = "https://snapshot-admin.3scale.net/admin/api/active_docs"

    private String providerKey

    private File dataPlatformApiPath

    ThreeScaleHttpUploadHelper providerKey(String providerKey) {
        this.providerKey = providerKey
        this
    }

    ThreeScaleHttpUploadHelper dataPlatformApiPath(File dataPlatformApiPath) {
        this.dataPlatformApiPath = dataPlatformApiPath
        this
    }

    def upload(ThreeScaleDataPlatformApiModule module, ThreeScaleApiEnvironment environment) {

        String threeScaleUploadUrl = constructUrl(providerKey, module.threeScaleModuleID.getID(environment))

        String jsonBody = readJsonBody(dataPlatformApiPath, module)

        try {
            Command command = new CommandBuilder("curl")
                    .parameters("-v", "-X", "PUT", "-F", "body=${jsonBody}", threeScaleUploadUrl)
                    .build()

            ProcessResult result = Spacelift.task(CommandTool)
                    .command(command)
                    .execute()
                    .await()

            if (logger.debugEnabled) {
                logger.debug("${module.name} - ${environment}: " + result.output())
            }
        } catch (Exception ex) {
            logger.error("Unable to upload to ${threeScaleUploadUrl}: " + ex.getMessage())
            return
        }

        logger.info("Uploading of {} to {} has finished.", module.name, environment)
    }

    private String constructUrl(String providerKey, int id) {
        String.format("${THREE_SCALE_BASE_URL}/${id}.xml?provider_key=${providerKey}")
    }

    private String readJsonBody(File dataPlatformApiPath, ThreeScaleDataPlatformApiModule module) {
        new File(dataPlatformApiPath, module.swaggerFilePath).text
    }
}
