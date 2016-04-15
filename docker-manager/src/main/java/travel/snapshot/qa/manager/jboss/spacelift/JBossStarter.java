package travel.snapshot.qa.manager.jboss.spacelift;

import org.arquillian.spacelift.task.Task;
import travel.snapshot.qa.manager.jboss.configuration.JBossManagerConfiguration;

/**
 * Base class for both standalone and domain starters.
 *
 * @param <T> manager this starter will return to caller - either {@link travel.snapshot.qa.manager.jboss.JBossStandaloneManager}
 *            or {@link travel.snapshot.qa.manager.jboss.JBossDomainManager}.
 */
public abstract class JBossStarter<T> extends Task<JBossManagerConfiguration, T> {

    protected JBossManagerConfiguration configuration;

    public JBossStarter() {
        configuration = new JBossManagerConfiguration.Builder().build();
    }

    public JBossStarter<T> configuration(JBossManagerConfiguration configuration) {
        setConfiguration(configuration);
        return this;
    }

    protected void setConfiguration(JBossManagerConfiguration configuration) {
        if (configuration != null) {
            this.configuration = configuration;
        }
    }
}
