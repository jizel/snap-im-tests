package travel.snapshot.qa.test.execution.log

import org.arquillian.spacelift.Spacelift
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MariaDBServiceLogReporter extends AbstractServiceLogReporter {

    private static final Logger logger = LoggerFactory.getLogger(MariaDBServiceLogReporter)

    private static final List<String> MYSQL_LOGS = new ArrayList() {
        {
            add("/var/lib/mysql/localhost.localdomain.log")
            add("/var/lib/mysql/localhost.localdomain.err")
        }
    }

    @Override
    def report(String container) {
        String reportDirectory = getReportDirectory(container)

        MYSQL_LOGS.each { log ->
            try {
                Spacelift.task("docker").parameters("cp", "${container}:${log}", reportDirectory).execute().await()
            } catch (Exception ex) {
                logger.info("Unable to get log {} from container {}, reason: {}", log, container, ex.getMessage())
            }
        }
    }
}
