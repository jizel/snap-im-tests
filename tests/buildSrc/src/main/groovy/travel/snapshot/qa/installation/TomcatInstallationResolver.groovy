package travel.snapshot.qa.installation

import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.connection.ConnectionCheck
import travel.snapshot.qa.connection.ConnectionCheckExecutor.TCPConnectionCheckTask
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration
import travel.snapshot.qa.manager.tomcat.docker.TomcatService
import travel.snapshot.qa.util.Properties
import travel.snapshot.qa.util.TestExecutionMode

import static travel.snapshot.qa.util.TestExecutionMode.DEVELOPMENT
import static travel.snapshot.qa.util.TestExecutionMode.TEST

class TomcatInstallationResolver {

    private final DataPlatformTestOrchestration orchestration

    TomcatInstallationResolver(DataPlatformTestOrchestration orchestration) {
        this.orchestration = orchestration
    }

    def resolve() {

        TestExecutionMode executionMode = TestExecutionMode.valueOf(Properties.Test.executionMode)

        switch (executionMode) {
            case DEVELOPMENT:

                // in case DEVELOPMENT mode is chosen, default checking task would check
                // started Tomcat service by repeatedly listing deployments on /manager but in this mode,
                // we are binding directory either from Docker machine or local host to the
                // deployment directory so it will be overlayed and manager will not be deployed afterwards.
                // In this case we just check connection by simple TCP connection check

                final TomcatManagerConfiguration configuration = new TomcatManagerConfiguration.Builder().build()

                final ConnectionCheck connectionCheck = new ConnectionCheck.Builder()
                        .host(configuration.getManagerHost())
                        .port(configuration.getManagerPort())
                        .build()

                final TCPConnectionCheckTask connectionCheckTask = new TCPConnectionCheckTask().connectionCheck(connectionCheck)

                orchestration.with(new TomcatService().init().setCheckingTask(connectionCheckTask))
                break
            case TEST:
                // here we expect that /manager will be deployed in Tomcat while checking the connction
                // so listing of deployments will work
                orchestration.with(new TomcatService().init())
                break
        }
    }
}
