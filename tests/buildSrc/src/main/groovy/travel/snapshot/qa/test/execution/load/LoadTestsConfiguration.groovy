package travel.snapshot.qa.test.execution.load

import travel.snapshot.qa.util.PropertyResolver

class LoadTestsConfiguration {

    static final int DEFAULT_START_USERS = 10

    static final int DEFAULT_END_USERS = 60

    static final int DEFAUT_RAMP = 120

    private LoadTestEnvironment environment

    private LoadTestsSimulation simulation

    private int startUsers = DEFAULT_START_USERS

    private int endUsers = DEFAULT_END_USERS

    private int ramp = DEFAUT_RAMP

    static LoadTestsConfiguration parseLoadTestsConfiguration() {

        LoadTestEnvironment environment = PropertyResolver.resolveLoadTestEnvironment()

        int startUsers = PropertyResolver.resolveLoadTestStartUsers()
        int endUsers = PropertyResolver.resolveLoadTestEndUsers()
        int ramp = PropertyResolver.resolveLoadTestRamp()

        LoadTestsConfiguration configuration = new LoadTestsConfiguration()
                .environment(environment)
                .startUsers(startUsers)
                .endUsers(endUsers)
                .ramp(ramp)

        configuration
    }

    static List<List<LoadTestsSimulation, LoadTestsConfiguration>> parse() {

        List<LoadTestsSimulation> simulations = PropertyResolver.resolveLoadTestSimulations()
        LoadTestsConfiguration configuration = parseLoadTestsConfiguration()

        List<List<LoadTestsSimulation, LoadTestsConfiguration>> configurations = []

        for (LoadTestsSimulation simulation : simulations) {
            configurations << [simulation, configuration]
        }

        configurations
    }

    LoadTestsConfiguration environment(LoadTestEnvironment environment) {
        this.environment = environment
        this
    }

    LoadTestsConfiguration simulation(LoadTestsSimulation simulation) {
        this.simulation = simulation
        this
    }

    LoadTestsConfiguration startUsers(int startUsers) {
        this.startUsers = startUsers
        this
    }

    LoadTestsConfiguration endUsers(int endUsers) {
        this.endUsers = endUsers
        this
    }

    LoadTestsConfiguration ramp(int ramp) {
        this.ramp = ramp
        this
    }

    LoadTestEnvironment getEnvironment() {
        environment
    }

    LoadTestsSimulation getSimulation() {
        simulation
    }

    int getStartUsers() {
        startUsers
    }

    int getEndUsers() {
        endUsers
    }

    int getRamp() {
        ramp
    }

    @Override
    String toString() {
        "environment: ${environment}, simulation: ${simulation}, startUsers: ${startUsers}, endUsers: ${endUsers}, ramp: ${ramp}"
    }
}
