package travel.snapshot.qa.manager.tomcat.command.deployment.lifecycle;

public abstract class TomcatDeploymentLifecycleCommand {

    protected String deployment;

    public TomcatDeploymentLifecycleCommand(String deployment) {
        if (!deployment.startsWith("/")) {
            deployment = "/" + deployment;
        }
        this.deployment = deployment;
    }
}
