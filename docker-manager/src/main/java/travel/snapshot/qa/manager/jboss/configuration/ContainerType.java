package travel.snapshot.qa.manager.jboss.configuration;

import org.arquillian.spacelift.Spacelift;

import java.util.ArrayList;
import java.util.List;

public enum ContainerType {

    EAP {
        @Override
        public List<String> javaOptions(JBossManagerConfiguration jboss) {

            List<String> opts = new ArrayList<>();

            if (!jboss.isDomain()) {
                opts.add("-server");
                opts.add("-XX:+UseCompressedOops");
                opts.add("-verbose:gc");
                opts.add("-Xloggc:" + jboss.getJBossLogDir() + "/gc.log");
                opts.add("-XX:+PrintGCDetails");
                opts.add("-XX:+PrintGCDateStamps");
                opts.add("-XX:+UseGCLogFileRotation");
                opts.add("-XX:NumberOfGCLogFiles=5");
                opts.add("-XX:GCLogFileSize=3M");
                opts.add("-XX:-TraceClassUnloading");
                opts.add("-Xms1303m");
                opts.add("-Xmx1303m");
            } else {
                if (Spacelift.task(jboss.getJVM(), JavaServerOptionCapabilityCheck.class).execute().await()) {
                    opts.add("-server");
                }
                opts.add("-Xms64m");
                opts.add("-Xmx512m");
            }

            opts.add("-XX:MaxPermSize=256m");
            opts.add("-Djava.net.preferIPv4Stack=true");
            opts.add("-Djboss.modules.system.pkgs=org.jboss.byteman");
            opts.add("-Djava.awt.headless=true");
            opts.add("-Djboss.modules.policy-permissions=true");

            if (jboss.isDomain()) {
                opts.add("-Djboss.domain.default.config=" + jboss.getConfiguration().getDomainConfig());
                opts.add("-Djboss.host.default.config=" + jboss.getConfiguration().getHostConfig());
            } else {
                opts.add("-Djboss.server.default.config=" + jboss.getConfiguration().getStandaloneConfig());
            }

            return opts;
        }
    },
    WILDFLY {
        @Override
        public List<String> javaOptions(JBossManagerConfiguration jboss) {

            List<String> opts = new ArrayList<>();

            if (!jboss.isDomain()) {
                opts.add("-server");
            } else if (Spacelift.task(jboss.getJVM(), JavaServerOptionCapabilityCheck.class).execute().await()) {
                opts.add("-server");
            }

            opts.add("-Xms64m");
            opts.add("-Xmx512m");
            opts.add("-XX:MaxPermSize=256m");
            opts.add("-Djava.net.preferIPv4Stack=true");
            opts.add("-Djboss.modules.system.pkgs=org.jboss.byteman");
            opts.add("-Djava.awt.headless=true");

            if (jboss.isDomain()) {
                opts.add("-Djboss.domain.default.config=" + jboss.getConfiguration().getDomainConfig());
                opts.add("-Djboss.host.default.config=" + jboss.getConfiguration().getHostConfig());
            } else {
                opts.add("-Djboss.server.default.config=" + jboss.getConfiguration().getStandaloneConfig());
            }

            return opts;
        }
    };

    public abstract List<String> javaOptions(JBossManagerConfiguration jboss);
}
