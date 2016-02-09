package travel.snapshot.qa.installation

import org.arquillian.spacelift.Spacelift
import org.arquillian.spacelift.gradle.BaseContainerizableObject
import org.arquillian.spacelift.gradle.DeferredValue
import org.arquillian.spacelift.gradle.GradleSpaceliftDelegate
import org.arquillian.spacelift.gradle.Installation
import org.arquillian.spacelift.task.TaskRegistry
import org.slf4j.Logger
import travel.snapshot.qa.util.DockerToolsRegister
import travel.snapshot.qa.util.PropertyResolver
import travel.snapshot.qa.util.container.DockerContainer
import travel.snapshot.qa.util.image.DockerImagesDowloader
import travel.snapshot.qa.util.interaction.DockerInteraction
import travel.snapshot.qa.util.machine.DockerMachineHelper

class DockerMachine extends BaseContainerizableObject<DockerMachine> implements Installation {

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

    DeferredValue<String> dockerMachine = DeferredValue.of(String).from({
        new GradleSpaceliftDelegate().project().spacelift.configuration['dockerMachine'].value
    })

    DeferredValue<List> containers = DeferredValue.of(List).from({
        new GradleSpaceliftDelegate().project().spacelift.configuration['serviceInstallations'].value
    })

    DockerMachine(String name, Object parent) {
        super(name, parent)
    }

    DockerMachine(String name, DockerMachine other) {
        super(name, other)

        this.isInstalled = other.@isInstalled.copy()
        this.product = other.@product.copy()
        this.version = other.@version.copy()
        this.home = other.@home.copy()
        this.fileName = other.@fileName.copy()
        this.postActions = other.@postActions.copy()

        this.dockerRegistryPassword = other.@dockerRegistryPassword.copy()
        this.images = other.@images.copy()
        this.dockerMachine = other.@dockerMachine.copy()
        this.containers = other.@containers.copy()
    }

    @Override
    String getProduct() {
        "dockerMachine"
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

        if (PropertyResolver.resolveDockerMode() != "machine") {
            logger.info(":install:${name} Skipping, did not meet preconditions.")
            return
        }

        String dockerMachine = dockerMachine.resolve()

        if (!dockerMachine) {
            dockerMachine = "default"
        }

        if (!DockerMachineHelper.isPresent(dockerMachine)) {
            DockerMachineHelper.create(dockerMachine)
        }

        if (!DockerMachineHelper.isRunning(dockerMachine)) {
            DockerMachineHelper.start(dockerMachine)
        }

        // here we have to re-register tools because from now on
        // we will talk to Docker daemon running in virtual machine and not
        // with the one running locally, this basically emulates
        // 'eval "$(docker-machine env default)"'

        def environmentProperties = DockerMachineHelper.getEnvironmentProperties(dockerMachine)

        new DockerToolsRegister().register(Spacelift.registry(), environmentProperties)

        // here we will download images into Docker machine instead locally
        // there is not need to download images to VM when we just want to stop containers

        def selectedProfile = new GradleSpaceliftDelegate().project().selectedProfile['name']

        if (selectedProfile != "platformStop") {
            DockerImagesDowloader.download(images.resolve(), dockerRegistryPassword.resolve())
            DockerContainer.removeContainers(containers.resolve())
        }

        // here we set docker.host system property which is eventually picked by Docker Manager
        // so service managers for Docker will talk to the right services running in Docker machine
        // when service installations resolve their 'setup' closures after this property is set,
        // they will internally be set to talk to Docker machine out of the box

        System.setProperty("docker.host", DockerMachineHelper.getIp(dockerMachine))

        // This is set here so when Arquillian gets involved with arquillian.xml file, they will be
        // expanded in 'serverUri' property

        System.setProperty("arquillian.xml.docker.scheme", "https")
        System.setProperty("arquillian.xml.docker.host", "dockerHost")
        System.setProperty("arquillian.xml.docker.port", "2376")

        System.setProperty("arquillian.xml.docker.machine", dockerMachine)

        // in case we run on VM, we need to copy configs to VM and set property from where configs will be picked
        // this is set here to "/home/docker/configuration/tomcat" but that directory will be in virtual machine so we
        // need to be sure that it exists

        DockerInteraction.execute("rm -rf /home/docker/configuration")
        DockerInteraction.execute("mkdir -p /home/docker/configuration")

        System.setProperty("arquillian.xml.data.tomcat.config.dir", "/home/docker/configuration/tomcat")
    }

    @Override
    void registerTools(TaskRegistry registry) {

    }

    @Override
    DockerMachine clone(String name) {
        new Docker(name, this)
    }
}