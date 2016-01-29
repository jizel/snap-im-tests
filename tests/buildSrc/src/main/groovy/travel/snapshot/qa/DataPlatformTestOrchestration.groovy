package travel.snapshot.qa

import travel.snapshot.qa.docker.manager.DockerServiceManager
import travel.snapshot.qa.docker.orchestration.DataPlatformOrchestration

class DataPlatformTestOrchestration {

    public def orchestration = new DataPlatformOrchestration()

    private def dockerManagers = []

    private def serviceCubePairs = []

    private boolean dockerManagersInitialized = false

    private boolean started = false

    def start() {
        if (!started) {
            serviceCubePairs.addAll(orchestration.startServices())
            started = true
        }

        this
    }

    def stop() {
        orchestration.stopServices(serviceCubePairs)
        orchestration.stop()

        serviceCubePairs.clear()
        dockerManagers.clear()

        started = false
        dockerManagersInitialized = false

        this
    }

    def with(DockerServiceManager dockerServiceManager) {
        dockerManagers << dockerServiceManager
        this
    }

    def initDockerManagers() {
        if (!dockerManagersInitialized) {
            init()
            orchestration.with(*dockerManagers)
            dockerManagersInitialized = true
        }

        this
    }

    def init() {
        orchestration.start()
        this
    }

    def get() {
        orchestration
    }
}
