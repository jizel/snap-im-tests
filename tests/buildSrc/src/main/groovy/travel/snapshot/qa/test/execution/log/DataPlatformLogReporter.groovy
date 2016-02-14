package travel.snapshot.qa.test.execution.log

import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.ServiceCubePair
import travel.snapshot.qa.docker.ServiceType
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration

class DataPlatformLogReporter {

    private DataPlatformTestOrchestration testOrchestration

    private List<String> services = []

    private File workspace

    DataPlatformLogReporter(DataPlatformTestOrchestration testOrchestration, List<String> services, File workspace) {
        this.testOrchestration = testOrchestration
        this.services = services
        this.workspace = workspace
    }

    def report() {

        if (!testOrchestration || !testOrchestration.get() || !services || services.isEmpty()) {
            return
        }

        DataPlatformOrchestration orchestration = testOrchestration.get()

        for (ServiceType serviceType : ServiceType.values()) {
            List<String> resolvedContainers = resolveContainers(serviceType, services, orchestration)
            resolveReport(workspace, serviceType, resolvedContainers)
        }
    }

    private void resolveReport(File workspace, ServiceType serviceType, List<String> containers) {
        switch (serviceType) {
            case ServiceType.TOMCAT:
                new TomcatServiceLogReporter(workspace).report(containers)
                break
            case ServiceType.ACTIVEMQ:
                new ActiveMQServiceLogReporter(workspace).report(containers)
                break
            case ServiceType.MARIADB:
                new MariaDBServiceLogReporter(workspace).report(containers)
                break
            case ServiceType.MONGODB:
                new MongoDBServiceLogReporter(workspace).report(containers)
                break
            default:
                throw new IllegalStateException(String.format("Unable to resolve reporting for service type %s.", serviceType.name()))
        }
    }

    private List<String> resolveContainers(ServiceType serviceType, List<String> services, DataPlatformOrchestration orchestration) {
        orchestration.getStartedContainers().findAll { ServiceCubePair container ->
            container.serviceType == serviceType && services.contains(container.getCube().getId())
        }.collect { it.getCube().getId() }
    }
}
