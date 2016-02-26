package travel.snapshot.qa.installation

import groovy.transform.CompileStatic
import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.*
import org.arquillian.spacelift.gradle.git.GitCheckoutTask
import org.arquillian.spacelift.gradle.git.GitCloneTask
import org.arquillian.spacelift.task.DefaultGradleTask
import org.arquillian.spacelift.task.TaskRegistry
import org.slf4j.Logger

@CompileStatic
class GitBasedInstallation extends BaseContainerizableObject<GitBasedInstallation> implements Installation {

    DeferredValue<String> product = DeferredValue.of(String).from("unused")

    DeferredValue<String> version = DeferredValue.of(String).from("unused")

    DeferredValue<String> repository = DeferredValue.of(String)

    DeferredValue<String> commit = DeferredValue.of(String).from("master")

    DeferredValue<Boolean> checkoutCommit = DeferredValue.of(Boolean).from(false)

    // represents directory where installation is extracted to
    DeferredValue<File> home = DeferredValue.of(File)

    DeferredValue<Boolean> isInstalled = DeferredValue.of(Boolean).from({

        boolean isInstalled = getHome().exists()

        if (isInstalled && getCheckoutCommit()) {
            Spacelift.task(getHome(), GitCheckoutTask).checkout(getCommit()).execute().await()
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
        String home = getHome()
        String commit = getCommit()

        logger.info(":install:${name} Cloning git repository from ${repository} to ${home}")
        Spacelift.task(repository, GitCloneTask).destination(home).execute().await()

        if (getCheckoutCommit()) {
            logger.info(":install:${name} Checking out commit ${getCommit()} for ${home}")
            Spacelift.task(home, GitCheckoutTask).checkout(commit).execute().await()
        }
    }

    @Override
    void registerTools(TaskRegistry registry) {
        ((Iterable<GradleTask>) tools).each { GradleTask task ->
            Spacelift.registry().register(task.factory())
        }
    }
}
