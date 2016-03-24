package travel.snapshot.qa.installation

import org.arquillian.spacelift.gradle.BaseContainerizableObject
import org.arquillian.spacelift.gradle.DeferredValue
import org.arquillian.spacelift.gradle.Installation
import org.arquillian.spacelift.task.TaskRegistry
import org.slf4j.Logger
import travel.snapshot.qa.DataPlatformTestOrchestration
import travel.snapshot.qa.util.DockerMode
import travel.snapshot.qa.util.DockerToolsRegister
import travel.snapshot.qa.util.ProjectHelper
import travel.snapshot.qa.util.Properties
import travel.snapshot.qa.util.container.DockerContainer
import travel.snapshot.qa.util.image.DockerImagesDowloader

import static java.lang.Boolean.FALSE

class Docker extends BaseContainerizableObject<Docker> implements Installation {

    DeferredValue<DataPlatformTestOrchestration> setup = DeferredValue.of(DataPlatformTestOrchestration)

    DeferredValue<Boolean> isInstalled = DeferredValue.of(Boolean).from(FALSE)

    DeferredValue<String> product = DeferredValue.of(String).from(getProduct())

    DeferredValue<String> version = DeferredValue.of(String).from(getVersion())

    DeferredValue<File> home = DeferredValue.of(File).from(name)

    DeferredValue<String> fileName = DeferredValue.of(String).from(name)

    DeferredValue<Void> postActions = DeferredValue.of(Void)

    DeferredValue<String> dockerRegistryPassword = DeferredValue.of(String).from({
        ProjectHelper.spacelift.configuration['dockerRegistryPassword'].value
    })

    DeferredValue<List> images = DeferredValue.of(List).from({
        ProjectHelper.spacelift.configuration['dockerImages'].value
    })

    DeferredValue<List> containers = DeferredValue.of(List).from({
        ProjectHelper.spacelift.configuration['serviceInstallations'].value
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
        isInstalled.resolve()
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

        System.setProperty("arquillian.xml.docker.scheme", "tcp")
        System.setProperty("arquillian.xml.docker.host", "localhost")
        System.setProperty("arquillian.xml.docker.port", "2375")

        System.setProperty("arquillian.xml.docker.machine", Properties.Docker.machineName)
        System.setProperty("arquillian.xml.connection.mode", Properties.Docker.connectionMode)
        System.setProperty("arquillian.xml.docker.registry.password", Properties.Docker.registryPassword)

        // in case we do not use VM, config directory will be automatically mounted to respective container transparently
        System.setProperty("arquillian.xml.data.tomcat.config.dir", Properties.Tomcat.springConfigDirectoryMount)
        System.setProperty("arquillian.xml.deployments.mount", Properties.Tomcat.deploymentDirectoryBind)

        // In case we run in HOST mode, this is empty so IP of the container itself will be resolved
        System.setProperty("arquillian.xml.java.rmi.server.hostname", "")

        // if Docker mode is 'machine', we will download images in Docker Machine installation
        // into the Docker's VM so we prevent from downloading them twice

        // there is not need to download images when we just want to stop containers

        if (Properties.Docker.mode == DockerMode.HOST.toString() && !ProjectHelper.isProfileSelected("platformStop")) {
            DockerImagesDowloader.download(images.resolve(), dockerRegistryPassword.resolve())
            DockerContainer.removeContainers(containers.resolve())
        }

        setup.resolve()

        // post action closure for custom actions after Docker installation is done

        postActions.resolve()
    }

    @Override
    void registerTools(TaskRegistry registry) {
        // this is executed before install method
        new DockerToolsRegister().register(registry)
    }

    @Override
    Docker clone(String name) {
        new Docker(name, this)
    }
}

