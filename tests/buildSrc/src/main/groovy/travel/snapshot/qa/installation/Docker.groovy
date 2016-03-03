package travel.snapshot.qa.installation

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.BaseContainerizableObject
import org.arquillian.spacelift.gradle.DeferredValue
import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.arquillian.spacelift.gradle.Installation
import org.arquillian.spacelift.task.TaskRegistry
import org.slf4j.Logger
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.util.DockerMode
import travel.snapshot.qa.util.DockerToolsRegister
import travel.snapshot.qa.util.PropertyResolver
import travel.snapshot.qa.util.container.DockerContainer
import travel.snapshot.qa.util.image.DockerImagesDowloader

class Docker extends BaseContainerizableObject<Docker> implements Installation {

    DeferredValue<DataPlatformTestOrchestration> setup = DeferredValue.of(DataPlatformTestOrchestration)

    DeferredValue<Boolean> isInstalled = DeferredValue.of(Boolean).from({ false })

    DeferredValue<String> product = DeferredValue.of(String).from(getProduct())

    DeferredValue<String> version = DeferredValue.of(String).from(getVersion())

    DeferredValue<File> home = DeferredValue.of(File).from(name)

    DeferredValue<String> fileName = DeferredValue.of(String).from(name)

    DeferredValue<Void> postActions = DeferredValue.of(Void)

    DeferredValue<String> dockerRegistryPassword = DeferredValue.of(String).from({
        new GradleSpaceliftDelegate().project().spacelift.configuration['dockerRegistryPassword'].value
    })

    DeferredValue<List> images = DeferredValue.of(List).from({
        new GradleSpaceliftDelegate().project().spacelift.configuration['dockerImages'].value
    })

    DeferredValue<List> containers = DeferredValue.of(List).from({
        new GradleSpaceliftDelegate().project().spacelift.configuration['serviceInstallations'].value
    })

    Docker(String name, Object parent) {
        super(name, parent)
    }

    Docker(String name, Docker other) {
        super(name, other)

        this.isInstalled = other.@isInstalled.copy()
        this.product = other.@product.copy()
        this.version = other.@version.copy()
        this.home = other.@home.copy()
        this.fileName = other.@fileName.copy()
        this.postActions = other.@postActions.copy()

        this.dockerRegistryPassword = other.@dockerRegistryPassword.copy()
        this.images = other.@images.copy()
        this.containers = other.@containers.copy()
    }

    @Override
    String getProduct() {
        "docker"
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

        // here we explicitly set docker.host system property which is eventually picked by Docker Manager
        // so service managers for Docker will talk to the right services running in Docker containers present locally.
        // when service installations resolve their 'setup' closures after this property is set,
        // they will internally be set to talk to Docker containers running locally automatically

        System.setProperty("docker.host", "127.0.0.1")

        // This is set here so when Arquillian gets involved with arquillian.xml file, they will be
        // expanded in 'serverUri' property

        System.setProperty("arquillian.xml.docker.scheme", "http")
        System.setProperty("arquillian.xml.docker.host", "localhost")
        System.setProperty("arquillian.xml.docker.port", "2375")

        System.setProperty("arquillian.xml.docker.machine", PropertyResolver.resolveDockerMachine())
        System.setProperty("arquillian.xml.connection.mode", PropertyResolver.resolveConnectionMode())
        System.setProperty("arquillian.xml.docker.registry.password", PropertyResolver.resolveDockerRegistryPassword())

        // in case we do not use VM, config directory will be automatically mounted to respective container transparently
        System.setProperty("arquillian.xml.data.tomcat.config.dir", PropertyResolver.resolveTomcatSpringConfigDirectoryMount())
        System.setProperty("arquillian.xml.deployments.mount", PropertyResolver.resolveTomcatDeploymentDirectoryBind())

        // In case we run in HOST mode, this is empty so IP of the container itself will be resolved
        System.setProperty("arquillian.xml.java.rmi.server.hostname", "")

        // if Docker mode is 'machine', we will download images in Docker Machine installation
        // into the Docker's VM so we prevent from downloading them twice

        // there is not need to download images when we just want to stop containers

        def selectedProfile = new GradleSpaceliftDelegate().project().selectedProfile['name']

        if (PropertyResolver.resolveDockerMode() == DockerMode.HOST.toString() && selectedProfile != "platformStop") {

            // registering tools in registerTools method in this class is too late for us
            // tools are registered after the installation has completed

            new DockerToolsRegister().register(Spacelift.registry())

            DockerImagesDowloader.download(images.resolve(), dockerRegistryPassword.resolve())

            DockerContainer.removeContainers(containers.resolve())
        }

        setup.resolve()
    }

    @Override
    void registerTools(TaskRegistry registry) {
        new DockerToolsRegister().register(registry)
    }

    @Override
    Docker clone(String name) {
        new Docker(name, this)
    }
}
