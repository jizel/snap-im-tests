package travel.snapshot.qa.test.execution.log

import org.arquillian.spacelift.Spacelift
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MongoDBServiceLogReporter extends AbstractServiceLogReporter {

    private static final Logger logger = LoggerFactory.getLogger(MongoDBServiceLogReporter)

    private static final List<String> MONGO_LOGS = new ArrayList() {
        {
            add("/var/log/mongodb/server.log")
        }
    }

    MongoDBServiceLogReporter(File workspace) {
        super(workspace)
    }

    @Override
    def report(String container) {
        String reportDirectory = getReportDirectory(container)

        MONGO_LOGS.each { log ->
            try {
                Spacelift.task("docker").parameters("cp", "${container}:${log}", reportDirectory).execute().await()
            } catch (Exception ex) {
                logger.info("Unable to get log {} from container {}, reason: {}", log, container, ex.getMessage())
            }
        }
    }
}
