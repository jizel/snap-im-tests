package travel.snapshot.qa.manager.jboss.configuration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class JVM {

    private final File javaHome;

    private final File jbossHome;

    private final List<String> javaOpts;

    private JVM(Builder builder) {
        javaHome = builder.javaHome;
        jbossHome = builder.jbossHome;
        javaOpts = builder.javaOpts;
    }

    public List<String> getJavaOpts() {
        return javaOpts;
    }

    public String getJavaHome() {
        try {
            return javaHome.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get canonical path of " + javaHome, e);
        }
    }

    public String getJBossHome() {
        try {
            return jbossHome.getCanonicalPath();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to get canonical path of " + jbossHome, e);
        }
    }

    public String getJavaBin() {
        if (getJavaHome() != null) {
            return getJavaHome() + File.separatorChar + "bin" + File.separatorChar + "java";
        } else {
            return "java";
        }
    }

    public static final class Builder {

        private File javaHome;

        private File jbossHome;

        private List<String> javaOpts = new ArrayList<>();

        public Builder() {
            String javaHome = System.getProperty("java.home");
            if (javaHome == null || javaHome.length() == 0) {
                javaHome = System.getenv("JAVA_HOME");
            }
            if (javaHome != null) {
                this.javaHome = new File(javaHome);
            }

            // get either from system property or env variable
            String jbossHome = System.getProperty("jboss.home");
            if (jbossHome == null || jbossHome.length() == 0) {
                jbossHome = System.getenv("JBOSS_HOME");
            }

            if (jbossHome != null) {
                this.jbossHome = new File(jbossHome);
            }
        }

        public Builder setJavaHome(String javaHome) {
            if (javaHome != null && new File(javaHome).isDirectory()) {
                this.javaHome = new File(javaHome);
            }
            return this;
        }

        public Builder setJBossHome(String jbossHome) {
            if (jbossHome != null && new File(jbossHome).isDirectory()) {
                this.jbossHome = new File(jbossHome);
            }
            return this;
        }

        public Builder setJavaOpts(List<String> javaOpts) {
            this.javaOpts = javaOpts;
            return this;
        }

        public Builder setJavaOpts(String... javaOpts) {
            this.javaOpts.addAll(Arrays.asList(javaOpts));
            return this;
        }

        public JVM build() {

            if (javaHome != null) {
                if (!javaHome.exists() || !javaHome.isDirectory()) {
                    throw new IllegalStateException("javaHome '" + javaHome.getAbsolutePath() + "' must exist!");
                }
            } else {
                throw new IllegalStateException("Could not determine the value of Java home directory.");
            }

            if (jbossHome != null) {
                if (!jbossHome.exists() || !jbossHome.isDirectory()) {
                    throw new IllegalStateException("jbossHome '" + jbossHome.getAbsolutePath() + "' must exist!");
                }
            } else {
                throw new IllegalStateException("Could not determine the value of JBoss home directory.");
            }

            return new JVM(this);
        }
    }
}
