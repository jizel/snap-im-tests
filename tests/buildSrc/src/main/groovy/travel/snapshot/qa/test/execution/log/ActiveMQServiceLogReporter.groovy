package travel.snapshot.qa.test.execution.log

import org.arquillian.spacelift.Spacelift
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class ActiveMQServiceLogReporter extends AbstractServiceLogReporter {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQServiceLogReporter)

    private static final List<String> ACTIVEMQ_LOGS = new ArrayList() {
        {
            add("/opt/activemq/data/activemq.log")
            add("/opt/activemq/data/audit.log")
        }
    }

    @Override
    def report(String container) {
        String reportDirectory = getReportDirectory(container)

        ACTIVEMQ_LOGS.each { log ->
            try {
                Spacelift.task("docker").parameters("cp", "${container}:${log}", reportDirectory).execute().await()
            } catch (Exception ex) {
                logger.info("Unable to get log {} from container {}, reason: {}", log, container, ex.getMessage())
            }
        }
    }
}
