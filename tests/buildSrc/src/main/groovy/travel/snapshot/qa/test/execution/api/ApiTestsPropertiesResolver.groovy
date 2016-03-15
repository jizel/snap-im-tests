package travel.snapshot.qa.test.execution.api

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.text.ProcessTemplate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import travel.snapshot.qa.inspection.InspectionException
import travel.snapshot.qa.util.ProjectHelper
import travel.snapshot.qa.util.PropertyResolver

import static travel.snapshot.qa.util.DockerMode.MACHINE
import static travel.snapshot.qa.util.PropertyResolver.resolveContainerIP
import static travel.snapshot.qa.util.PropertyResolver.resolveDockerMachine
import static travel.snapshot.qa.util.machine.DockerMachineHelper.getIp

class ApiTestsPropertiesResolver {

    private static final Logger logger = LoggerFactory.getLogger(ApiTestsPropertiesResolver)

    /**
     * Returns properties file for DP API tests. When not set, 'dp.properties' is default.
     *
     * This will be set in apiTests Spacelift gradle task as a property.
     *
     * @return properties file for DP API tests
     */
    static String resolve() {

        // in case we want to override template dp file
        String dpPropertiesSystemProperty = System.getProperty("dp.properties")

        if (dpPropertiesSystemProperty) {
            logger.info("Going to use ${dpPropertiesSystemProperty} file for API tests")
            return dpPropertiesSystemProperty
        }

        File rootDir = (File) ProjectHelper.project.rootDir

        File templateDpProperties = new File(rootDir, "configuration/api-tests/template_dp.properties")

        String tomcatIP
        String mariadbIP
        String activemqIP
        String mongodbIP

        if (PropertyResolver.resolveDockerMode() == MACHINE.name()) {
            try {
                tomcatIP = mariadbIP = activemqIP = mongodbIP = getIp(resolveDockerMachine())
            } catch (InspectionException ex) {
                throw new InspectionException("Unable to get IP of the container for dp.properties file", ex)
            }
        } else {
            try {
                tomcatIP = resolveContainerIP("tomcat")
                mariadbIP = resolveContainerIP("mariadb")
                activemqIP = resolveContainerIP("activemq")
                mongodbIP = resolveContainerIP("mongodb")
            } catch (InspectionException ex) {
                throw new InspectionException("Unable to get IP of the container for dp.properties file", ex)
            }
        }

        File output = Spacelift.task(templateDpProperties, ProcessTemplate)
                .bindings(["tomcat.ip": tomcatIP])
                .bindings(["mariadb.ip": mariadbIP])
                .bindings(["activemq.ip": activemqIP])
                .bindings(["mongodb.ip": mongodbIP])
                .execute().await()

        logger.info("Going to use this dp.properties file for API tests")
        logger.info(output.text)

        output.absolutePath
    }
}
