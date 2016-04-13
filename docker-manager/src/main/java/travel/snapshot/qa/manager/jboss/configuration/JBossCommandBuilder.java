package travel.snapshot.qa.manager.jboss.configuration;

import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.CommandBuilder;

import java.io.File;

public class JBossCommandBuilder {

    public Command build(JBossManagerConfiguration configuration) throws Exception {

        validate(configuration);

        if (configuration.isDomain()) {
            return buildDomainCommand(configuration);
        } else {
            return buildStandaloneCommand(configuration);
        }
    }

    private Command buildStandaloneCommand(JBossManagerConfiguration configuration) {

        final CommandBuilder cb = new CommandBuilder(configuration.getJVM().getJavaBin());

        cb.parameter("-D[Standalone]");
        cb.parameters(configuration.getJavaOpts());
        cb.parameter("-Dorg.jboss.boot.log.file=" + configuration.getJBossLogDir() + "/server.log");
        cb.parameter("-Dlogging.configuration=file:" + configuration.getJBossConfigDir() + "/logging.properties");

        if (new File(configuration.getJBossModuleDir() + "/bundles").exists()) {
            cb.parameter("-Djboss.bundles.dir=" + configuration.getJBossModuleDir() + "/bundles");
        }

        cb.parameters("-jar", configuration.getJVM().getJBossHome() + "/jboss-modules.jar");
        cb.parameters("-mp", configuration.getJBossModuleDir());
        cb.parameters("-jaxpmodule", "javax.xml.jaxp-provider");
        cb.parameters("org.jboss.as.standalone");
        cb.parameter("-Djboss.home.dir=" + configuration.getJVM().getJBossHome());
        cb.parameter("-Djboss.server.base.dir=" + configuration.getJBossBaseDir());
        cb.parameters(configuration.getServerJavaOpts());

        return cb.build();
    }

    private Command buildDomainCommand(JBossManagerConfiguration configuration) {

        final CommandBuilder cb = new CommandBuilder(configuration.getJVM().getJavaBin());

        cb.parameter("-D[Process Controller]");
        cb.parameters(configuration.getProcessControllerJavaOpts());
        cb.parameter("-Dorg.jboss.boot.log.file=" + configuration.getJBossLogDir() + "/process-controller.log");
        cb.parameter("-Dlogging.configuration=file:" + configuration.getJBossConfigDir() + "/logging.properties");

        if (new File(configuration.getJBossModuleDir() + "/bundles").exists()) {
            cb.parameter("-Djboss.bundles.dir=" + configuration.getJBossModuleDir() + "/bundles");
        }

        cb.parameter("-jar")
                .parameter(configuration.getJVM().getJBossHome() + "/jboss-modules.jar");
        cb.parameter("-mp")
                .parameter(configuration.getJBossModuleDir());
        cb.parameter("org.jboss.as.process-controller");
        cb.parameter("-jboss-home")
                .parameter(configuration.getJVM().getJBossHome());
        cb.parameter("-jvm")
                .parameter(configuration.getJVM().getJavaBin());
        cb.parameter("-mp")
                .parameter(configuration.getJBossModuleDir());
        cb.parameter("--");
        cb.parameter("-Dorg.jboss.boot.log.file=" + configuration.getJBossLogDir() + "/host-controller.log");
        cb.parameter("-Dlogging.configuration=file:" + configuration.getJBossConfigDir() + "/logging.properties");
        cb.parameters(configuration.getHostControllerJavaOpts());
        cb.parameter("--");
        cb.parameters("-default-jvm", configuration.getJVM().getJavaBin());

        return cb.build();
    }

    private void validate(JBossManagerConfiguration configuration) throws RuntimeException {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration set to JBossCommandBuilder is null.");
        }

        if (configuration.getJVM().getJBossHome() == null) {
            throw new IllegalStateException("JBOSS_HOME is set to null for JBossCommandBuilder");
        }
    }
}
