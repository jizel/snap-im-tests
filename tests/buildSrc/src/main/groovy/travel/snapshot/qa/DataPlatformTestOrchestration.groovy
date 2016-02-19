package travel.snapshot.qa

import travel.snapshot.qa.docker.manager.DockerServiceManager
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration

/**
 * Wrapper for DataPlatformOrchestration which can start and stop containers.
 */
class DataPlatformTestOrchestration {

    private final def orchestration = new DataPlatformOrchestration()

    private final def dockerManagers = []

    private final def serviceCubePairs = []

    private boolean dockerManagersInitialized = false

    private boolean started = false

    /**
     * Starts all added Docker service managers, previously added by 'with' method.
     *
     * @return this
     */
    def start() {
        if (!started) {
            serviceCubePairs.addAll(orchestration.startServices())
            started = true
        }

        this
    }

    /**
     * Stops all added Docker service managers, previously added by 'with' method and started by 'start'.
     *
     * @return this
     */
    def stop() {
        if (started) {
            orchestration.stopServices(serviceCubePairs)
            orchestration.stop()

            serviceCubePairs.clear()
            dockerManagers.clear()

            started = false
            dockerManagersInitialized = false
        }
        this
    }

    /**
     * Adds some Docker service manager to be managed by orchestration.
     *
     * @param dockerServiceManager Docker service manager to manage
     * @return this
     */
    def with(DockerServiceManager dockerServiceManager) {
        dockerManagers << dockerServiceManager
        this
    }

    /**
     * Initializes Docker managers but does not actually start them. Initialization basically starts Arquillian's
     * Manager and, among many other things, reads arquillian.xml file with properties expected to be set and expanded.
     *
     * @return this
     */
    def initDockerManagers() {
        if (!dockerManagersInitialized) {
            init()
            orchestration.with(*dockerManagers)
            dockerManagersInitialized = true
        }

        this
    }

    /**
     * Initializes Arquillian Cube / starts its manager.
     *
     * @return this
     */
    private def init() {
        orchestration.start()
        this
    }

    /**
     *
     * @return instance of DataPlatformOrchestration
     */
    def get() {
        orchestration
    }
}
