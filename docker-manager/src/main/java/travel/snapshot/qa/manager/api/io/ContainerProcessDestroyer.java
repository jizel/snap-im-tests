package travel.snapshot.qa.manager.api.io;

import travel.snapshot.qa.manager.api.container.ContainerManagerException;

public class ContainerProcessDestroyer {

    private Process process;

    private Thread shutdownThread;

    public ContainerProcessDestroyer(Process process) {
        this.process = process;
    }

    public ContainerProcessDestroyer shutdownThread(Thread shutdownThread) {
        this.shutdownThread = shutdownThread;
        return this;
    }

    public void destroy() throws ContainerManagerException {
        if (shutdownThread != null) {
            Runtime.getRuntime().removeShutdownHook(shutdownThread);
            shutdownThread = null;
        }

        try {
            if (process != null) {
                process.destroy();
                process.waitFor();
                process = null;
            }
        } catch (Exception ex) {
            throw new ContainerManagerException("Could not stop container.", ex);
        }
    }
}
