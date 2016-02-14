package travel.snapshot.qa.test.execution.log

import org.arquillian.spacelift.Spacelift
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TomcatServiceLogReporter extends AbstractServiceLogReporter {

    private static final Logger logger = LoggerFactory.getLogger(TomcatServiceLogReporter)

    private static final String TOMCAT_LOGS_DIRECTORY = "/opt/tomcat/logs"

    TomcatServiceLogReporter(File workspace) {
        super(workspace)
    }

    @Override
    def report(String container) {
        try {
            String reportDirectory = getReportDirectory(container)
            Spacelift.task("docker").parameters("cp", "${container}:${TOMCAT_LOGS_DIRECTORY}", reportDirectory).execute().await()
        } catch (Exception ex) {
            logger.info("Unable to get logs from container {}, reason: {}", container, ex.getMessage())
        }
    }
}
