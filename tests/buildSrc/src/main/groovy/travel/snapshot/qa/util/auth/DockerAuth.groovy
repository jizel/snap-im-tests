package travel.snapshot.qa.util.auth

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.process.ProcessResult

/**
 * Aggregates helper methods related to authentication agains Docker
 */
class DockerAuth {

    /**
     * Authenticates against Docker repository
     *
     * @param password password for login Docker repository
     * @param username username for login to Docker repository
     * @param email email for login to Docker repository
     * @param repository repository to authenticate against
     * @return result of the execution
     */
    static ProcessResult loginToDockerRegistry(String password, String username, String email, String repository) {
        Spacelift.task("docker").parameters("login", "-p", password, "-u", username, "-e", email, repository).execute().await()
    }
}
