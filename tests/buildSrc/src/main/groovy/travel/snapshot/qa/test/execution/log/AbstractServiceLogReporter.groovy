package travel.snapshot.qa.test.execution.log

import org.apache.commons.io.FileUtils
import travel.snapshot.qa.util.DockerMode
import travel.snapshot.qa.util.PropertyResolver

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class AbstractServiceLogReporter {

    boolean hostMode = (PropertyResolver.resolveDockerMode() == DockerMode.HOST.toString())

    String dockerMachine = PropertyResolver.resolveDockerMachine()

    static String dateTime = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

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

        if (hostMode) {
            reportDirectory = new File("snapshot/workspace/reports/host/${dateTime}/${container}")
        } else {
            reportDirectory = new File("snapshot/workspace/reports/machine/${dockerMachine}/${dateTime}/${container}")
        }

        FileUtils.deleteQuietly(reportDirectory)
        reportDirectory.mkdirs()
        reportDirectory.absolutePath
    }
}
