package travel.snapshot.qa.docker.manager;

/**
 * Connection Mode to bypass the Create/Start Cube commands if the a Docker Container with the same name is already
 * running on the target system.
 */
public enum ConnectionMode {

    /**
     * Default connection mode if not set. Simply creates and stops all Docker Containers. If a container is already
     * running, an exception is thrown.
     */
    STARTANDSTOP,

    /**
     * Tries to bypass the Create/Start Cube commands if a container with the same name is already running, and if it is
     * the case it does not stop it at the end. But if container is not already running, Cube will start one and stop it
     * at the end of the execution.
     */
    STARTORCONNECT,

    /**
     * Exactly the same as STARTORCONNECT but if container is started by Cube it will not be stopped at the end of the
     * execution so it can be reused in next executions.
     */
    STARTORCONNECTANDLEAVE;
}
