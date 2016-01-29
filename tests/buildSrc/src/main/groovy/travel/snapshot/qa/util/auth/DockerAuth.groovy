package travel.snapshot.qa.util.auth

import org.arquillian.spacelift.Spacelift

class DockerAuth {

    static def loginToDockerRegistry(String password, String username, String email, String repository) {
        Spacelift.task("docker").parameters("login", "-p", password, "-u", username, "-e", email, repository).execute().await()
    }
}
