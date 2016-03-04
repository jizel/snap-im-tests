package travel.snapshot.qa.installation

import groovy.transform.CompileStatic
import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.*
import org.arquillian.spacelift.gradle.git.GitCheckoutTask
import org.arquillian.spacelift.gradle.git.GitCloneTask
import org.arquillian.spacelift.gradle.git.GitFetchTask
import org.arquillian.spacelift.gradle.git.GitRevParseTask
import org.arquillian.spacelift.task.DefaultGradleTask
import org.arquillian.spacelift.task.TaskRegistry
import org.slf4j.Logger

import static java.lang.Boolean.FALSE

@CompileStatic
class GitBasedInstallation extends BaseContainerizableObject<GitBasedInstallation> implements Installation {

    DeferredValue<String> product = DeferredValue.of(String).from("unused")

    DeferredValue<String> version = DeferredValue.of(String).from("unused")

    DeferredValue<String> repository = DeferredValue.of(String)

    DeferredValue<String> commit = DeferredValue.of(String).from("master")

    DeferredValue<Boolean> checkoutCommit = DeferredValue.of(Boolean).from(FALSE)

    // represents directory where installation is extracted to
    DeferredValue<File> home = DeferredValue.of(File)

    DeferredValue<Boolean> isInstalled = DeferredValue.of(Boolean).from({

        boolean isInstalled = getHome().exists()

        if (isInstalled && getCheckoutCommit()) {

            // get commit sha
            String commitSha = getCommit()

            // if we checked out a commit, this should work
            String repositorySha = Spacelift.task(getHome(), GitRevParseTask).rev("HEAD").execute().await()
            if (repositorySha && (repositorySha == commitSha)) {
                return true
            }

            // if we checkout out master or a reference, make sure that we fetch latest first
            Spacelift.task(getHome(), GitFetchTask).execute().await()
            def originRepositorySha = Spacelift.task(getHome(), GitRevParseTask).rev("origin/${commitSha}").execute().await()
            // ensure that content is the same
            if (repositorySha && originRepositorySha && (repositorySha == originRepositorySha)) {
                return true
            }

            return false
        }

        isInstalled
    })

    // tools provided by this installation
    InheritanceAwareContainer<GradleTask, DefaultGradleTask> tools

    GitBasedInstallation(String name, Object parent) {
        super(name, parent)

        this.tools = new InheritanceAwareContainer(this, GradleTask, DefaultGradleTask)
    }

    GitBasedInstallation(String name, GitBasedInstallation other) {
        super(name, other)

        this.product = other.@product.copy()
        this.version = other.@product.copy()
        this.repository = other.@repository.copy()
        this.commit = other.@commit.copy()
        this.checkoutCommit = other.@checkoutCommit.copy()
        this.home = other.@home.copy()
        this.isInstalled = other.@isInstalled.copy()
        this.tools = (InheritanceAwareContainer<GradleTask, DefaultGradleTask>) other.@tools.clone()
    }

    @Override
    GitBasedInstallation clone(String name) {
        new GitBasedInstallation(name, this)
    }

    @Override
    String getVersion() {
        version.resolve()
    }

    @Override
    String getProduct() {
        product.resolve()
    }

    @Override
    File getHome() {
        home.resolve()
    }

    @Override
    public boolean isInstalled() {
        isInstalled.resolve()
    }

    String getRepository() {
        repository.resolve()
    }

    String getCommit() {
        commit.resolve()
    }

    Boolean getCheckoutCommit() {
        checkoutCommit.resolve()
    }

    @Override
    void install(Logger logger) {

        String repository = getRepository()
        File home = getHome()
        String commit = getCommit()

        if (!home.exists()) {
            logger.info(":install:${name} Cloning git repository from ${repository} to ${home}")
            home = Spacelift.task(repository, GitCloneTask).destination(home).execute().await()
        }

        if (getCheckoutCommit()) {
            logger.info(":install:${name} Identified existing git installation at ${home}, will fetch latest content.")
            Spacelift.task(home, GitFetchTask).execute().await()

            // checkout commit
            logger.info(":install:${name} Force checking out ${commit} at ${home}.")
            Spacelift.task(home, GitCheckoutTask).checkout(commit).force().execute().await()
        }
    }

    @Override
    void registerTools(TaskRegistry registry) {
        ((Iterable<GradleTask>) tools).each { GradleTask task ->
            Spacelift.registry().register(task.factory())
        }
    }
}
