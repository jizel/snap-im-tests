package travel.snapshot.qa.test.execution.log

import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.docker.ServiceCubePair
import travel.snapshot.qa.docker.ServiceType
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration
import travel.snapshot.qa.util.ProjectHelper

class ContainerLogReporter {

    private DataPlatformTestOrchestration testOrchestration

    private List<String> services = []

    ContainerLogReporter(DataPlatformTestOrchestration testOrchestration, List<String> services) {
        this.testOrchestration = testOrchestration
        this.services = services
    }

    def report() {

        if (!ProjectHelper.dockerUsed || !testOrchestration || !testOrchestration.get() || !services || services.isEmpty()) {
            return
        }

        DataPlatformOrchestration orchestration = testOrchestration.get()

        for (ServiceType serviceType : ServiceType.values()) {
            List<String> resolvedContainers = resolveContainers(serviceType, services, orchestration)
            resolveReport(serviceType, resolvedContainers)
        }
    }

    private void resolveReport(ServiceType serviceType, List<String> containers) {
        switch (serviceType) {
            case ServiceType.TOMCAT:
                new TomcatServiceLogReporter().report(containers)
                break
            case ServiceType.ACTIVEMQ:
                new ActiveMQServiceLogReporter().report(containers)
                break
            case ServiceType.MARIADB:
                new MariaDBServiceLogReporter().report(containers)
                break
            case ServiceType.MONGODB:
                new MongoDBServiceLogReporter().report(containers)
                break
            case ServiceType.JBOSS_DOMAIN:
                // TODO
            case ServiceType.JBOSS_STANDALONE:
                // TODO
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
