package travel.snapshot.qa.installation

import org.arquillian.spacelift.gradle.BaseContainerizableObject
import org.arquillian.spacelift.gradle.DeferredValue
import org.arquillian.spacelift.gradle.Installation
import org.arquillian.spacelift.task.TaskRegistry
import org.slf4j.Logger
import travel.snapshot.qa.DataPlatformTestOrchestration

class DockerService extends BaseContainerizableObject<DockerService> implements Installation {

    DeferredValue<DataPlatformTestOrchestration> setup = DeferredValue.of(DataPlatformTestOrchestration)

    DeferredValue<Boolean> isInstalled = DeferredValue.of(Boolean).from({ false })

    DeferredValue<String> product = DeferredValue.of(String).from(getProduct())

    DeferredValue<String> version = DeferredValue.of(String).from(getVersion())

    DeferredValue<File> home = DeferredValue.of(File).from(name)

    DeferredValue<String> fileName = DeferredValue.of(String).from(name)

    DeferredValue<Void> postActions = DeferredValue.of(Void)

    DockerService(String name, Object parent) {
        super(name, parent)
    }

    DockerService(String name, DockerService other) {
        super(name, other)

        this.isInstalled = other.@isInstalled.copy()
        this.product = other.@product.copy()
        this.version = other.@version.copy()
        this.home = other.@home.copy()
        this.fileName = other.@fileName.copy()
        this.postActions = other.@postActions.copy()
    }

    @Override
    String getProduct() {
        "dockerService"
    }

    @Override
    String getVersion() {
        "unspecified"
    }

    @Override
    File getHome() {
        home.resolve()
    }

    @Override
    boolean isInstalled() {
        false
    }

    @Override
    void install(Logger logger) {
        // this will effectivelly add service into the platform from the setup closure
        setup.resolve()
    }

    @Override
    void registerTools(TaskRegistry registry) {

    }

    @Override
    DockerService clone(String name) {
        new DockerService(name, this)
    }
}
