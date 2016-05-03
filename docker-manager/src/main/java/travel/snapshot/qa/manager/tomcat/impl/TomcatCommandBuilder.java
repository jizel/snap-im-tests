package travel.snapshot.qa.manager.tomcat.impl;

import org.arquillian.spacelift.process.Command;
import org.arquillian.spacelift.process.CommandBuilder;
import travel.snapshot.qa.manager.tomcat.configuration.TomcatManagerConfiguration;
import travel.snapshot.qa.manager.tomcat.util.AdditionalJavaOptionsParser;

import java.io.File;

/**
 * Builds a string representation of a command by which Tomcat container is started.
 */
public class TomcatCommandBuilder {

    public Command build(TomcatManagerConfiguration configuration) {

        final File CATALINA_HOME = configuration.getCatalinaHome();
        final File CATALINA_BASE = configuration.getCatalinaBase();
        final String ADDITIONAL_JAVA_OPTS = configuration.getJavaVmArguments();

        final String absoluteCatalinaHomePath = CATALINA_HOME.getAbsolutePath();
        final String absoluteCatalinaBasePath = CATALINA_BASE.getAbsolutePath();

        final CommandBuilder cb = new CommandBuilder(configuration.getJavaBin());

        cb.parameter("-Djava.util.logging.config.file=" + absoluteCatalinaBasePath + "/conf/" + configuration.getLoggingProperties());

        cb.parameter("-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager");
        cb.parameters(new AdditionalJavaOptionsParser().parse(ADDITIONAL_JAVA_OPTS));
        cb.parameter("-classpath");

        String CLASS_PATH = absoluteCatalinaHomePath + "/bin/bootstrap.jar" + System.getProperty("path.separator");
        CLASS_PATH += absoluteCatalinaHomePath + "/bin/tomcat-juli.jar";
        cb.parameter(CLASS_PATH);

        cb.parameter("-Djava.endorsed.dirs=" + absoluteCatalinaHomePath + "/endorsed");
        cb.parameter("-Dcatalina.base=" + absoluteCatalinaBasePath);
        cb.parameter("-Dcatalina.home=" + absoluteCatalinaHomePath);
        cb.parameter("-Djava.io.tmpdir=" + absoluteCatalinaBasePath + "/temp");
        cb.parameter("org.apache.catalina.startup.Bootstrap");
        cb.parameter("-config");
        cb.parameter(absoluteCatalinaBasePath + "/conf/" + configuration.getServerConfig());
        cb.parameter("start");

        return cb.build();
    }

}
