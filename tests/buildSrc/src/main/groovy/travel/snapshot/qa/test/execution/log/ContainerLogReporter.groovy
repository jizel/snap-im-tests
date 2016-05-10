package travel.snapshot.qa.test.execution.log

import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.ServiceType
import travel.snapshot.qa.docker.ServiceCubePair
import travel.snapshot.qa.docker.orchestration.Orchestration
import travel.snapshot.qa.util.ProjectHelper

import static travel.snapshot.qa.ServiceType.*

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

        Orchestration orchestration = testOrchestration.get()

        List<ServiceType> unsupportedServices = [ GENERIC, REDIS, JBOSS_DOMAIN, JBOSS_STANDALONE ]

        for (ServiceType serviceType : ServiceType.values()) {
            if (!unsupportedServices.contains(serviceType)) {
                List<String> resolvedContainers = resolveContainers(serviceType, services, orchestration)
                resolveReport(serviceType, resolvedContainers)
            }
        }
    }

    private static void resolveReport(ServiceType serviceType, List<String> containers) {
        switch (serviceType) {
            case TOMCAT:
                new TomcatServiceLogReporter().report(containers)
                break
            case ACTIVEMQ:
                new ActiveMQServiceLogReporter().report(containers)
                break
            case MARIADB:
                new MariaDBServiceLogReporter().report(containers)
                break
            case MONGODB:
                new MongoDBServiceLogReporter().report(containers)
                break
            case JBOSS_DOMAIN:
                // TODO
            case JBOSS_STANDALONE:
                // TODO
                break
            case GENERIC:
                // not supported
                break
            case REDIS:
                // not supported
                break
            default:
                throw new IllegalStateException(String.format("Unable to resolve reporting for service type %s.", serviceType.name()))
        }
    }

    private static List<String> resolveContainers(ServiceType serviceType, List<String> services, Orchestration orchestration) {
        orchestration.getStartedContainers().findAll { ServiceCubePair container ->
            container.serviceName == serviceType.name() && services.contains(container.getCube().getId())
        }.collect { it.getCube().getId() }
    }
}
