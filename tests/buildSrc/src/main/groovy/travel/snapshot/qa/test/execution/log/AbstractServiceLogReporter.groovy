package travel.snapshot.qa.test.execution.log

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.SystemUtils
import travel.snapshot.qa.util.DockerMode
import travel.snapshot.qa.util.Properties

abstract class AbstractServiceLogReporter {

    /**
     * Gets logs from container of specified service type to local host
     *
     * @param container container to be reported
     */
    abstract def report(String container)

    /**
     * Reports all containers of specified service type
     *
     * @param containers containers to be reported
     */
    def report(List<String> containers) {
        containers.each { container ->
            report(container)
        }
    }

    /**
     * Resolves directory on local host where logs from container will be resolved.
     *
     * @param container container to get reporting directory for
     * @return directory , where logs for some container will be saved
     */
    String getReportDirectory(String container) {

        File reportDirectory

        if (Properties.Docker.mode == DockerMode.HOST.toString()) {
            reportDirectory = new File("snapshot/workspace/reports/host/${container}")
        } else {
            reportDirectory = new File("snapshot/workspace/reports/machine/${Properties.Docker.machineName}/${container}")
        }

        FileUtils.deleteQuietly(reportDirectory)

        reportDirectory.mkdirs()

        getPlatformSpecificPath(reportDirectory)
    }

    private def getPlatformSpecificPath(File reportDirectory) {
        if (SystemUtils.IS_OS_UNIX) {
            return reportDirectory.absolutePath
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return sanitizeWindowsPath(reportDirectory.absolutePath)
        }

        throw new IllegalStateException("Platfrom not supported.");
    }

    private def sanitizeWindowsPath(String reportDirectory) {
        extractPath(reportDirectory)
    }

    private def extractWindowsDisk(String reportDirectory) {
        reportDirectory.tokenize(":").get(0).toLowerCase()
    }

    private def extractPath(String reportDirectory) {
        reportDirectory.tokenize(":").get(1).replaceAll("\\\\", "/")
    }
}
